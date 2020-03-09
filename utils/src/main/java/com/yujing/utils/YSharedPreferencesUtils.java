package com.yujing.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences文件工具类
 *
 * @author 余静 2018年5月15日19:00:17
 */
@SuppressWarnings("unused")
public class YSharedPreferencesUtils {
    public static final String DEFAULT_FILE = "defaultSharedPreferences";

    /**
     * 向SharedPreferences文件中写入key和value
     * Example：SharedPreferencesUtils.writeInSharedPreferences
     * (LoginActivity.this, "role", Context.MODE_PRIVATE, "role", resultStr);
     *
     * @param context  Android上下文
     * @param fileName 文件名
     * @param mode     读写模式
     * @param key      写入的键
     * @param value    写入的值
     */
    public static void write(Context context, String fileName, int mode, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, mode);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void write(Context context, String fileName, String key, String value) {
        write(context, fileName, Context.MODE_PRIVATE, key, value);
    }

    public static void write(Context context, String key, String value) {
        write(context, DEFAULT_FILE, Context.MODE_PRIVATE, key, value);
    }

    //----------int--------
    public static void writeInt(Context context, String fileName, int mode, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, mode);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void writeInt(Context context, String fileName, String key, int value) {
        writeInt(context, fileName, Context.MODE_PRIVATE, key, value);
    }

    public static void writeInt(Context context, String key, int value) {
        writeInt(context, DEFAULT_FILE, Context.MODE_PRIVATE, key, value);
    }

    //----------Boolean--------
    public static void writeBoolean(Context context, String fileName, int mode, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, mode);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void writeBoolean(Context context, String fileName, String key, boolean value) {
        writeBoolean(context, fileName, Context.MODE_PRIVATE, key, value);
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        writeBoolean(context, DEFAULT_FILE, Context.MODE_PRIVATE, key, value);
    }

    //----------delete--------
    public static void delete(Context context, String fileName, int mode, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, mode);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void delete(Context context, String key) {
        delete(context, DEFAULT_FILE, Context.MODE_PRIVATE, key);
    }

    public static void delete(Context context, String fileName, String key) {
        delete(context, fileName, Context.MODE_PRIVATE, key);
    }


    /**
     * 从SharedPreferences文件中读取指定Key的value
     *
     * @param context  Android上下文
     * @param fileName 文件名
     * @param mode     读写模式
     * @param key      读取的Key
     * @return key对应的值
     */
    public static String get(Context context, String fileName, int mode, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, mode);
        return sp.getString(key, null);// null为默认值
    }

    public static String get(Context context, String fileName, String key) {
        return get(context, fileName, Context.MODE_PRIVATE, key);// null为默认值
    }

    public static String get(Context context, String key) {
        return get(context, DEFAULT_FILE, Context.MODE_PRIVATE, key);// null为默认值
    }

    //--------------int------------
    public static int getInt(Context context, String fileName, int mode, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, mode);
        return sp.getInt(key, 0);// null为默认值
    }

    public static int getInt(Context context, String fileName, String key) {
        return getInt(context, fileName, Context.MODE_PRIVATE, key);// null为默认值
    }

    public static int getInt(Context context, String key) {
        return getInt(context, DEFAULT_FILE, Context.MODE_PRIVATE, key);// null为默认值
    }

    //--------------boolean-----------------
    public static boolean getBoolean(Context context, String fileName, int mode, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, mode);
        return sp.getBoolean(key, false);// null为默认值
    }

    public static boolean getBoolean(Context context, String fileName, String key) {
        return getBoolean(context, fileName, Context.MODE_PRIVATE, key);// null为默认值
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, DEFAULT_FILE, Context.MODE_PRIVATE, key);// null为默认值
    }
}
