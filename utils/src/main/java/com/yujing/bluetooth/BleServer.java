package com.yujing.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;

import androidx.annotation.RequiresApi;

import com.yujing.contract.YListener1;
import com.yujing.utils.YConvert;
import com.yujing.utils.YLog;
import com.yujing.utils.YThread;

import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * 蓝牙BLE服务，广播
 */
/*
用法：
BleServer bleServer;
bleServer = new BleServer(this);
bleServer.setName("YBle");
bleServer.init();
//连接成功监听
bleServer.setConnectListener(bluetoothDevice -> txtDevice.setText("连接成功"));
//连接断开监听
bleServer.setDisConnectListener(bluetoothDevice -> txtDevice.setText("断开连接"));
//错误监听
bleServer.setErrorListener(s -> txtDevice.setText("错误：" + s));
//读取监听
bleServer.setReadListener(bytes -> txtDevice.setText("数据：" + YConvert.bytesToHexString(bytes)));

//打开蓝牙
bleServer.open();
//打开服务
bleServer.startService();
//广播数据
bleServer.send(YConvert.hexStringToByte("123456789ABCD"));
//停止服务
bleServer.stopService();
//关闭蓝牙
bleServer.close();
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleServer {
    public final static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_LOST_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_LOST_WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_LOST_ENABLE = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private final static String TAG = "BleServer";
    private BluetoothManager mBluetoothManager;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private BluetoothGattServer gattServer;
    private BluetoothGattCharacteristic characterNotify;
    private BluetoothDevice bluetoothDevice;
    BluetoothAdapter bluetoothAdapter;
    Context context;
    String name;

    //连接成功
    private YListener1<BluetoothDevice> connectListener;
    //连接失败
    private YListener1<BluetoothDevice> disConnectListener;
    //接收消息
    private YListener1<byte[]> readListener;
    //错误消息
    private YListener1<String> errorListener;

    public BleServer(Context context) {
        this.context = context;
    }

    /**
     * 初始化，必须调用
     */
    public boolean init() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (errorListener != null) YThread.runOnUiThread(() -> errorListener.value("不支持BLE"));
            return false;
        }
        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            if (errorListener != null) YThread.runOnUiThread(() -> errorListener.value("蓝牙不支持"));
            return false;
        }
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            if (errorListener != null) YThread.runOnUiThread(() -> errorListener.value("未打开蓝牙"));
            return false;
        }
        bluetoothAdapter.setName(name);
        setServer();
        return true;
    }

    //打开蓝牙
    public void open() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    //关闭蓝牙
    public void close() {
        if (bluetoothAdapter != null) bluetoothAdapter.disable();
    }

    //发送数据
    public void send(byte[] data) {
        if (bluetoothDevice == null) {
            if (errorListener != null) YThread.runOnUiThread(() -> errorListener.value("未连接蓝牙设备"));
            return;
        }
        characterNotify.setValue(data);
        gattServer.notifyCharacteristicChanged(bluetoothDevice, characterNotify, false);
    }

    //打开服务
    public void startService() {
        if (bluetoothAdapter == null || mBluetoothLeAdvertiser == null) {
            open();
            if (!init()) return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            if (errorListener != null) YThread.runOnUiThread(() -> errorListener.value("没有打开蓝牙"));
            return;
        }
        mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(), mAdvertiseCallback);
    }

    //关闭服务
    public void stopService() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }

    /**
     * 添加服务，特征
     */
    private void setServer() {
        if (!bluetoothAdapter.isEnabled()) {
            if (errorListener != null) YThread.runOnUiThread(() -> errorListener.value("没有打开蓝牙"));
            return;
        }
        //读写特征
        BluetoothGattCharacteristic characterWrite = new BluetoothGattCharacteristic(UUID_LOST_WRITE, BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        //使能特征
        characterNotify = new BluetoothGattCharacteristic(UUID_LOST_ENABLE, BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ);
        characterNotify.addDescriptor(new BluetoothGattDescriptor(CLIENT_CHARACTERISTIC_CONFIG, BluetoothGattDescriptor.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ));
        //服务
        BluetoothGattService gattService = new BluetoothGattService(UUID_LOST_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        //为服务添加特征
        gattService.addCharacteristic(characterWrite);
        gattService.addCharacteristic(characterNotify);
        //管理服务，连接和数据交互回调
        gattServer = mBluetoothManager.openGattServer(context, new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothDevice device, final int status, final int newState) {
                super.onConnectionStateChange(device, status, newState);
                bluetoothDevice = device;
                YLog.d("蓝牙服务", "onConnectionStateChange:" + device + "    " + status + "   " + newState);
                if (newState == 2)
                    if (connectListener != null)
                        YThread.runOnUiThread(() -> connectListener.value(device));
                if (newState == 0)
                    if (disConnectListener != null)
                        YThread.runOnUiThread(() -> disConnectListener.value(device));
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
                YLog.d("蓝牙服务", "onServiceAdded");
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
                YLog.d("蓝牙服务", "onCharacteristicReadRequest");
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, final byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
                YLog.d("蓝牙服务", "收到：" + YConvert.bytesToHexString(value));
                if (readListener != null) YThread.runOnUiThread(() -> readListener.value(value));
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                super.onNotificationSent(device, status);
                YLog.d("蓝牙服务", "onNotificationSent");
            }

            @Override
            public void onMtuChanged(BluetoothDevice device, int mtu) {
                super.onMtuChanged(device, mtu);
                YLog.d("蓝牙服务", "onMtuChanged");
            }

            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor);
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characterNotify.getValue());
                YLog.d("蓝牙服务", "onDescriptorReadRequest");
            }

            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
                YLog.d("蓝牙服务", "onDescriptorWriteRequest");
            }

            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                super.onExecuteWrite(device, requestId, execute);
                YLog.d("蓝牙服务", "onExecuteWrite");
            }
        });
        gattServer.addService(gattService);
    }

    /**
     * 广播的一些基本设置
     **/
    public AdvertiseSettings createAdvSettings(boolean connectAble, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setConnectable(connectAble);
        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = builder.build();
        if (mAdvertiseSettings == null) {
            YLog.e(TAG, "mAdvertiseSettings == null");
        }
        return mAdvertiseSettings;
    }

    //广播数据
    public AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        mDataBuilder.setIncludeDeviceName(true); //广播名称也需要字节长度
        mDataBuilder.setIncludeTxPowerLevel(true);
        mDataBuilder.addServiceData(ParcelUuid.fromString("0000fff0-0000-1000-8000-00805f9b34fb"), new byte[]{1, 2});
        AdvertiseData mAdvertiseData = mDataBuilder.build();
        if (mAdvertiseData == null) {
            YLog.e(TAG, "mAdvertiseSettings == null");
        }
        return mAdvertiseData;
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                YLog.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
            } else {
                YLog.e(TAG, "onStartSuccess, settingInEffect is null");
            }
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            YLog.e(TAG, "蓝牙错误代码：" + errorCode);
            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                YLog.e(TAG, "无法启动播发，因为要广播的播发数据大于31字节。");
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                YLog.e(TAG, "无法启动播发，因为没有可用的播发实例。");
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                YLog.e(TAG, "无法启动播发，因为播发已启动。");
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                YLog.e(TAG, "由于内部错误，操作失败。");
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                YLog.e(TAG, "此平台不支持此功能。");
            }
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public YListener1<BluetoothDevice> getConnectListener() {
        return connectListener;
    }

    public void setConnectListener(YListener1<BluetoothDevice> connectListener) {
        this.connectListener = connectListener;
    }

    public YListener1<BluetoothDevice> getDisConnectListener() {
        return disConnectListener;
    }

    public void setDisConnectListener(YListener1<BluetoothDevice> disConnectListener) {
        this.disConnectListener = disConnectListener;
    }

    public YListener1<byte[]> getReadListener() {
        return readListener;
    }

    public void setReadListener(YListener1<byte[]> readListener) {
        this.readListener = readListener;
    }

    public YListener1<String> getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(YListener1<String> errorListener) {
        this.errorListener = errorListener;
    }
}