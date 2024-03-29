package com.yujing.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yujing.contract.YListener1;
import com.yujing.contract.YSuccessFailListener;
import com.yujing.utils.YConvert;
import com.yujing.utils.YLog;
import com.yujing.utils.YThread;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

/**
 * 低功耗蓝牙连接类，实现连接，发送数据，读取数据
 *
 * @author 余静 2020年7月16日17:43:04
 */
/*
<!--蓝牙权限，6.0之后蓝牙还需要地理位置权限 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
*/
@Deprecated
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("MissingPermission")
public class YBle implements YBluetoothDeviceConnect {
    private static final String TAG = "YBle";
    private BluetoothGatt mBluetoothGatt;
    private Context context;
    //服务和特征值
    private UUID write_UUID_service;
    private UUID write_UUID_chara;
    private UUID read_UUID_service;
    private UUID read_UUID_chara;
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;
    private UUID indicate_UUID_service;
    private UUID indicate_UUID_chara;
    public boolean showLog = false;
    YSuccessFailListener<BluetoothDevice, String> listener;
    //读取数据监听
    private YListener1<byte[]> readListener;

    BluetoothDevice bluetoothDevice;

    public YBle(Context context) {
        this.context = context;
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
                    YLog.d(TAG, "read_chara=" + read_UUID_chara + "----read_service=" + read_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    write_UUID_chara = characteristic.getUuid();
                    write_UUID_service = bluetoothGattService.getUuid();
                    YLog.d(TAG, "write_chara=" + write_UUID_chara + "----write_service=" + write_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    write_UUID_chara = characteristic.getUuid();
                    write_UUID_service = bluetoothGattService.getUuid();
                    YLog.d(TAG, "write_chara=" + write_UUID_chara + "----write_service=" + write_UUID_service);

                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    notify_UUID_chara = characteristic.getUuid();
                    notify_UUID_service = bluetoothGattService.getUuid();
                    YLog.d(TAG, "notify_chara=" + notify_UUID_chara + "----notify_service=" + notify_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                    indicate_UUID_chara = characteristic.getUuid();
                    indicate_UUID_service = bluetoothGattService.getUuid();
                    YLog.d(TAG, "indicate_chara=" + indicate_UUID_chara + "----indicate_service=" + indicate_UUID_service);
                }
            }
        }
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /**
         * 断开或连接 状态发生变化时调用
         * */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            YLog.d(TAG, "onConnectionStateChange()");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //连接成功
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    if (showLog) YLog.i(TAG, "连接成功");
                    //发现服务
                    gatt.discoverServices();
                }
            } else {
                //连接失败
                YLog.e(TAG, "失败==" + status);
                mBluetoothGatt.close();
                //连接失败
                if (listener != null)
                    YThread.runOnUiThread(() -> listener.fail("连接失败"));
            }
        }

        /**
         * 发现设备（真正建立连接）
         * */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //直到这里才是真正建立了可通信的连接
            if (showLog) YLog.i(TAG, "onServicesDiscovered()---建立连接");
            //获取初始化服务和特征值
            initServiceAndChara();
            //订阅通知
            mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt.getService(notify_UUID_service).getCharacteristic(notify_UUID_chara), true);
//            mBluetoothGatt.requestMtu(512);
            //连接成功
            if (listener != null)
                YThread.runOnUiThread(() -> listener.success(bluetoothDevice));
            read();
        }

        /**
         * 读操作的回调
         * */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            YLog.d(TAG, "onCharacteristicRead()");
        }

        /**
         * 写操作的回调
         * */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            YLog.d(TAG, "onCharacteristicWrite()  status=" + status + ",value=" + YConvert.bytesToHexString(characteristic.getValue()));
        }

        /**
         * 接收到硬件返回的数据
         * */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            YLog.d(TAG, "onCharacteristicChanged()" + Arrays.toString(characteristic.getValue()));
            byte[] data = characteristic.getValue();
            if (readListener != null)
                YThread.runOnUiThread(() -> readListener.value(data));
        }
    };

    @Override
    public void setReadListener(YListener1<byte[]> readListener) {
        this.readListener = readListener;
    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice, YSuccessFailListener<BluetoothDevice, String> listener) {
        this.listener = listener;
        this.bluetoothDevice = bluetoothDevice;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback, TRANSPORT_LE);
        } else {
            mBluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback);
        }
    }

    @Override
    public void read() {
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(read_UUID_service).getCharacteristic(read_UUID_chara);
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void send(byte[] data) {
        BluetoothGattService service = mBluetoothGatt.getService(write_UUID_service);
        BluetoothGattCharacteristic charaWrite = service.getCharacteristic(write_UUID_chara);
        if (showLog) YLog.i(TAG, "发送数据长度：" + data.length + "字节");
        charaWrite.setValue(data);
        mBluetoothGatt.writeCharacteristic(charaWrite);

//        if (data.length > 20) {
//            charaWrite.setValue(data);
//            mBluetoothGatt.writeCharacteristic(charaWrite);
//            int num = data.length % 20 != 0 ? data.length / 20 + 1 : data.length / 20;
//            new Thread(() -> {
//                for (int i = 0; i < num; i++) {
//                    byte[] send;
//                    if (i == num - 1) {
//                        send = new byte[data.length - i * 20];
//                        System.arraycopy(data, i * 20, send, 0, data.length - i * 20);
//                    } else {
//                        send = new byte[20];
//                        System.arraycopy(data, i * 20, send, 0, 20);
//                    }
//                    charaWrite.setValue(send);
//                    mBluetoothGatt.writeCharacteristic(charaWrite);
//
//                    YLog.i("发送"+YConvert.bytesToHexString(send));
//                    SystemClock.sleep(100);
//                }
//            }).start();
//        }
    }

    @Override
    public void onDestroy() {
        if (mBluetoothGatt != null)
            mBluetoothGatt.disconnect();
    }
}
