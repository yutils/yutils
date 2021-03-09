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

当这样获取db时需要初始化。
YDB.getHelper("test.db").getDatabase();
//初始化
YUtils.init(this)
//绑定，如果不绑定，当数据库版本有变化时候，其他表收不到变化信息
YDB.getDefault().binding(UserDao())
//或
YDB.getHelper("test.db", 2).binding(UserDao()).binding(InfoDao())


public class UserDao extends YBaseDao {
    @Override
    public SQLiteDatabase getDB() {
        // return SQLiteDatabase.openOrCreateDatabase(YPath.getFilePath(App.Companion.get()) + "/test.db", null);
        return YDB.getHelper("test.db", 2).binding(this).getDatabase();
    }

    @Override
    public String tableName() {
        return "user";
    }

    @Override
    public String createTableSql() {
        //return YCreateSQL.create(User.class);

        //Map<String,String> map=new HashMap<>();
        //map.put("account","varchar(50)");
        //map.put("password","TEXT");
        //return YCreateSQL.create("user",map);

        return "CREATE TABLE IF NOT EXISTS " + tableName() + " (account varchar(50),password TEXT)";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public List<?> cursorToDatas(Cursor cursor) {
        List<User> datas = new ArrayList<User>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                User data = new User();
                data.setAccount(cursor.getString(cursor.getColumnIndex("account")));
                data.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                datas.add(data);
            }
            cursor.close();
        }
        return datas;
    }


    @Override
    public ContentValues dataToValues(Object data) {
        User history = (User) data;
        ContentValues values = new ContentValues();
        values.put("account", history.getAccount());
        values.put("password", history.getPassword());
        return values;
    }

    //插入或更新
    public void insertOrUpdate(User data) {
        // 查询，如果account不为0那么就修改，否则插入
        Cursor cursor = getDB().query(tableName(), new String[]{"account"}, "account=?", new String[]{data.getAccount()}, null, null, null);
        if (cursor.getCount() > 0) update(data);
        else insert(data);
    }

    public long update(User data) {
        return update(data, "account=?", data.getAccount());
    }

    public List<User> queryByAccount(String account) {
        return (List<User>) query("account=?", account);
    }

    public List<User> queryByAccount(String account, String password, String unit) {
        return (List<User>) query("account=? AND password=? AND unit=?", account, password, unit);
    }
}
 */
public abstract class YBaseDao<T> {
    abstract public SQLiteDatabase getDB();

    abstract public String tableName();

    abstract public String createTableSql();

    abstract public List<T> cursorToDatas(Cursor cursor);

    abstract public ContentValues dataToValues(T data);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ }

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
        return cursorToDatas(cursor);
    }

    // 查询
    public List<T> query(String whereClause, String... whereArgs) {
        Cursor cursor = getDB().query(tableName(), null, whereClause, whereArgs, null, null, null);
        return cursorToDatas(cursor);
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

    //删除一张表
    public void dropTable() {
        YLog.i("删除表：" + tableName());
        getDB().execSQL("DROP TABLE IF EXISTS " + tableName());
    }
}
