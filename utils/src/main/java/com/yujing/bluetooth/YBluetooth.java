package com.yujing.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yujing.contract.YListener1;
import com.yujing.contract.YListener2;
import com.yujing.contract.YSuccessFailListener;
import com.yujing.utils.YApp;
import com.yujing.utils.YLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 蓝牙类，如果type=null，则无法连接需要，实例化YBle或者YBt
 * 实现，打开，关闭，扫描，停止扫描，连接，读取，写入
 * 兼容蓝牙BT和BLE
 *
 * @author 余静 2020年7月16日17:44:50
 */
/*
<!--蓝牙权限，6.0之后蓝牙还需要地理位置权限 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
//用法
List<BluetoothDevice> connected = new ArrayList<>();
//实例化，读BT
YBluetooth yBtDevice = YBluetooth.getInstance().init(YBluetooth.TYPE_BT);
yBtDevice.open();
//获取设备
yBtDevice.search(bluetoothDevice -> {
    connected.add(bluetoothDevice);
    //取消搜索
    yBtDevice.cancelSearch();
});
//或者连接过的
connected.addAll(yBtDevice.getConnected());
yBtDevice.setReadListener((device, rssi) -> {
    //读取到的数据
});
yBtDevice.connect(bluetoothDevice, new YSuccessFailListener<BluetoothDevice, String>() {
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
 */
@Deprecated
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("MissingPermission")
public class YBluetooth implements YBluetoothDeviceConnect {
    public static final String TYPE_BT = "TYPE_BT";
    public static final String TYPE_BLE = "TYPE_BLE";
    private static final String TAG = "YBluetooth";
    //单例
    @SuppressLint("StaticFieldLeak")
    private static volatile YBluetooth yBluetooth;
    //context
    private Context context;
    // 注册广播BroadcastReceiver的IntentFilter
    IntentFilter intent = new IntentFilter();
    //连接接口
    YBluetoothDeviceConnect btAndBle;
    //区分是BT还是BLE
    private String type = null;
    public static boolean showLog = false;

    /**
     * 设置成单例模式
     */
    private YBluetooth(Context context, String type) {
        init(context, type);
    }

    private YBluetooth() {
    }

    public static YBluetooth getInstance() {
        if (yBluetooth == null) {
            synchronized (YBluetooth.class) {
                if (yBluetooth == null) yBluetooth = new YBluetooth();
            }
        }
        return yBluetooth;
    }

    /**
     * 蓝牙适配器
     * BluetoothAdapter是Android系统中所有蓝牙操作都需要的，
     * 它对应本地Android设备的蓝牙模块，
     * 在整个系统中BluetoothAdapter是单例的。
     * 当你获取到它的实例之后，就能进行相关的蓝牙操作了。
     */
    private BluetoothAdapter bluetoothAdapter;

    //搜索监听
    private YListener2<BluetoothDevice, Integer> searchListener;

    public YBluetooth init(String type) {
        return init(YApp.get(), type);
    }

    //初始化
    public YBluetooth init(Context context, String type) {
        this.context = context;
        this.type = type;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            YLog.e("不支持蓝牙");
            return null;
        }
        if (TYPE_BT.equals(type)) {
            btAndBle = new YBt(context);
        } else if (TYPE_BLE.equals(type)) {
            btAndBle = new YBle(context);
        }
        intent.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        context.registerReceiver(mReceiver, intent);
        return this;
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (showLog) YLog.i(TAG, "搜索到设备：" + device.getName() + "，" + ((device.getType() == 1) ? "BT" : "BLE") + "，" + device.getAddress() + ",信号强度：" + result.getRssi());
            searchListener.value(device, result.getRssi());
        }
    };

    //开始搜索
    public YBluetooth search(YListener2<BluetoothDevice, Integer> listener) {
        searchListener = listener;
        if (TYPE_BT.equals(type)) {
            bluetoothAdapter.startDiscovery();
        } else if (TYPE_BLE.equals(type)) {
            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        }
        return this;
    }

    //停止搜索
    public YBluetooth cancelSearch() {
        if (TYPE_BT.equals(type)) {
            if (bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();
        } else if (TYPE_BLE.equals(type)) {
            //停止
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
        return this;
    }

    //获取BluetoothAdapter
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    //打开蓝牙
    public YBluetooth open() {
        //没有打开蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            //提示当前应用请求蓝牙
            bluetoothAdapter.enable();
            //提示某个应用请求蓝牙
            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(enableBtIntent);
        }
        return this;
    }

    //关闭蓝牙
    public void close() {
        if (bluetoothAdapter != null) bluetoothAdapter.disable();
    }

    //获取已配对列表
    public List<BluetoothDevice> getConnected() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
//                if (TYPE_BT.equals(type)) {
//                    if (device.getType() == 1)
//                        btAndBle = new YBt(context);
//                } else if (TYPE_BLE.equals(type)) {
//                    if (device.getType() == 2)
//                        btAndBle = new YBle(context);
//                } else {
//                    bluetoothDevices.add(device);
//                }
                YLog.d(TAG, "已配对" + device.getName() + "：" + device.getAddress());
            }
        }
        return bluetoothDevices;
    }

    // 创建一个接受 ACTION_FOUND 的 BroadcastReceiver
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 当 Discovery 发现了一个设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = (device.getBondState() != BluetoothDevice.BOND_BONDED) ? intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI) : 0;
                YLog.i(TAG, "搜索到设备：" + device.getName() + "，" + ((device.getType() == 1) ? "BT" : "BLE") + "，" + device.getAddress() + ",信号强度：" + rssi);
                if (searchListener != null) {
                    if (TYPE_BT.equals(type)) {
                        if (device.getType() == 1)
                            searchListener.value(device, (int) rssi);
                    } else if (TYPE_BLE.equals(type)) {
                        if (device.getType() == 2)
                            searchListener.value(device, (int) rssi);
                    } else {
                        searchListener.value(device, (int) rssi);
                    }
                }
            } //状态改变时
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        YLog.d(TAG, "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        YLog.d(TAG, "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对
                        YLog.d(TAG, "取消配对");
                    default:
                        break;
                }
            }
        }
    };


    @Override
    public void setReadListener(YListener1<byte[]> readListener) {
        if (btAndBle != null) btAndBle.setReadListener(readListener);
    }

    @Override
    public void connect(BluetoothDevice device, YSuccessFailListener<BluetoothDevice, String> listener) {
        cancelSearch();
        if (btAndBle != null) btAndBle.connect(device, listener);
    }

    @Override
    public void read() {
        if (btAndBle != null) btAndBle.read();
    }

    @Override
    public void send(byte[] bytes) {
        if (btAndBle != null) btAndBle.send(bytes);
    }

    //关闭蓝牙
    public void onDestroy() {
        cancelSearch();
        if (context != null) {
            if (mReceiver != null)
                context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (btAndBle != null) btAndBle.onDestroy();
    }
}
