package com.yujing.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Properties配置工具类
 *
 * @author 余静 2018年5月15日19:00:17
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class YPropertiesUtils {
    // ppts的注释
    private static final String NOTES = "this is YuJing property";

    public static void setValue(String propertyFilePath, HashMap<String, String> htKeyValue) {
        createFile(propertyFilePath);
        set(propertyFilePath, htKeyValue);
    }

    public static boolean setValue(String propertyFilePath, String key, String value) {
        createFile(propertyFilePath);
        HashMap<String, String> ht = new HashMap<>();
        ht.put(key, value);
        return set(propertyFilePath, ht);
    }


    public static String getValue(String propertyFilePath, String key) {
        Properties ppts = loadPropertyFile(propertyFilePath);
        return ppts == null ? null : ppts.getProperty(key);
    }

    public static String getValue(String propertyFilePath, String key, boolean isAbsolutePath) {
        if (isAbsolutePath) {
            Properties ppts = loadPropertyFileByFileSystem(propertyFilePath);
            return ppts == null ? null : ppts.getProperty(key);
        }
        return getValue(propertyFilePath, key);
    }

    public static Map<String, String> getAll(String propertyFilePath) {
        Properties ppts = loadPropertyFile(propertyFilePath);
        Map<String, String> map = new HashMap<>();
        if (ppts == null) {
            return map;
        }
        String key;
        String value;
        Enumeration<?> en = ppts.propertyNames();
        if (en == null) {
            return map;
        }
        while (en.hasMoreElements()) {
            key = (String) en.nextElement();
            value = ppts.getProperty(key);
            map.put(key, value);
        }
        return map;
    }

    public static void removeValue(String propertyFilePath, String key) {
        removeValue(propertyFilePath, new String[]{key});
    }


    public static boolean removeValue(String propertyFilePath, String[] key) {
        if (key == null) {
            System.out.println("key[] is null!");
            return false;
        }
        Properties ppts = loadPropertyFile(propertyFilePath);
        if (ppts == null) {
            return false;
        }
        for (String strKey : key) {
            ppts.remove(strKey);
        }
        return commit(propertyFilePath, ppts);
    }


    public static boolean removeAll(String propertyFilePath) {
        Properties ppts = loadPropertyFile(propertyFilePath);
        if (ppts == null) {
            return false;
        }
        ppts.clear();
        return commit(propertyFilePath, ppts);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createFile(String propertyFilePath) {
        java.io.File file = new java.io.File(propertyFilePath);
        java.io.File fileParent = file.getParentFile();
        if (!Objects.requireNonNull(fileParent).exists()) {
            fileParent.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Properties loadPropertyFile(String propertyFilePath) {
        java.io.InputStream is = YPropertiesUtils.class.getResourceAsStream(propertyFilePath);
        if (is == null) {
            return loadPropertyFileByFileSystem(propertyFilePath);
        }
        Properties ppts = new Properties();
        try {
            ppts.load(is);
            return ppts;
        } catch (Exception e) {
            System.out.println("加载属性文件出错:" + propertyFilePath + "\n错误:" + e.getMessage());
            return null;
        }
    }

    private static Properties loadPropertyFileByFileSystem(final String propertyFilePath) {
        try {
            Properties ppts = new Properties();
            ppts.load(new java.io.FileInputStream(propertyFilePath));
            return ppts;
        } catch (FileNotFoundException e) {
            System.out.println("FileInputStream(\"" + propertyFilePath + "\")! FileNotFoundException: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Properties.load(InputStream)! IOException: " + e.getMessage());
            return null;
        }
    }

    private static boolean set(String propertyFilePath, HashMap<String, String> htKeyValue) {
        Properties ppts = loadPropertyFile(propertyFilePath);
        if (ppts == null || htKeyValue == null) {
            return false;
        }
        ppts.putAll(htKeyValue);
        return commit(propertyFilePath, ppts);
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    private static boolean commit(String propertyFilePath, Properties ppts) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(propertyFilePath);
        } catch (FileNotFoundException e) {
            String path = YPropertiesUtils.class.getResource(propertyFilePath).getPath();
            try {
                stream = new FileOutputStream(path);
            } catch (FileNotFoundException e1) {
                System.out.println("文件没有找到!" + e1.getMessage());
                return false;
            }
        } finally {
            if (stream == null) {
                return false;
            }
        }
        try {
            ppts.store(stream, NOTES);
            return true;
        } catch (IOException e) {
            System.out.println("文件写入错误" + e.getMessage());
            return false;
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        TEST();
    }

    public static void TEST() {
        String path = "tt.properties";
        HashMap<String, String> ht = new HashMap<>();
        ht.put("name", "00");
        // 创建或修改
        YPropertiesUtils.setValue(path, ht);
        // 删除
        YPropertiesUtils.removeValue(path, new String[]{"age", "age2"});
        // 删除
        YPropertiesUtils.removeValue(path, "age7777");
        // 取值
        String v = YPropertiesUtils.getValue(path, "name");
        System.out.println("value1 = " + v);
        // 取值全部
        Map<String, String> ht2 = YPropertiesUtils.getAll(path);
        System.out.println(ht2.get("age2"));
    }
}
