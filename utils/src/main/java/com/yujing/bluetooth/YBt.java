package com.yujing.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.yujing.contract.YListener1;
import com.yujing.contract.YSuccessFailListener;
import com.yujing.utils.YLog;
import com.yujing.utils.YReadInputStream;
import com.yujing.utils.YThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 蓝牙连接类，实现连接，发送数据，读取数据
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
@SuppressLint("MissingPermission")
public class YBt implements YBluetoothDeviceConnect {
    private YListener1<byte[]> readListener;
    private YReadInputStream readInputStream;
    Context context;
    InputStreamReadListener inputStreamReadListener = null;

    public YBt(Context context) {
        this.context = context;
    }

    /**
     * 配对成功后的蓝牙套接字
     */
    private BluetoothSocket bluetoothSocket;
    /**
     * 蓝牙UUID
     * 此处，必须使用Android的SSP（协议栈默认）的UUID：00001101-0000-1000-8000-00805F9B34FB
     */
    public static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * 设置解析inputStream
     *
     * @param readInputStream
     */
    public void setReadInputStream(YReadInputStream readInputStream) {
        this.readInputStream = readInputStream;
    }

    /**
     * 尝试连接一个设备，子线程中完成，因为会线程阻塞
     *
     * @param device   蓝牙设备对象
     * @param listener 结果回调事件
     */
    @Override
    public void connect(BluetoothDevice device, YSuccessFailListener<BluetoothDevice, String> listener) {
        Thread thread = new Thread(() -> {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                //如果这个设备取消了配对，则尝试配对
                device.createBond();
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                try {
                    //通过和服务器协商的uuid来进行连接
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                    YLog.d("blueTooth", "开始连接...");
                    //如果当前socket处于非连接状态则调用连接
                    if (!bluetoothSocket.isConnected()) {
                        //你应当确保在调用connect()时设备没有执行搜索设备的操作。
                        // 如果搜索设备也在同时进行，那么将会显著地降低连接速率，并很大程度上会连接失败。
                        bluetoothSocket.connect();
                    }
                    YLog.d("blueTooth", "已经链接");
                    YThread.runOnUiThread(() -> listener.success(device));
                    read();
                } catch (Exception e) {
                    YLog.e("blueTooth", "...连接失败");
                    YThread.runOnUiThread(() -> listener.fail("连接失败"));
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        });
        thread.setName("YBt-连接线程");
        thread.start();
    }

    /**
     * 发送bytes
     */
    @Override
    public void send(byte[] bytes) {
        try {
            OutputStream outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取
     */
    @Override
    public void read() {
        //如果没有设置读取监听，直接返回
        if (readListener == null)
            return;
        //如果设置inputStream读取监听，那就就是用户自己解析inputStream
        if (inputStreamReadListener != null) {
            try {
                readListener.value(inputStreamReadListener.inputStreamToBytes(bluetoothSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //否则使用readInputStream读取
            if (readInputStream != null) {
                readInputStream.stop();
            }
            try {
                readInputStream = new YReadInputStream(bluetoothSocket.getInputStream(), readListener);
                readInputStream.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * inputSteam读取解析监听
     */
    public interface InputStreamReadListener {
        byte[] inputStreamToBytes(InputStream inputStream) throws IOException;
    }

    @Override
    public void setReadListener(YListener1<byte[]> readListener) {
        this.readListener = readListener;
    }

    //关闭onDestroy
    @Override
    public void onDestroy() {
        if (readInputStream != null)
            readInputStream.stop();
    }
}
