package com.yujing.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/**
 * 对象储存到磁盘，读取时候调取，如果缓存中有该对象就在缓存中取，如果缓存中没有对象就在磁盘上面取 用于解决静态变量被释放问题
 *
 * @author 余静 2018年5月15日19:00:17
 * 已被YSave代替
 */
@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressWarnings("unused")
@Deprecated
public class YObjectStorage {
    // 缓存，临时缓存
    private static volatile HashMap<String, Object> cache;
    private static final String FILENAME = "Object.properties";
    private final String path;
    private static volatile boolean useCache = true;

    // ★★★★★★★★★★★★★★★★★★★★★★★静态方法开始★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // 写入
    public static <Object extends Serializable> void put(String path, String key, Object object) {
        YObjectStorage objectStorage = new YObjectStorage(path);
        objectStorage.put(key, object);
    }

    // 写入
    public static <Object extends Serializable> void put(Context context, String key, Object object) {
        YObjectStorage objectStorage = new YObjectStorage(context);
        objectStorage.put(key, object);
    }

    // 写入
    public static <Object extends Serializable> void put(Context context, String fileName, String key, Object object) {
        YObjectStorage objectStorage = new YObjectStorage(context, fileName);
        objectStorage.put(key, object);
    }

    // 读取
    public static Object get(String path, String key) {
        YObjectStorage objectStorage = new YObjectStorage(path);
        return objectStorage.get(key);
    }

    // 读取
    public static Object get(String path, String key, Object defaultObject) {
        YObjectStorage objectStorage = new YObjectStorage(path);
        return objectStorage.read(key, defaultObject);
    }

    // 读取
    public static Object get(Context context, String key) {
        YObjectStorage objectStorage = new YObjectStorage(context);
        return objectStorage.get(key);
    }

    // 读取
    public static Object get(Context context, String key, Object defaultObject) {
        YObjectStorage objectStorage = new YObjectStorage(context);
        return objectStorage.read(key, defaultObject);
    }

    // 读取
    public static Object get(Context context, String fileName, String key) {
        YObjectStorage objectStorage = new YObjectStorage(context, fileName);
        return objectStorage.get(key);
    }

    // 读取
    public static Object get(Context context, String fileName, String key, Object defaultObject) {
        YObjectStorage objectStorage = new YObjectStorage(context, fileName);
        return objectStorage.read(key, defaultObject);
    }

    // 删除
    public static void remove(Context context, String key) {
        YObjectStorage objectStorage = new YObjectStorage(context);
        objectStorage.remove(key);
    }

    // 删除
    public static void remove(Context context, String fileName, String key) {
        YObjectStorage objectStorage = new YObjectStorage(context, fileName);
        objectStorage.remove(key);
    }

    // 删除
    public static void remove(String path, String key) {
        YObjectStorage objectStorage = new YObjectStorage(path);
        objectStorage.remove(key);
    }

    // 删除全部
    public static void removeAll(Context context) {
        YObjectStorage objectStorage = new YObjectStorage(context);
        objectStorage.removeAll();
    }

    // 删除全部
    public static void removeAll(String path) {
        YObjectStorage objectStorage = new YObjectStorage(path);
        objectStorage.removeAll();
    }

    // 删除全部
    public static void removeAll(Context context, String fileName) {
        YObjectStorage objectStorage = new YObjectStorage(context, fileName);
        objectStorage.removeAll();
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★静态方法结束★★★★★★★★★★★★★★★★★★★★★★★★★★★


    public static boolean isUseCache() {
        return useCache;
    }

    public static void setUseCache(boolean useCache) {
        YObjectStorage.useCache = useCache;
    }

    // 获取缓存
    private synchronized static HashMap<String, Object> getCache() {
        if (cache == null) {
            cache = new HashMap<>();
        }
        return cache;
    }

    /**
     * 存放在任意路径
     *
     * @param path 路径
     */
    public YObjectStorage(String path) {
        this.path = path;
    }

    /**
     * 存放在默认位置, 获取data/data/ObjectStorage目录
     *
     * @param context context
     */
    public YObjectStorage(Context context) {
        //path = context.getDir("ObjectStorage", Context.MODE_PRIVATE).getPath() + File.separator + FILENAME;
        path = context.getFilesDir() + File.separator + "ObjectStorage" + File.separator + FILENAME;
    }

    /**
     * 存放在默认位置，但是自己自定文件名,获取data/data/ObjectStorage目录+fileName
     *
     * @param context  context
     * @param fileName 文件名
     */
    public YObjectStorage(Context context, String fileName) {
        //path = context.getDir("ObjectStorage", Context.MODE_PRIVATE).getPath() + File.separator + fileName;
        path = context.getFilesDir() + File.separator + "ObjectStorage" + File.separator + FILENAME;
    }

    // 写入
    public <T extends Serializable> void put(String key, T object) {
        if (object == null) {
            remove(key);
            return;
        }
        getCache().put(key, object);
        // 然后写盘
        try {
            String value = YConvert.object2Base64(object);
            YPropertiesUtils.set(path, key, value);
        } catch (Exception e) {
            YLog.e("ObjectStorage", "存盘错误" + e.getMessage());
        }
    }

    // 删除
    public void remove(String key) {
        getCache().remove(key);
        YPropertiesUtils.remove(path, key);
    }

    // 删除全部
    public void removeAll() {
        getCache().clear();
        YPropertiesUtils.removeAll(path);
    }

    // 读取
    public Object get(String key) {
        return read(key, null);
    }

    // 读取
    public Object read(String key, Object defaultObject) {
        Object object;
        if (useCache) {
            object = getCache().get(key);
            if (object != null) {
                return object;
            }
        }
        // 读盘
        String value = YPropertiesUtils.get(path, key);
        if (value == null) {
            return defaultObject;
        }
        // 转成对象
        object = YConvert.base642Object(value);
        if (object == null) {
            YPropertiesUtils.remove(path, key);
            return defaultObject;
        }
        getCache().put(key, object);// 保存到内存中
        return object;
    }
}
