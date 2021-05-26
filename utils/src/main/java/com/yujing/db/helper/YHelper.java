package com.yujing.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yujing.db.base.YBaseDao;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取一个SQLiteOpenHelper
 * @author 余静 2021年5月26日10:55:21
 */
/*用法
//初始化
YUtils.init(this)

//获得一个Helper，Database
YHelper yHelper = new YHelper(YApp.get(), dbName, version);
SQLiteDatabase db = yHelper.setOnUpgradeListener(listener).getDatabase();

//或，获取一个Database
SQLiteDatabase db = YDB.getHelper("test.db", 2).setOnUpgradeListener(onUpgradeListener).getDatabase();
 */
public class YHelper extends SQLiteOpenHelper {
   private OnUpgradeListener onUpgradeListener;

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
        onUpgradeListener.onUpgrade(db,oldVersion,newVersion);
    }

    public YHelper setOnUpgradeListener(OnUpgradeListener onUpgradeListener) {
        this.onUpgradeListener = onUpgradeListener;
        return this;
    }

    public void onDestroy() {
        getDatabase().close();
    }
}
