package com.yujing.db.control;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yujing.utils.YLog;

import java.util.List;

/**
 * SQLite常用方法
 */
public class YSQLite {
    //执行一条sql，比如：insert,create,delete
    public static void exec(SQLiteDatabase db, String sql) {
        db.execSQL(sql);
    }

    //sql查询返回
    public static Cursor query(SQLiteDatabase db, String sql) {
        return query(db, sql, null);
    }

    //sql查询返回
    public static Cursor query(SQLiteDatabase db, String sql, String[] args) {
        return db.rawQuery(sql, args);
    }

    //返回一张表中全部数据
    public static Cursor query(SQLiteDatabase db, String tableName, String whereClause, String... whereArgs) {
        return db.query(tableName, null, whereClause, whereArgs, null, null, null);
    }

    //返回一张表中全部数据
    public static Cursor queryAll(SQLiteDatabase db, String tableName) {
        return db.query(tableName, null, null, null, null, null, null);
    }

    //根据表名插入一条数据
    public static void insert(SQLiteDatabase db, ContentValues values, String tableName) {
        db.insert(tableName, null, values);
    }

    //删除一条数据
    public static void del(SQLiteDatabase db, String tableName, String whereClause, String... whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
    }

    //根据id删除一条数据
    public static void delById(SQLiteDatabase db, String tableName, int id) {
        del(db, tableName, "id=?", String.valueOf(id));
    }

    //删除全部
    public static long delAll(SQLiteDatabase db, String tableName) {
        return db.delete(tableName, null, null);
    }

    //创建一张表
    public static void createTable(SQLiteDatabase db, String createSQL) {
        YLog.i("创建表：" + createSQL);
        exec(db, createSQL);
    }

    //创建全部表
    public static void createTables(SQLiteDatabase db, List<String> createSQLs) {
        for (String sql : createSQLs) createTable(db, sql);
    }

    //删除一张表
    public static String dropTable(String tableName) {
        return "DROP TABLE IF EXISTS `" + tableName + "`";
    }

    //删除一张表
    public static void dropTable(SQLiteDatabase db, String tableName) {
        YLog.i("删除表：" + dropTable(tableName));
        exec(db, dropTable(tableName));
    }

    //删除全部表
    public static void dropTable(SQLiteDatabase db, List<String> tableNames) {
        for (String name : tableNames) dropTable(db, name);
    }
}
