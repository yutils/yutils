package com.yujing.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;

/**
 * 对象储存到磁盘，读取时候调取，如果缓存中有该对象就在缓存中取，如果缓存中没有对象就在磁盘上面取 用于解决静态变量被释放问题
 *
 * @author 余静 2019年12月9日12:05:41
 */
/*
用法：
//参数下载
kotlin
var parameterInfo: ParameterInfo
    get() = YSave.get(App.get(), "ParameterInfo", ParameterInfo::class.java)
    set(parameterInfo) = YSave.put(App.get(), "ParameterInfo", parameterInfo)
java
    public static String getP() {
        return YSave.get(App.get(), "ParameterInfo", String.class);
    }
    public static void setP(String purchaseLine) {
        YSave.put(App.get(), "ParameterInfo", purchaseLine);
    }
 */
@SuppressWarnings("unused")
public class YSave {
    private static final String TAG = "YSave"; //标记
    private static HashMap<String, Object> cache;    // 缓存，临时缓存
    private static volatile boolean useCache = true;//是否启用缓存

    private final Gson gson = new Gson();
    private final Context context;
    private String path; //保存文件位置
    private String extensionName; //扩展名

    public static boolean isUseCache() {
        return useCache;
    }

    public static void setUseCache(boolean useCache) {
        YSave.useCache = useCache;
    }

    // 获取缓存
    private synchronized static HashMap<String, Object> getCache() {
        return (cache == null) ? cache = new HashMap<>() : cache;
    }

    public YSave(Context context) {
        this.context = context;
    }

    public YSave(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    public YSave(Context context, String path, String extensionName) {
        this.context = context;
        this.path = path;
        this.extensionName = extensionName;
    }

    // 存放在默认位置，路径data/data/files/+ FOLDER_NAME +/+ Name +EXTENSION
    public String getPath() {
        return (path != null) ? path : (context.getFilesDir() + File.separator + "YSave" + File.separator);
    }

    // 写入
    public void put(String key, Object data) {
        if (data == null) {
            remove(key);
            return;
        }
        if (useCache) getCache().put(key, data);

        // 然后写盘,byte直接写入
        if (byte[].class.equals(data.getClass())) {
            YFileUtil.byteToFile(getFile(key), (byte[]) data);
            return;
        }
        // 如果是String，直接写入
        if (String.class.equals(data.getClass())) {
            YFileUtil.stringToFile(getFile(key), (String) data);
            return;
        }
        // 如果是其他对象String，转换成json写入
        String value = gson.toJson(data);
        YFileUtil.stringToFile(getFile(key), value);
    }

    // 删除
    public void remove(String key) {
        getCache().remove(key);
        YFileUtil.delFile(getFile(key));
    }

    // 删除全部
    public void removeAll() {
        getCache().clear();
        YFileUtil.delFile(getPath());
    }

    // 读取
    public Object get(String key, java.lang.reflect.Type type) {
        return get(key, type, null);
    }

    // 读取
    public Object get(String key, java.lang.reflect.Type type, Object defaultObject) {
        // 如果使用缓存，直接返回
        if (useCache) {
            Object object = getCache().get(key);
            if (object != null) return object;
        }
        // 读盘，如果是byte[]直接返回
        if (type.toString().equals("byte[]")) {
            return YFileUtil.fileToByte(getFile(key));
        }
        // 读盘，如果是null直接返回
        String value = getString(key);
        if (value == null) return defaultObject;

        //读盘，如果是String直接返回
        if (type.equals(String.class)) {
            if (useCache) getCache().put(key, value);// 保存到内存中
            return value;
        }
        //如果是其他对象，转换后返回
        Object object = gson.fromJson(value, type);
        if (useCache) getCache().put(key, object);// 保存到内存中
        return object;
    }

    // 读取
    public <T> T get(String key, Class<T> classOfT) {
        return get(key, classOfT, null);
    }

    // 读取
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> classOfT, Object defaultObject) {
        // 如果使用缓存，直接返回
        if (useCache) {
            T object = classOfT.cast(getCache().get(key));
            if (object != null) return object;
        }
        // 读盘，如果是byte[]直接返回
        if (classOfT.equals(byte[].class)) {
            return (T) YFileUtil.fileToByte(getFile(key));
        }

        // 读盘，如果是null直接返回
        String json = getString(key);
        if (json == null) return (T) defaultObject;

        //读盘，如果是String直接返回
        if (classOfT.equals(String.class)) {
            T object = (T) json;
            if (useCache) getCache().put(key, object);// 保存到内存中
            return object;
        }

        //如果是其他对象，转换后返回
        T object = gson.fromJson(json, classOfT);
        if (useCache) getCache().put(key, object);// 保存到内存中
        return object;
    }

    // 读取String
    public String getString(String key) {
        return YFileUtil.fileToString(getFile(key));
    }

    // 获取文件
    public File getFile(String key) {
        return new File(getPath() + key + (extensionName != null ? extensionName : ".save"));
    }

    public YSave setPath(String path) {
        this.path = path;
        return this;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public YSave setExtensionName(String extensionName) {
        this.extensionName = extensionName;
        return this;
    }


    // ★★★★★★★★★★★★★★★★★★★★★★★静态方法开始★★★★★★★★★★★★★★★★★★★★★★★★★★
    @SuppressLint("StaticFieldLeak")
    private static volatile YSave ySave;//单例

    public static YSave getInstance(Context context) {
        if (ySave == null) {
            synchronized (YSave.class) {
                if (ySave == null) ySave = new YSave(context);
            }
        }
        return ySave;
    }

    public static YSave create(Context context) {
        return new YSave(context);
    }

    public static YSave create(Context context, String path) {
        return new YSave(context, path);
    }

    public static YSave create(Context context, String path, String extensionName) {
        return new YSave(context, path, extensionName);
    }

    // 写入
    public static <T> void put(Context context, String key, T data) {
        YSave ySave = new YSave(context);
        ySave.put(key, data);
    }

    // 读取
    public static <T> T get(Context context, String key, Class<T> classOfT) {
        YSave ySave = new YSave(context);
        return ySave.get(key, classOfT);
    }

    // 读取
    public static <T> T get(Context context, String key, Class<T> classOfT, Object defaultObject) {
        YSave ySave = new YSave(context);
        return ySave.get(key, classOfT, defaultObject);
    }

    // 读取
    public static Object get(Context context, String key, java.lang.reflect.Type type) {
        YSave ySave = new YSave(context);
        return ySave.get(key, type);
    }

    // 读取
    public static Object get(Context context, String key, java.lang.reflect.Type type, Object defaultObject) {
        YSave ySave = new YSave(context);
        return ySave.get(key, type, defaultObject);
    }

    // 读取
    public static String getString(Context context, String key) {
        YSave ySave = new YSave(context);
        return ySave.getString(key);
    }

    // 删除
    public static void remove(Context context, String key) {
        YSave ySave = new YSave(context);
        ySave.remove(key);
    }

    // 删除全部
    public static void removeAll(Context context) {
        YSave ySave = new YSave(context);
        ySave.removeAll();
    }
}
