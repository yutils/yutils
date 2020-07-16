package com.yujing.bluetooth;

import android.bluetooth.BluetoothDevice;

import com.yujing.contract.YListener1;
import com.yujing.contract.YSuccessFailListener;

public interface YBluetoothDeviceConnect {
    void setReadListener(YListener1<byte[]> readListener);

    void connect(BluetoothDevice device, YSuccessFailListener<BluetoothDevice, String> listener);

    void read();

    void send(byte[] bytes);

    void onDestroy();
}
