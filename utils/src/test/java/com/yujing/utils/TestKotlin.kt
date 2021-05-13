package com.yujing.utils

import com.yujing.utils.YJson.toBean
import com.yujing.utils.YJson.toJson
import com.yujing.utils.YJson.toListMap
import com.yujing.utils.YJson.toMap
import org.junit.Test
import java.util.*

class TestKotlin {
    @Test
    fun run1() {
        var list: MutableList<String> = ArrayList()
        list.add("a")
        list.add("b")
        list.add("c")
        list.add("d")
        list.add("e")
        list.add("f")
//        val mIterator = list.iterator()
//        while (mIterator.hasNext()) {
//            val next = mIterator.next()
//            if (next == "b") mIterator.remove()
//            println(next)
//        }
    }

    @Test
    fun run2() {
        val user = User()
        user.name = "张三"
        user.password = "123456"
        user.date = Date()
        val user2 = User()
        user2.name = "李四"
        user2.password = null
        user2.date = Date()
        val users: MutableList<User> = ArrayList()
        users.add(user)
        users.add(user2)
        println("--------------------------------------------------obj转json--------------------------------------------------")
        var str = YJson.getGsonFormat().toJson(user)
        println(str)
        println(toBean(str, User::class.java))
        str = toJson(user, "yyyy年MM月dd日 HH:mm:ss")
        println(str)
        println(toBean(str, User::class.java, "yyyy年MM月dd日 HH:mm:ss"))
        println("--------------------------------------------------json转list--------------------------------------------------")
        str = toJson(users, "yyyy-MM-dd HH:mm:ss")
        println(str)
//        val users2 = toList(str, User::class.java)
//        for (item in users2) {
//            println("★" + item.javaClass.name)
//            println("★$item")
//        }
//        println("--------------------------------------------------json转list--------------------------------------------------")
//        val users3: List<User> = toList(str, object : TypeToken<List<User?>?>() {}.type)
//        for (item in users3) {
//            println("★" + item.javaClass.name)
//            println("★$item")
//        }
        println("--------------------------------------------------map转json--------------------------------------------------")
        val map: MutableMap<String, User> = HashMap()
        map["001"] = user
        map["002"] = user2
        str = toJson(map, "yyyy年MM月dd日 HH:mm:ss")
        println(str)
        println("--------------------------------------------------json转map--------------------------------------------------")
        val map2 = toMap(str, String::class.java, User::class.java, "yyyy年MM月dd日 HH:mm:ss")
        println(map2)
        println(map2["001"]!!.javaClass.name)
        println(map2["001"])
        println("--------------------------------------------------listMap转json--------------------------------------------------")
        val lms: MutableList<Map<String, User>> = ArrayList()
        lms.add(map)
        lms.add(map2)
        str = toJson(lms, "yyyy年MM月dd日 HH:mm:ss")
        println(str)
        println("--------------------------------------------------json转listMap--------------------------------------------------")
        val lms2 = toListMap(str, String::class.java, User::class.java, "yyyy年MM月dd日 HH:mm:ss")
        println(lms2)
        println(lms2[0]["001"]!!.javaClass.name)
        println(lms2[0]["001"])
    }
}