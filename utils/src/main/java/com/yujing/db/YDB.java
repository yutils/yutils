package com.yujing.db;

import android.database.sqlite.SQLiteDatabase;

import com.yujing.db.helper.YHelper;
import com.yujing.utils.YApp;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取一个SQLiteDatabase
 *
 * @author 余静 2021年5月26日10:53:32
 */
/*用法
//初始化
YUtils.init(this)

//获取一个Database
SQLiteDatabase db = YDB.getDB("test.db");
//或
SQLiteDatabase db = YDB.getHelper("test.db", 2).setOnUpgradeListener(onUpgradeListener).getDatabase();
 */
public class YDB {
    static List<YHelper> yHelpers = new ArrayList<>();

    /**
     * 获取一个YHelper,如果不存在就创建。
     *
     * @param dbName  数据库名称
     * @param version 版本号
     * @return YHelper
     */
    public static YHelper getHelper(String dbName, int version) {
        //已存在就不添加
        for (YHelper item : yHelpers) if (item.getDatabaseName().equals(dbName)) return item;
        YHelper yHelper = new YHelper(YApp.get(), dbName, version);
        yHelpers.add(yHelper);
        return yHelper;
    }

    /**
     * 创建并获取一个SQLiteDatabase
     *
     * @param dbName 数据库名称
     * @return SQLiteDatabase
     */
    public static SQLiteDatabase getDB(String dbName) {
        return SQLiteDatabase.openOrCreateDatabase(YApp.get().getDatabasePath(dbName), null);
    }

    /**
     * 程序退出时候调用，释放所有已经存在的Helper
     */
    public static void onDestroy() {
        for (YHelper item : yHelpers) item.onDestroy();
        yHelpers.clear();
    }
}
