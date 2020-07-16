package com.yujing.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.yujing.contract.YSuccessFailListener;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public void test1(Context context) {
        List<BluetoothDevice> connected = new ArrayList<>();
        //实例化，读BT
        YBluetooth yBtDevice = YBluetooth.getInstance().init(context, YBluetooth.TYPE_BT);
        yBtDevice.open();
        //获取设备
        yBtDevice.search(bluetoothDevice -> {
            connected.add(bluetoothDevice);
            //取消搜索
            yBtDevice.cancelSearch();
        });
        //或者连接过的
        connected.addAll(yBtDevice.getConnected());
        yBtDevice.setReadListener(bytes -> {
            //读取到的数据
        });
        yBtDevice.connect(connected.get(0), new YSuccessFailListener<BluetoothDevice, String>() {
            @Override
            public void success(BluetoothDevice bluetoothDevice) {
                //连接成功
            }

            @Override
            public void fail(String s) {
                //连接失败
            }
        });
        yBtDevice.send("发送的内容".getBytes());
        //yBtConnect.onDestroy();
    }
    public void test2(Context context) {
        YBluetooth yBtDevice = YBluetooth.getInstance().init(context, null);
        yBtDevice.open();
        //获取设备
        List<BluetoothDevice> connected = new ArrayList<>();
        yBtDevice.search(bluetoothDevice -> {
            connected.add(bluetoothDevice);
            //取消搜索
            yBtDevice.cancelSearch();
        });

        //或者连接过的
        connected.addAll(yBtDevice.getConnected());
        //yBtDevice.onDestroy();

        //BT连接

        YBt yBtConnect = new YBt(context);
        yBtConnect.setReadListener(bytes -> {
            //读取到的数据
        });
        yBtConnect.connect(connected.get(0), new YSuccessFailListener<BluetoothDevice, String>() {
            @Override
            public void success(BluetoothDevice bluetoothDevice) {
                //连接成功
            }

            @Override
            public void fail(String s) {
                //连接失败
            }
        });
        yBtConnect.send("发送的内容".getBytes());
        //yBtConnect.onDestroy();

        //BLe连接
        YBle yBleConnect = new YBle(context);
        yBleConnect.setReadListener(bytes -> {
            //读取到的数据
        });
        yBleConnect.connect(connected.get(0), new YSuccessFailListener<BluetoothDevice, String>() {
            @Override
            public void success(BluetoothDevice bluetoothDevice) {
                //连接成功
            }

            @Override
            public void fail(String s) {
                //连接失败
            }
        });
        yBleConnect.send("发送的内容".getBytes());
        yBleConnect.onDestroy();
    }
}
