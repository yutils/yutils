package com.yujing.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yujing.db.base.YBaseDao;

import java.util.ArrayList;
import java.util.List;

/*用法

当这样获取db时需要初始化。
YDB.getHelper("test.db").getDatabase();

//初始化
YUtils.init(this)
//绑定，如果不绑定，当数据库版本有变化时候，其他表收不到变化信息
YDB.getDefault().binding(UserDao())
//或
YDB.getHelper("test.db", 2).binding(UserDao()).binding(InfoDao())

//再之后，自己写一个类继承YBaseDao

 */
public class YHelper extends SQLiteOpenHelper {
    public List<YBaseDao<?>> daoAll = new ArrayList<>();

    public YHelper binding(YBaseDao<?> dao) {
        for (YBaseDao<?> item : daoAll) if (item.tableName().equals(dao.tableName())) return this;
        daoAll.add(dao);
        return this;
    }

    public YHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    // 以读写的形式打开数据库，当磁盘满后只能调用读方法
    public SQLiteDatabase getDatabase() {
        return getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (YBaseDao<?> item : daoAll) item.onUpgrade(db, oldVersion, newVersion);
    }

    public void onDestroy() {
        daoAll.clear();
        getDatabase().close();
    }
}
