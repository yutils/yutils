package com.yujing.db;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 创建SQL语句，
 * 如YCreateSQL.create（user）; 返回字符串 CREATE TABLE IF NOT EXISTS `User`(`id` TEXT NULL,`account` TEXT）
 *
 * @author 余静
 */
@Deprecated
public class YCreateSQL {
    // CREATE TABLE IF NOT EXISTS `User`(`id` TEXT NULL,`account` TEXT
    // NULL,`name` TEXT NULL,`phone` TEXT NULL,`nickname` TEXT NULL,`age` TEXT
    // NULL,`sex` TEXT NULL,`password` TEXT NULL,`photourl` TEXT NULL)
    public static String create(Class<?> cls) {
        // 创建表
        String tempvalue = "";
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!Modifier.isFinal(field.getModifiers()))
                tempvalue += "`" + field.getName() + "`" + " TEXT NULL,";
        }
        tempvalue = tempvalue.substring(0, tempvalue.lastIndexOf(","));
        return "CREATE TABLE IF NOT EXISTS `" + cls.getSimpleName() + "` (" + tempvalue + ")";
    }

    //创建一张表 "CREATE TABLE IF NOT EXISTS `tableName` (account varchar(50),password TEXT)"
    public static String create(String tableName, Map<String, String> fields) {
        StringBuilder tempValue = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet())
            tempValue.append("`").append(entry.getKey()).append("` ").append(entry.getValue()).append(",");
        tempValue = new StringBuilder(tempValue.substring(0, tempValue.lastIndexOf(",")));
        return "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + tempValue + ")";
    }

    // INSERT INTO 'User'(id,psd,name,age) VALUES ('1','123456','yu','23')
    public static String insert(Object object) {
        return "INSERT INTO " + "`" + object.getClass().getSimpleName() + "`" + getKeyValue(object);
    }

    // SELECT * FROM 'User'
    public static String query(Class<?> cls) {
        return "SELECT * FROM `" + cls.getSimpleName() + "`";
    }

    // SELECT * FROM 'User' WHERE id='1' AND name='张三'
    public static String query(Class<?> cls, Map<String, Object> where) {
        return "SELECT * FROM `" + cls.getSimpleName() + "`" + getWheresValue(where, "=");
    }

    // SELECT count(*) FROM 'User'
    public static String count(Class<?> cls) {
        return "SELECT count(*) FROM `" + cls.getSimpleName() + "`";
    }

    // SELECT count(*) FROM 'User' WHERE id='1' AND name='张三'
    public static String count(Class<?> cls, Map<String, Object> where) {
        return "SELECT count(*) FROM `" + cls.getSimpleName() + "`" + getWheresValue(where, "=");
    }

    // DELETE FROM 'User'
    public static String delete(Class<?> cls) {
        return "DELETE FROM `" + cls.getSimpleName() + "`";
    }

    // DELETE FROM 'User' WHERE id='1' AND name='张三'
    public static String delete(Class<?> cls, Map<String, Object> where) {
        return "DELETE FROM `" + cls.getSimpleName() + "`" + getWheresValue(where, "=");
    }

    // DROP TABLE IF EXISTS 'User'
    public static String deleteTable(Class<?> cls) {
        return "DROP TABLE IF EXISTS `" + cls.getSimpleName() + "`";
    }

    // UPDATE 'User' SET id='1',psd='123456',name='yu',age='23' WHERE id='1' AND
    // name='张三'
    public static String update(Class<?> cls, Object object, Map<String, Object> where) {
        return "UPDATE `" + cls.getSimpleName() + "`" + getSetValue(object) + getWheresValue(where, "=");
    }

    // 是集合
    public static boolean isCollectionType(Object obj) {
        return (obj.getClass().isArray() || (obj instanceof Collection) || (obj instanceof Hashtable) || (obj instanceof HashMap) || (obj instanceof HashSet) || (obj instanceof List) || (obj instanceof AbstractMap));
    }

    // 是集合
    public static boolean isCollectionType(Class<?> typeClass) {
        return (typeClass.isArray() || (typeClass.isAssignableFrom(Collection.class)) || (typeClass.isAssignableFrom(Hashtable.class)) || (typeClass.isAssignableFrom(HashMap.class)) || (typeClass.isAssignableFrom(HashSet.class)) || (typeClass.isAssignableFrom(List.class)) || (typeClass.isAssignableFrom(AbstractMap.class)));
    }

    // 是复杂类
    public static boolean isComplexType(Object obj) {
        return !(obj instanceof Boolean) && !(obj instanceof Short) && !(obj instanceof Byte) && !(obj instanceof Integer) && !(obj instanceof Long) && !(obj instanceof Float) && !(obj instanceof Character) && !(obj instanceof Double) && !(obj instanceof String) && isComplexType(obj.getClass());
    }

    // 是复杂类
    public static boolean isComplexType(Class<?> objectClass) {
        return objectClass != boolean.class && objectClass != Boolean.class && objectClass != short.class && objectClass != Short.class && objectClass != byte.class && objectClass != Byte.class && objectClass != int.class && objectClass != Integer.class && objectClass != long.class && objectClass != Long.class && objectClass != float.class && objectClass != Float.class && objectClass != char.class && objectClass != Character.class && objectClass != double.class && objectClass != Double.class && objectClass != String.class;
    }

    // (`id`,`account`,`name`,`phone`,`nickname`,`age`,`sex`,`password`,`photourl`)
    // VALUES ('11','','余','','雨季','123','13','','')
    private static String getKeyValue(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return "";
        }
        String tempKey = "";
        String tempValues = "";
        Gson gson = new Gson();
        for (Field field : fields) {
            field.setAccessible(true);// 获取私有变量值
            try {
                Object obj = field.get(object);
                if (!Modifier.isFinal(field.getModifiers())) {// 不是常量
                    if (obj != null) {
                        if (!isComplexType(obj)) {// 不是复杂类型直接存放
                            tempKey += "`" + field.getName() + "`" + ",";
                            tempValues += "'" + obj.toString() + "',";
                        } else {// 是复杂类型直接序列化
                            tempKey += "`" + field.getName() + "`" + ",";
                            tempValues += "'" + gson.toJson(obj) + "',";
                        }
                    } else {
                        tempKey += "`" + field.getName() + "`" + ",";
                        tempValues += " null ,";
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (tempKey.length() > 0) tempKey = tempKey.substring(0, tempKey.lastIndexOf(","));
        if (tempValues.length() > 0)
            tempValues = tempValues.substring(0, tempValues.lastIndexOf(","));
        return "(" + tempKey + ") VALUES (" + tempValues + ")";
    }

    // WHERE id='1' AND name='张三'
    private static String getWheresValue(Map<String, Object> where, String Symbol) {
        if (where == null) return "";
        Gson gson = new Gson();
        String tempwhere = " WHERE ";
        for (Map.Entry<String, Object> entry : where.entrySet()) {
            Object obj = entry.getValue();
            if (obj != null) {
                if (isComplexType(obj)) {// 如果是复杂类
                    tempwhere += "`" + entry.getKey() + "`" + " " + Symbol + "'" + gson.toJson(entry.getValue()) + "' AND ";
                } else {
                    tempwhere += "`" + entry.getKey() + "`" + " " + Symbol + "'" + entry.getValue().toString() + "' AND ";
                }
            } else {
                tempwhere += "`" + entry.getKey() + "`" + " is null AND ";
            }
        }
        tempwhere = (tempwhere.length() > 0 ? tempwhere.substring(0, tempwhere.lastIndexOf(" AND ")) : "");
        return tempwhere;
    }

    // SET id='1',psd='123456',name='yu',age='23'
    private static String getSetValue(Object object) {
        if (object == null) return "";
        String temp = " SET ";
        Gson gson = new Gson();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);// 获取私有变量值
            try {
                Object obj = field.get(object);
                // 不是常量
                if (!Modifier.isFinal(field.getModifiers())) {
                    if (obj != null) {// 如果该值为空就插入空串
                        if (!isComplexType(obj)) {// 不是复杂类型直接存放
                            temp += "`" + field.getName() + "`" + "='" + obj.toString() + "',";
                        } else {// 是复杂类型直接序列化
                            temp += "`" + field.getName() + "`" + "='" + gson.toJson(obj) + "',";
                        }
                    } else {
                        temp += "`" + field.getName() + "`" + "= null ,";
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        temp = temp.substring(0, temp.lastIndexOf(","));
        return temp;
    }
}