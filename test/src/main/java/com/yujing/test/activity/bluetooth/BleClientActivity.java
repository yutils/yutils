package com.yujing.test.activity.bluetooth;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.yujing.base.YBaseDialog;
import com.yujing.bluetooth.BleClient;
import com.yujing.test.R;
import com.yujing.test.activity.bluetooth.adapter.BleAdapter;
import com.yujing.test.databinding.ActivityBleClientBinding;
import com.yujing.test.databinding.DialogInfoBinding;
import com.yujing.utils.YConvert;
import com.yujing.utils.YLog;
import com.yujing.utils.YPermissions;

import java.util.ArrayList;
import java.util.List;

//事实上这种方案已过时，请参考 YBluetooth 工程
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleClientActivity extends AppCompatActivity {
    private static final String TAG = "ble_tag";
    private ActivityBleClientBinding binding;
    private List<BluetoothDevice> mDatas;
    private List<Integer> mRssis;
    private BleAdapter mAdapter;
    private BleClient bleClient;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBleClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestBlePermissionsThenInit();
    }

    private String[] blePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
        }
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestBlePermissionsThenInit() {
        String[] perms = blePermissions();
        if (YPermissions.hasPermissions(this, perms)) {
            setupBle();
            return;
        }
        new YPermissions(this)
                .setAllSuccessListener(this::setupBle)
                .setFailListener(p -> {
                    binding.tvSerBindStatus.setText("权限被拒绝:" + p);
                    Toast.makeText(this, "缺少蓝牙权限", Toast.LENGTH_LONG).show();
                })
                .request(perms);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupBle() {
        bleClient = new BleClient(this);
        bleClient.setConnectListener(aBoolean -> {
            if (aBoolean) {
                binding.pbSearchBle.setVisibility(View.GONE);
                binding.bleListView.setVisibility(View.GONE);
                binding.operaView.setVisibility(View.VISIBLE);
                binding.tvSerBindStatus.setText("已连接");
            }
        });
        bleClient.setReadListener(data -> {
                    YLog.e(TAG, "收到:" + YConvert.bytesToHexString(data));
                    binding.tvResponse.setText("收到:" + YConvert.bytesToHexString(data) + "\n"
                            + binding.tvResponse.getText().toString());
                }
        );
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        binding.btClear.setOnClickListener(v -> binding.tvResponse.setText(""));
        //读取
        binding.btnRead.setOnClickListener(v -> bleClient.read());

        //执行写入操作
        binding.btnWrite.setOnClickListener(v -> {
            String content = binding.etWriteContent.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                byte[] data = YConvert.hexStringToByte(content);
                bleClient.send(data);
            }
        });
        //搜索
        binding.ivSerBleStatus.setOnClickListener(v -> {
            if (bleClient.isScanning()) {
                binding.tvSerBindStatus.setText("停止搜索");
                binding.pbSearchBle.setVisibility(View.GONE);
                bleClient.stopScanDevice();
            } else {
                //TODO 这儿应该先判断权限
                bleClient.onDestroy();
                binding.bleListView.setVisibility(View.VISIBLE);
                binding.operaView.setVisibility(View.GONE);
                // 用户已经同意该权限
                binding.tvSerBindStatus.setText("正在搜索");
                binding.pbSearchBle.setVisibility(View.VISIBLE);
                mDatas.clear();
                mRssis.clear();
                bleClient.scanDevice(scanCallback, () -> {
                    binding.pbSearchBle.setVisibility(View.GONE);
                    binding.tvSerBindStatus.setText("搜索已结束");
                });
            }

        });
        //点击列表
        binding.bleListView.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice bluetoothDevice = mDatas.get(position);
            bleClient.connect(bluetoothDevice);
            if (bleClient.isConnecting()) {
                binding.tvSerBindStatus.setText("连接中");
            }
        });
        //关于
        binding.btInfo.setOnClickListener(v -> {
            class TestDialog extends YBaseDialog<DialogInfoBinding> {
                public TestDialog(Activity activity) {
                    super(activity, R.layout.dialog_info, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                    setWidthPixels(0.8F);
                    setHeightPixels(0.8F);
                    setFillColor(Color.parseColor("#2266FF"));
                }

                @Override
                protected void init() {
                    binding.title.setText("关于");
                    binding.tvContent.setText("蓝牙BLE客户端\n作者:余静\n实现蓝牙BLE通信，突破单次发送20字节\n\n完全开放使用\ngithub地址：https://github.com/yutils/bleclient\n\n广播服务端请参考：https://github.com/yutils/yutils/blob/master/app/src/main/java/com/yujing/test/activity/BleServerActivity.kt");
                    binding.btConfirm.setOnClickListener(v -> dismiss());
                }
            }
            TestDialog dialog = new TestDialog(this);
            dialog.show();
        });
        //初始化列表
        mDatas = new ArrayList<>();
        mRssis = new ArrayList<>();
        mAdapter = new BleAdapter(this, mDatas, mRssis);
        binding.bleListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    //扫描回调
    private android.bluetooth.le.ScanCallback scanCallback = new android.bluetooth.le.ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //YLog.i(TAG, "正在扫描");
            BluetoothDevice device = result.getDevice();
            if (!mDatas.contains(device)) {
                mDatas.add(device);
                mRssis.add(result.getRssi());
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (bleClient != null) {
            bleClient.onDestroy();
        }
        super.onDestroy();
    }
}
