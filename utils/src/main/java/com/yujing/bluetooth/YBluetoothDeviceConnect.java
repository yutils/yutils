package com.yujing.bluetooth;

import android.bluetooth.BluetoothDevice;

import com.yujing.contract.YListener1;
import com.yujing.contract.YSuccessFailListener;
@Deprecated
public interface YBluetoothDeviceConnect {
    //设置读取监听
    void setReadListener(YListener1<byte[]> readListener);
    //连接
    void connect(BluetoothDevice device, YSuccessFailListener<BluetoothDevice, String> listener);
    //读取
    void read();
    //发送
    void send(byte[] bytes);
    //关闭
    void onDestroy();
}
