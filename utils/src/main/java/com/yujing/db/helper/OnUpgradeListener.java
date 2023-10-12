package com.yujing.db.helper;

import android.database.sqlite.SQLiteDatabase;

/**
 * 版本更新监听
 *
 * @author 余静 2021年5月26日10:33:10
 */
public interface OnUpgradeListener {
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
