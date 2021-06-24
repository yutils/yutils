package com.yujing.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yujing.contract.YListener1;
import com.yujing.utils.YConvert;
import com.yujing.utils.YDelay;
import com.yujing.utils.YLog;
import com.yujing.utils.YThread;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * 蓝牙BLE读取
 *
 * @author yujing  2021年6月24日14:28:50
 */
/*
<!--蓝牙权限，4个 6.0之后蓝牙还需要地理位置权限 -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

用法：
BleClient bleClient;
bleClient = new BleClient(this);

//停止搜索
bleClient.stopScanDevice();

//开始搜索
bleClient.scanDevice(scanCallback, () -> {
    //搜索完毕
});

//搜索中
bleClient.isScanning()

//连接监听
bleClient.setConnectListener(aBoolean -> {
if (aBoolean)
   //"已连接"
});

//收到数据
bleClient.setReadListener(data -> {
        YLog.e(TAG, "收到:" + YConvert.bytesToHexString(data));
    }
);

//发送数据
bleClient.send(data);


//连接
bleClient.connect(bluetoothDevice);
if (bleClient.isConnecting()) {
    //"连接中"
}
*/
//参考 YBluetooth 工程
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("MissingPermission")
public class BleClient {
    private static final String TAG = "YBle";
    private BluetoothManager mBluetoothManager;
    private boolean isScanning = false;
    private boolean isConnecting = false;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;

    //服务和特征值
    private UUID write_UUID_service;
    private UUID write_UUID_chara;
    private UUID read_UUID_service;
    private UUID read_UUID_chara;
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;
    private UUID indicate_UUID_service;
    private UUID indicate_UUID_chara;

    //扫描监听
    private android.bluetooth.le.ScanCallback scanCallback;
    //连接监听
    private YListener1<Boolean> connectListener;
    //读取数据监听
    private YListener1<byte[]> readListener;
    Context context;

    public BleClient(Context context) {
        this.context = context;
        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            open();
        }
    }

    //打开蓝牙
    public void open() {
        //没有打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            //提示当前应用请求蓝牙
            mBluetoothAdapter.enable();
            //提示某个应用请求蓝牙
            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(enableBtIntent);
        }
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /**
         * 断开或连接 状态发生变化时调用
         * */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            YLog.i(TAG, "onConnectionStateChange()");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //连接成功
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    YLog.i(TAG, "连接成功");
                    if (runnable != null) YDelay.remove(runnable);
                    //发现服务
                    gatt.discoverServices();
                }
            } else {
                //连接失败
                YLog.e(TAG, "失败==" + status);
                mBluetoothGatt.close();
                isConnecting = false;
                if (connectListener != null)
                    YThread.runOnUiThread(() -> connectListener.value(false));
            }
        }

        /**
         * 发现设备（真正建立连接）
         * */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //直到这里才是真正建立了可通信的连接
            isConnecting = false;
            YLog.i(TAG, "onServicesDiscovered()---建立连接");
            //获取初始化服务和特征值
            initServiceAndChara();
            //订阅通知
            mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt.getService(notify_UUID_service).getCharacteristic(notify_UUID_chara), true);
            //mBluetoothGatt.requestMtu(512);
            if (connectListener != null)
                YThread.runOnUiThread(() -> connectListener.value(true));
            read();
        }

        /**
         * 读操作的回调
         * */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            YLog.i(TAG, "onCharacteristicRead()");
        }

        /**
         * 写操作的回调
         * */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            YLog.i(TAG, "onCharacteristicWrite()  status=" + status + ",value=" + YConvert.bytesToHexString(characteristic.getValue()));
        }

        /**
         * 接收到硬件返回的数据
         * */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            YLog.i(TAG, "onCharacteristicChanged()" + Arrays.toString(characteristic.getValue()));
            byte[] data = characteristic.getValue();
            if (readListener != null)
                YThread.runOnUiThread(() -> readListener.value(data));
        }
    };

    public void connect(BluetoothDevice bluetoothDevice) {
        if (isScanning())
            stopScanDevice();
        if (!isConnecting()) {
            isConnecting = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback, TRANSPORT_LE);
            } else {
                mBluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback);
            }
        }
    }

    private void initServiceAndChara() {
        List<BluetoothGattService> bluetoothGattServices = mBluetoothGatt.getServices();
        for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                int charaProp = characteristic.getProperties();
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    read_UUID_chara = characteristic.getUuid();
                    read_UUID_service = bluetoothGattService.getUuid();
                    YLog.i(TAG, "read_chara=" + read_UUID_chara + "----read_service=" + read_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    write_UUID_chara = characteristic.getUuid();
                    write_UUID_service = bluetoothGattService.getUuid();
                    YLog.i(TAG, "write_chara=" + write_UUID_chara + "----write_service=" + write_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    write_UUID_chara = characteristic.getUuid();
                    write_UUID_service = bluetoothGattService.getUuid();
                    YLog.i(TAG, "write_chara=" + write_UUID_chara + "----write_service=" + write_UUID_service);

                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    notify_UUID_chara = characteristic.getUuid();
                    notify_UUID_service = bluetoothGattService.getUuid();
                    YLog.i(TAG, "notify_chara=" + notify_UUID_chara + "----notify_service=" + notify_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                    indicate_UUID_chara = characteristic.getUuid();
                    indicate_UUID_service = bluetoothGattService.getUuid();
                    YLog.i(TAG, "indicate_chara=" + indicate_UUID_chara + "----indicate_service=" + indicate_UUID_service);
                }
            }
        }
    }

    public void read() {
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(read_UUID_service).getCharacteristic(read_UUID_chara);
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public synchronized void send(byte[] data) {
        BluetoothGattService service = mBluetoothGatt.getService(write_UUID_service);
        BluetoothGattCharacteristic charaWrite = service.getCharacteristic(write_UUID_chara);
        YLog.i(TAG, "发送数据长度：" + data.length + "字节");
        charaWrite.setValue(data);
        mBluetoothGatt.writeCharacteristic(charaWrite);
    }

    //结束扫描
    Runnable runnable;
    /**
     * 开始扫描 10秒后自动停止
     */
    public void scanDevice(android.bluetooth.le.ScanCallback scanCallback, Runnable scanEnd) {
        this.scanCallback = scanCallback;
        isScanning = true;
        mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        runnable = () -> {
            //结束扫描
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            isScanning = false;
            YThread.runOnUiThread(scanEnd);
        };
        //10秒后停止扫描
        YDelay.run(1000 * 10, runnable);
    }

    /**
     * 停止扫描
     */
    public void stopScanDevice() {
        YDelay.remove(runnable);
        isScanning = false;
        if (mBluetoothAdapter.getBluetoothLeScanner() != null)
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
    }

    public void setReadListener(YListener1<byte[]> readListener) {
        this.readListener = readListener;
    }

    public void setConnectListener(YListener1<Boolean> connectListener) {
        this.connectListener = connectListener;
    }

    public boolean isScanning() {
        return isScanning;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void onDestroy() {
        if (mBluetoothGatt != null)
            mBluetoothGatt.disconnect();
    }
}
