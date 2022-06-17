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
kotlin：
var user: User
    get() = YSave.get("user", User::class.java)
    set(obj) = YSave.put( "user", obj)

//或
var user: User
    get() = YSave.get(YApp.get(), "user", User::class.java)
    set(obj) = YSave.put(YApp.get(), "user", obj)

//或
var bl: Boolean
    get() = YSave.getInstance().get("b", Boolean::class.java)
    set(obj) = YSave.getInstance().put("b", obj)

//或
var bl: Boolean
    get() = YSave.create(YPath.get(),".txt").get("test", Boolean::class.java)
    set(obj) = YSave.create(YPath.get(),".txt").put("test", obj)

java：
public static String getIP() { return YSave.get(YApp.get(), "ip", String.class);}
public static void setIP(String value) {YSave.put(YApp.get(), "ip", value);}

//或
public static boolean getP() { return YSave.getInstance().get("p", boolean.class); }
public static void setP(Boolean b) { YSave.getInstance().put("p", b); }

//或
public static boolean getP() { return YSave.create(YPath.get(),"txt").get("p", boolean.class); }
public static void setP(Boolean b) { YSave.create(YPath.get(),"txt").put("p", b); }
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

    public static boolean isUseCache() {return useCache;}

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
        return (path != null) ? YPath.toDir(path) : (context.getFilesDir() + File.separator + "YSave" + File.separator);
    }

    // 写入
    public void write(String key, Object data) {
        if (data == null) {
            remove(key);
            return;
        }
        if (useCache) getCache().put(key, data);

        // 然后写盘,byte直接写入
        if (byte[].class.equals(data.getClass())) {
            YFileUtil.byteToFile(readFile(key), (byte[]) data);
            return;
        }
        // 如果是String，直接写入
        if (String.class.equals(data.getClass())) {
            YFileUtil.stringToFile(readFile(key), (String) data);
            return;
        }
        // 如果是其他对象String，转换成json写入
        String value = gson.toJson(data);
        YFileUtil.stringToFile(readFile(key), value);
    }

    // 删除
    public void remove(String key) {
        getCache().remove(key);
        YFileUtil.delFile(readFile(key));
    }

    // 删除全部
    public void removeAll() {
        getCache().clear();
        YFileUtil.delFile(getPath());
    }

    // 读取
    public Object read(String key, java.lang.reflect.Type type) {
        return read(key, type, null);
    }

    // 读取
    public Object read(String key, java.lang.reflect.Type type, Object defaultObject) {
        // 如果使用缓存，直接返回
        if (useCache) {
            Object object = getCache().get(key);
            if (object != null) return object;
        }
        // 读盘，如果是byte[]直接返回
        if (type.toString().equals("byte[]")) {
            return YFileUtil.fileToByte(readFile(key));
        }
        // 读盘，如果是null直接返回
        String value = readString(key);
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
    public <T> T read(String key, Class<T> classOfT) {
        return read(key, classOfT, null);
    }

    // 读取
    @SuppressWarnings("unchecked")
    public <T> T read(String key, Class<T> classOfT, Object defaultObject) {
        // 如果使用缓存，直接返回
        if (useCache) {
            try {
                T object;
                if (classOfT == boolean.class)
                    classOfT = (Class<T>) Boolean.class;
                else if (classOfT == int.class)
                    classOfT = (Class<T>) Integer.class;
                else if (classOfT == long.class)
                    classOfT = (Class<T>) Long.class;
                else if (classOfT == double.class)
                    classOfT = (Class<T>) Double.class;
                else if (classOfT == float.class)
                    classOfT = (Class<T>) Float.class;
                else if (classOfT == byte.class)
                    classOfT = (Class<T>) Byte.class;
                else if (classOfT == char.class)
                    classOfT = (Class<T>) Character.class;
                else if (classOfT == short.class)
                    classOfT = (Class<T>) Short.class;
                object = (T) classOfT.cast(getCache().get(key));
                if (object != null) return object;
            } catch (Exception e) {
                YLog.i("类型转换失败,数据：" + getCache().get(key) + " 采用JSON反序列化模式运行。错误异常：" + e.getMessage());
            }
        }
        // 读盘，如果是byte[]直接返回
        if (classOfT.equals(byte[].class)) {
            return (T) YFileUtil.fileToByte(readFile(key));
        }

        // 读盘，如果是null直接返回
        String json = readString(key);
        if (json == null) return (T) defaultObject;

        //读盘，如果是String直接返回
        if (classOfT.equals(String.class)) {
            T obj = (T) json;
            if (useCache) getCache().put(key, obj);// 保存到内存中
            return obj;
        }

        //如果是其他对象，转换后返回
        T obj = gson.fromJson(json, classOfT);
        if (useCache) getCache().put(key, obj);// 保存到内存中
        return obj;
    }

    // 读取String
    public String readString(String key) {
        return YFileUtil.fileToString(readFile(key));
    }

    // 获取文件
    public File readFile(String key) {
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

    //单例一个YSave
    public static YSave getInstance() {
        return getInstance(YApp.get());
    }

    public static YSave getInstance(Context context) {
        if (ySave == null) {
            synchronized (YSave.class) {
                if (ySave == null) ySave = new YSave(context);
            }
        }
        return ySave;
    }

    // 创建一个YSave
    public static YSave create() {
        return new YSave(YApp.get());
    }

    public static YSave create(String path) {
        return new YSave(YApp.get(), path);
    }

    public static YSave create(String path, String extensionName) {
        return new YSave(YApp.get(), path, extensionName);
    }
    // 创建，带context
    public static YSave create(Context context) {
        return new YSave(context);
    }

    public static YSave create(Context context, String path) {
        return new YSave(context, path);
    }

    public static YSave create(Context context, String path, String extensionName) {
        return new YSave(context, path, extensionName);
    }


    // 读取
    public static String get(String key) {
        return getInstance().readString(key);
    }

    public static <T> T get(String key, Class<T> classOfT) {
        return getInstance().read(key, classOfT);
    }

    public static <T> T get(String key, Class<T> classOfT, Object defaultObject) {
        return getInstance().read(key, classOfT, defaultObject);
    }

    public static Object get(String key, java.lang.reflect.Type type) {
        return getInstance().read(key, type);
    }

    public static Object get(String key, java.lang.reflect.Type type, Object defaultObject) {
        return getInstance().read(key, type, defaultObject);
    }

    public static String getString(String key) {
        return getInstance().readString(key);
    }

    // 读取，带context
    public static String get(Context context, String key) {
        return getInstance(context).readString(key);
    }

    public static <T> T get(Context context, String key, Class<T> classOfT) {
        return getInstance(context).read(key, classOfT);
    }

    public static <T> T get(Context context, String key, Class<T> classOfT, Object defaultObject) {
        return getInstance(context).read(key, classOfT, defaultObject);
    }

    public static Object get(Context context, String key, java.lang.reflect.Type type) {
        return getInstance(context).read(key, type);
    }

    public static Object get(Context context, String key, java.lang.reflect.Type type, Object defaultObject) {
        return getInstance(context).read(key, type, defaultObject);
    }

    public static String getString(Context context, String key) {
        return getInstance(context).readString(key);
    }


    // 写入
    public static <T> void put(String key, T data) {
        getInstance().write(key, data);
    }

    public static <T> void set(String key, T data) {
        getInstance().write(key, data);
    }

    // 写入，带context
    public static <T> void put(Context context, String key, T data) {
        getInstance(context).write(key, data);
    }

    public static <T> void set(Context context, String key, T data) {
        getInstance(context).write(key, data);
    }

    // 删除
    public static void remove(Context context, String key) {
        getInstance(context).remove(key);
    }

    // 删除全部
    public static void removeAll(Context context) {
        getInstance(context).removeAll();
    }
}
