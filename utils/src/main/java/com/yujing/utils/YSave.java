package com.yujing.utils;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;

/**
 * 对象储存到磁盘，读取时候调取，如果缓存中有该对象就在缓存中取，如果缓存中没有对象就在磁盘上面取 用于解决静态变量被释放问题
 *
 * @author 余静 2019年12月9日12:05:41
 */
@SuppressWarnings("unused")
public class YSave {
    private static final String TAG = "YSave"; //标记
    private static HashMap<String, Object> cache;    // 缓存，临时缓存
    private static boolean useCache = true;//是否启用缓存
    private String folderName = TAG; //文件夹
    private static String EXTENSION = ".save";//扩展名

    public static boolean isUseCache() {
        return useCache;
    }

    public static void setUseCache(boolean useCache) {
        YSave.useCache = useCache;
    }

    private Gson gson = new Gson();
    private Context context;

    // 获取缓存
    private synchronized static HashMap<String, Object> getCache() {
        return (cache == null) ? cache = new HashMap<>() : cache;
    }

    public YSave(Context context) {
        this.context = context;
    }

    // 存放在默认位置，路径data/data/files/+ FOLDER_NAME +/+ Name +EXTENSION
    public String getPath() {
        return context.getFilesDir() + File.separator + folderName + File.separator;
    }

    // 写入
    public void put(String key, Object object) {
        if (object == null) {
            remove(key);
            return;
        }
        if (useCache) getCache().put(key, object);

        // 然后写盘,byte直接写入
        if (byte[].class.equals(object.getClass())) {
            YFileUtil.byteToFile((byte[]) object, getFile(key));
            return;
        }
        // 如果是String，直接写入
        if (String.class.equals(object.getClass())) {
            YFileUtil.stringToFile(getFile(key), (String) object);
            return;
        }
        // 如果是其他对象String，转换成json写入
        String value = gson.toJson(object);
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
        if (value == null) return null;

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
        if (json == null) return null;

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
        return new File(getPath() + key + EXTENSION);
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    // ★★★★★★★★★★★★★★★★★★★★★★★静态方法开始★★★★★★★★★★★★★★★★★★★★★★★★★★

    // 写入
    public static <T> void put(Context context, String key, T object) {
        YSave ySave = new YSave(context);
        ySave.put(key, object);
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
