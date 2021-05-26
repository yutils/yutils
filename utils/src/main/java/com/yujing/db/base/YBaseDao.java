package com.yujing.db.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yujing.utils.YLog;

import java.util.List;

/**
 * 数据库操作基类
 */
@SuppressWarnings("ALL")
/*用法举例

//初始化
YUtils.init(this)
//然后创建Dao继承YBaseDao

//如：
public class UserDao extends YBaseDao<User> {
    @Override
    public SQLiteDatabase getDB() {
        //return YDB.getDB("test.db");//无版本控制
        //有版本控制
        OnUpgradeListener listener = (db, oldVersion, newVersion) -> {
            if (oldVersion != newVersion && oldVersion <= 1)
                db.execSQL("alter table " + tableName() + " add `addr` TEXT");
        };
        return YDB.getHelper("test.db", 2).setOnUpgradeListener(listener).getDatabase();
    }

    @Override
    public String tableName() {
        return "user";
    }

    @Override
    public String createTableSql() {
        return "CREATE TABLE IF NOT EXISTS " + tableName() + " (id INTEGER,account TEXT,password TEXT)";
    }

    @Override
    public List<User> cursorToList(Cursor cursor) {
        List<User> users = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex("id")));
                user.setAccount(cursor.getString(cursor.getColumnIndex("account")));
                user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                users.add(user);
            }
            cursor.close();
        }
        return users;
    }

    @Override
    public ContentValues dataToValues(User data) {
        ContentValues values = new ContentValues();
        values.put("id", data.getId());
        values.put("account", data.getAccount());
        values.put("password", data.getPassword());
        return values;
    }

    //插入或更新，先查询，如果有就更新，否则插入
    public void insertOrUpdateById(User data) {
        Cursor cursor = getDB().query(tableName(), new String[]{"id"}, "id=?", new String[]{data.getId().toString()}, null, null, null);
        if (cursor.getCount() > 0) updateById(data);
        else insert(data);
    }

    public long updateById(User data) {
        return update(data, "id=?", data.getId().toString());
    }

    public List<User> queryByIdAndAccount(String id, String account) {
        return query("id=? AND account=?", id, account);
    }
}
 */
public abstract class YBaseDao<T> {

    abstract public SQLiteDatabase getDB();

    abstract public String tableName();

    //return "CREATE TABLE IF NOT EXISTS " + tableName() + " (id INTEGER,account TEXT,password TEXT)";
    abstract public String createTableSql();

    abstract public List<T> cursorToList(Cursor cursor);

    abstract public ContentValues dataToValues(T data);

    public YBaseDao() {
        //创建表
        createTable();
    }

    // 插入一条记录
    public long insert(T data) {
        return getDB().insert(tableName(), null, dataToValues(data));
    }

    // 查询全部
    public List<T> query() {
        Cursor cursor = getDB().query(tableName(), null, null, null, null, null, null);
        return cursorToList(cursor);
    }

    // 查询
    public List<T> query(String whereClause, String... whereArgs) {
        Cursor cursor = getDB().query(tableName(), null, whereClause, whereArgs, null, null, null);
        return cursorToList(cursor);
    }

    // 更新
    public long update(T data, String whereClause, String... whereArgs) {
        return (long) getDB().update(tableName(), dataToValues(data), whereClause, whereArgs);
    }

    // 删除
    public long delete(String whereClause, String... whereArgs) {
        return getDB().delete(tableName(), whereClause, whereArgs);
    }

    // 删除
    public long delete() {
        return getDB().delete(tableName(), null, null);
    }

    //创建表
    public void createTable() {
        getDB().execSQL(createTableSql());
    }

    //删除当前表
    public void dropTable() {
        YLog.i("删除表：" + tableName());
        getDB().execSQL("DROP TABLE IF EXISTS " + tableName());
    }
}
