package com.yujing.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

/**
 * 获取gps
 *
 * @author 余静 2021年5月25日15:24:39
 */
/*
用法：

//创建
YGps yGps = new YGps(this);
//方法1.立即获取一次定位，如果有GPS就获取GPS信息，若没有就获取网络定位
Location location = yGps.getLocation();

//方法2.每秒获取一次GSP位置
yGps.getLocationGPS(new YGps.GpsLocation() {
    @Override
    public void backLocation(Location location) {
        //location位置
    }
});

//方法3.每秒获取一次基站位置
yGps.getLocationNET(new YGps.GpsLocation() {
    @Override
    public void backLocation(Location location) {
        //location位置
    }
});

//停止
yGps.StopGPS();

//停止
yGps.StopNET();

double latitude     = location.getLatitude();//纬度
double longitude    = location.getLongitude();//经度
double time         = location.getTime();//时间
double altitude     = location.getAltitude();//海拔
String provider     = location.getProvider();//定位形式 基站还是GPS


kotlin：

//创建
val yGps = YGps(this)
//方法1.立即获取一次定位，如果有GPS就获取GPS信息，若没有就获取网络定位
//val location = yGps.location


//方法2.每秒获取一次GSP位置
yGps.getLocationGPS { location->
    //location位置
    var latitude     = location.latitude//纬度
    var longitude    = location.longitude//经度
}

//方法3.每秒获取一次基站位置
yGps.getLocationNET {  location->
    //location位置
}
 */
/*
权限：
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

获取权限
private void initPermission() {
    String[] permissions = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    ArrayList<String> toApplyList = new ArrayList<>();
    for (String perm : permissions) {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
        toApplyList.add(perm); // 进入到这里代表没有权限.
        }
    }
    String[] tmpList = new String[toApplyList.size()];
    if (!toApplyList.isEmpty()) {
        ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
    }
}
 */
@SuppressWarnings({"unused"})
@SuppressLint("MissingPermission")
public class YGps {
    private GpsLocation gpsLocation;
    private final Context context;
    private LocationManager locationManagerGPS;// GPS
    private LocationManager locationManagerNET;// 网络
    /**
     * 纬度
     */
    private static volatile double latitude = 0.0;
    /**
     * 经度
     */
    private static volatile double longitude = 0.0;

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    /**
     * 判断GPS模块是否存在或者是开启 如果开启正常，则会直接进入到显示页面，如果开启不正常，则会进行到GPS设置页面传递的Activity对象
     *
     * @param context context
     */
    public YGps(Context context) {
        this.context = context;
    }

    public void openGPSSettings(Activity activity) {
        LocationManager alm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (alm == null) return;
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            YLog.d("GPS", "GPS模块正常");
            return;
        }
        Toast.makeText(activity, "请手动开启GPS后再试！", Toast.LENGTH_SHORT).show();
        // Intent(Settings.ACTION_SECURITY_SETTINGS);//进入系统安全设置
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);// 进入位置设置
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
    }

    /**
     * 获取GPS信息
     *
     * @param gpsLocation 回调
     */
    public void getLocationGPS(GpsLocation gpsLocation) {
        getLocationGPS(1000, gpsLocation);
    }

    /**
     * 获取GPS信息
     *
     * @param minTimeMs   每minTimeMs毫秒一次
     * @param gpsLocation 回调
     */
    public void getLocationGPS(long minTimeMs, GpsLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
        // GPS定位
        locationManagerGPS = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMs, 0, locationListener);
        } catch (Exception e) {
            Toast.makeText(context, "请给本APP获取位置权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取网络定位信息
     *
     * @param gpsLocation 回调
     */
    public void getLocationNET(GpsLocation gpsLocation) {
        getLocationNET(1000, gpsLocation);
    }

    /**
     * 获取网络定位信息
     *
     * @param minTimeMs   每minTimeMs毫秒一次
     * @param gpsLocation 回调
     */
    public void getLocationNET(long minTimeMs, GpsLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
        // 基站定位
        locationManagerNET = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            locationManagerNET.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, minTimeMs, 0, locationListener);
        } catch (Exception e) {
            Toast.makeText(context, "请给本APP获取位置权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取网络wifi定位信息
     *
     * @param gpsLocation 回调
     */
    public void getLocationWIFI(GpsLocation gpsLocation) {
        getLocationWIFI(1000, gpsLocation);
    }

    /**
     * 获取网络wifi定位信息
     *
     * @param minTimeMs   每minTimeMs毫秒一次
     * @param gpsLocation 回调
     */
    public void getLocationWIFI(long minTimeMs, GpsLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
        // 基站定位
        locationManagerNET = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            locationManagerNET.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMs, 0, locationListener);
        } catch (Exception e) {
            Toast.makeText(context, "请给本APP获取位置权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 停止GPS定位信息
     */
    public void StopGPS() {
        if (locationManagerGPS != null)
            locationManagerGPS.removeUpdates(locationListener);
        locationManagerGPS = null;
    }

    /**
     * 停止网络定位信息
     */
    public void StopNET() {
        if (locationManagerNET != null)
            locationManagerNET.removeUpdates(locationListener);
        locationManagerNET = null;
    }

    private final LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            // 记录最后一次定位GPS
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            if (location != null && gpsLocation != null) {
                gpsLocation.backLocation(location);
            }
        }
    };

    /**
     * 获取一次定位信息，如果有GPS就获取GPS信息，若没有就获取网络定位
     *
     * @return Location位置信息
     */
    public Location getLocation() {
        Location location = null;
        try {
            // GPS定位
            LocationManager locationManager0 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager0 == null) {
                return null;
            }
            if (locationManager0.isProviderEnabled(LocationManager.GPS_PROVIDER)) {// 判断GPS是否打开
                if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    location = locationManager0.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                        return location;
                }
            }
            // 基站定位
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                return null;
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            YLog.e("错误", "没有GPS权限");
        }
        return location;
    }

    public interface GpsLocation {
        void backLocation(Location location);
    }
}
