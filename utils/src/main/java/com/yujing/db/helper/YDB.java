package com.yujing.db.helper;

import android.database.sqlite.SQLiteDatabase;

import com.yujing.utils.YApp;

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
public class YDB {
    static List<YHelper> yHelpers = new ArrayList<>();
    public static String default_dbName = "default";
    public static int default_version = 1;

    //设置默认的数据库文件名称，和版本号
    public static void setDefault(String dbName, int version) {
        default_dbName = dbName;
        default_version = version;
    }

    //获取一个YHelper,如果不存在就创建。
    public static YHelper getHelper(String dbName, int version) {
        //已存在就不添加
        for (YHelper item : yHelpers) if (item.getDatabaseName().equals(dbName)) return item;
        YHelper yHelper = new YHelper(YApp.get(), dbName, version);
        yHelpers.add(yHelper);
        return yHelper;
    }

    //获取一个YHelper,如果不存在就创建。
    public static YHelper getHelper(String dbName) {
        for (YHelper item : yHelpers) if (item.getDatabaseName().equals(dbName)) return item;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(YApp.get().getDatabasePath(dbName), null);
        YHelper yHelper = new YHelper(YApp.get(), dbName, db.getVersion());
        yHelpers.add(yHelper);
        return yHelper;
    }

    //获取一个默认的YHelper，如果不存在就创建。
    public static YHelper getDefault() {
        return getHelper(default_dbName, default_version);
    }

    //程序退出时候调用
    public static void onDestroy() {
        for (YHelper item : yHelpers) item.onDestroy();
        yHelpers.clear();
    }
}
