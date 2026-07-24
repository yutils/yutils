package com.yujing.test.cases

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.yujing.db.YCreateSQL
import com.yujing.db.YDB
import com.yujing.db.base.YBaseDao
import com.yujing.db.helper.YHelper
import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory
import com.yujing.utils.YApp

/**
 * 数据库相关用例（临时库文件，测完关闭）
 */
object DbCases {
    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("db.createSql", "YCreateSQL 建表语句", TestCategory.OTHER) {
            class DemoRow {
                @JvmField
                var id: String? = null

                @JvmField
                var name: String? = null
            }

            val sql = YCreateSQL.create(DemoRow::class.java)
            require(sql.contains("CREATE TABLE IF NOT EXISTS")) { sql }
            require(sql.contains("`id`") && sql.contains("`name`")) { sql }

            val fields = LinkedHashMap<String, String>()
            fields["id"] = "TEXT"
            fields["score"] = "INTEGER"
            val sql2 = YCreateSQL.create("score_table", fields)
            require(sql2.contains("`score_table`") && sql2.contains("`score`")) { sql2 }

            val row = DemoRow().apply { id = "1"; name = "n" }
            val insert = YCreateSQL.insert(row)
            require(insert.contains("INSERT") && insert.contains("`id`")) { insert }
            val query = YCreateSQL.query(DemoRow::class.java)
            require(query.contains("SELECT") && query.contains("FROM")) { query }
            val where = LinkedHashMap<String, Any>()
            where["id"] = "1"
            val update = YCreateSQL.update(DemoRow::class.java, row, where)
            require(update.contains("UPDATE") && update.contains("WHERE")) { update }
            val del = YCreateSQL.delete(DemoRow::class.java, where)
            require(del.contains("DELETE") && del.contains("WHERE")) { del }
            val count = YCreateSQL.count(DemoRow::class.java)
            require(count.contains("COUNT") || count.contains("count") || count.contains("SELECT")) { count }
        },
        AutoTestCase("db.helper.crud", "YHelper 建表读写", TestCategory.OTHER) {
            val name = "ytest_${System.currentTimeMillis()}.db"
            val helper = YHelper(YApp.get(), name, 1)
            val db = helper.database
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS t_kv (k TEXT PRIMARY KEY, v TEXT)")
                db.execSQL("INSERT OR REPLACE INTO t_kv(k,v) VALUES('a','1')")
                db.rawQuery("SELECT v FROM t_kv WHERE k=?", arrayOf("a")).use { c ->
                    require(c.moveToFirst()) { "查无数据" }
                    require(c.getString(0) == "1") { "值=${c.getString(0)}" }
                }
            } finally {
                helper.onDestroy()
                YApp.get().deleteDatabase(name)
            }
        },
        AutoTestCase("db.helper.upgradeRefuse", "YHelper 无监听拒绝升级", TestCategory.OTHER) {
            val name = "ytest_up_${System.currentTimeMillis()}.db"
            // 先建 v1
            YHelper(YApp.get(), name, 1).also { it.database; it.onDestroy() }
            var threw = false
            try {
                // 升到 v2 且不设 listener，应抛
                YHelper(YApp.get(), name, 2).database
            } catch (_: IllegalStateException) {
                threw = true
            } finally {
                YApp.get().deleteDatabase(name)
            }
            require(threw) { "无 upgradeListener 时应拒绝升级" }
        },
        AutoTestCase("db.baseDao.insertQuery", "YBaseDao 插入查询", TestCategory.OTHER) {
            val name = "ytest_dao_${System.currentTimeMillis()}.db"
            val helper = YHelper(YApp.get(), name, 1)
            val db = helper.database
            try {
                val dao = object : YBaseDao<Kv>() {
                    override fun getDB(): SQLiteDatabase = db
                    override fun tableName(): String = "kv"
                    override fun createTableSql(): String =
                        "CREATE TABLE IF NOT EXISTS kv(k TEXT PRIMARY KEY, v TEXT)"

                    override fun cursorToList(cursor: Cursor): List<Kv> {
                        val list = mutableListOf<Kv>()
                        while (cursor.moveToNext()) {
                            list.add(Kv(cursor.getString(0), cursor.getString(1)))
                        }
                        return list
                    }

                    override fun dataToValues(data: Kv): ContentValues {
                        return ContentValues().apply {
                            put("k", data.k)
                            put("v", data.v)
                        }
                    }
                }
                dao.createTable()
                dao.insert(Kv("hello", "world"))
                val all = dao.query()
                require(all.any { it.k == "hello" && it.v == "world" }) { "query=$all" }
                dao.delete()
            } finally {
                helper.onDestroy()
                YApp.get().deleteDatabase(name)
            }
            // 顺带确认 YDB 入口可用
            val h2 = YDB.getHelper("ytest_ydb_tmp.db", 1)
            h2.database
            h2.onDestroy()
            YApp.get().deleteDatabase("ytest_ydb_tmp.db")
        },
    )

    data class Kv(val k: String, val v: String)
}
