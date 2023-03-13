package com.yujing.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Json工具类，常用转换
 * @author 余静 2021年3月12日16:37:57
 */

/*
//参考网址：https://www.jianshu.com/p/3108f1e44155

//json字段重命名
@SerializedName("email_address")
public String address;

//json字段重命名，当上面的三个属性(email_address、email、emailAddress)都中出现任意一个时均可以得到正确的结果。当多种情况同时出时，以最后一个出现的值为准。
@SerializedName(value = "emailAddress", alternate = {"email", "email_address"})
public String emailAddress;

@Expose //过滤字段序列化和反序列化都生效
@Expose(deserialize = true,serialize = true) //序列化和反序列化都都生效，等价于上一条
@Expose(deserialize = true,serialize = false) //反序列化时生效
@Expose(deserialize = false,serialize = true) //序列化时生效
@Expose(deserialize = false,serialize = false) // 和不写注解一样

//该属性自1.2+版本开始弃用，当：new GsonBuilder().setVersion(double v);时，@Since(double v)、@Until(double v)才会起作用。
@Until(1.2)
private String mobile;

//该属性自1.9+版本 开始启用，当：new GsonBuilder().setVersion(double v);时，@Since(double v)、@Until(double v)才会起作用。
@Since(1.9)
private String nickName;

//常用方法
Gson gson = new GsonBuilder()
//序列化null
.serializeNulls()
// 设置日期时间格式，另有2个重载方法
// 在序列化和反序化时均生效
.setDateFormat("yyyy-MM-dd")
// 禁此序列化内部类
.disableInnerClassSerialization()
//生成不可执行的Json（多了 )]}' 这4个字符）
.generateNonExecutableJson()
//禁止转义html标签
.disableHtmlEscaping()
//格式化输出
.setPrettyPrinting()
.create();
 */

/*
//格式化json的Gson
GsonBuilder().setPrettyPrinting().create()

//格式化日期的Gson
GsonBuilder().setDateFormat(dateFormat).create()

//格式化日期的Gson
return GsonBuilder().registerTypeHierarchyAdapter(Date::class.java, JsonSerializer<Date> { src, _, _ ->
    val format = SimpleDateFormat(dateFormat)
    JsonPrimitive(format.format(src))
}).setDateFormat(dateFormat).create()

//格式化日期的Gson
val gson = GsonBuilder().registerTypeAdapter(Date::class.java, JsonDeserializer { json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext? ->
   val format = SimpleDateFormat(dateFormat)
   return@JsonDeserializer format.parse(json.asString)
}).setDateFormat(dateFormat).create()
 */

/*用法：
var str = toJson(user)
toBean(str, User::class.java)

var str = toJson(user, "yyyy年MM月dd日 HH:mm:ss")
toBean(str, User::class.java, "yyyy年MM月dd日 HH:mm:ss")

val users: MutableList<User> = ArrayList()
var str = toJson(user)
toList(str, User::class.java)
toList(str, object : TypeToken<List<User?>?>() {}.type)

val map: MutableMap<String, User> = HashMap()
var str = toJson(map, "yyyy年MM月dd日 HH:mm:ss")
toMap(str, String::class.java, User::class.java, "yyyy年MM月dd日 HH:mm:ss")

val lms: MutableList<Map<String, User>> = ArrayList()
var str = toJson(lms, "yyyy年MM月dd日 HH:mm:ss")
toListMap(str, String::class.java, User::class.java, "yyyy年MM月dd日 HH:mm:ss")
 */
object YJson {
    /**
     * 获取一个gson对象
     */
    val gson: Gson by lazy { Gson() }

    /**
     * 获取一个格式化日期的 gson对象
     */
    @JvmStatic
    fun getGsonDate(dateFormat: String?): Gson {
        return GsonBuilder().setDateFormat(dateFormat).create()
    }

    /**
     * 获取一个格式化json的gson对象
     */
    @JvmStatic
    fun getGsonFormat(): Gson {
        return GsonBuilder().setPrettyPrinting().create()
    }

    /**
     * 将对象转换成JsonObject格式
     */
    @JvmStatic
    fun toJsonObject(jsonStr: String?): Any {
        return JsonParser.parseString(jsonStr).asJsonObject
    }

    /**
     * 将对象转换成json格式(并自定义日期格式)
     */
    @JvmStatic
    fun toJson(obj: Any?, dateFormat: String? = null): String {
        val gson = if (dateFormat == null) gson else getGsonDate(dateFormat)
        return gson.toJson(obj)
    }

    /**
     * 将对象转换成json格式,并且格式化(并自定义日期格式)
     */
    @JvmStatic
    fun toJsonFormat(obj: Any?, dateFormat: String? = null): String? {
        val gson = if (dateFormat == null) gson else getGsonDate(dateFormat)
        return format(gson.toJson(obj))
    }

    /**
     * 将json转换成bean对象(并自定义日期格式)
     */
    @JvmStatic
    fun <T> toBean(jsonStr: String, cl: Class<T>?, dateFormat: String? = null): T {
        val gson = if (dateFormat == null) gson else getGsonDate(dateFormat)
        return gson.fromJson(jsonStr, cl)
    }

    /**
     * 将json转换成List
     */
    @JvmStatic
    fun <T> toList(json: String?, cls: Class<T>?, dateFormat: String? = null): List<T> {
        val gson = if (dateFormat == null) gson else getGsonDate(dateFormat)
        val list: MutableList<T> = ArrayList()
        val array = JsonParser.parseString(json).asJsonArray
        for (elem in array) {
            list.add(gson.fromJson(elem, cls))
        }
        return list
    }

    /**
     * 将json格式转换成list对象，并指定类型
     */
    @JvmStatic
    fun <T> toList(jsonStr: String?, type: Type?, dateFormat: String? = null): List<T> {
        val gson = if (dateFormat == null) gson else getGsonDate(dateFormat)
        return gson.fromJson(jsonStr, type)
    }

    /**
     * 转成map
     */
    @JvmStatic
    fun <T> toMap(json: String?, cls: Class<T>?, dateFormat: String? = null): Map<String, T> {
        val gson = if (dateFormat == null) gson else getGsonDate(dateFormat)
        val map: MutableMap<String, T> = HashMap()
        val jsonObject = JsonParser.parseString(json).asJsonObject
        for ((key, value) in jsonObject.entrySet()) {
            map[key] = gson.fromJson(value, cls)
        }
        return map
    }

    /**
     * 转成map
     */
    @JvmStatic
    fun <K, T> toMap(json: String?, kCls: Class<K>?, cls: Class<T>?, dateFormat: String? = null): Map<K, T> {
        val gson = if (dateFormat == null) gson else getGsonDate(dateFormat)
        val map: MutableMap<K, T> = HashMap()
        val jsonObject = JsonParser.parseString(json).asJsonObject
        for ((key, value) in jsonObject.entrySet()) {
            map[gson.fromJson(key, kCls)] = gson.fromJson(value, cls)
        }
        return map
    }

    /**
     * 将json转换成List<Map></Map><String></String>, T>>
     */
    @JvmStatic
    fun <T> toListMap(json: String?, cls: Class<T>?, dateFormat: String? = null): List<Map<String, T>> {
        val list: MutableList<Map<String, T>> = ArrayList()
        val array = JsonParser.parseString(json).asJsonArray
        for (elem in array) {
            list.add(toMap(elem.toString(), cls, dateFormat))
        }
        return list
    }

    /**
     * 将json转换成List<Map></Map><String></String>, T>>
     */
    @JvmStatic
    fun <K, T> toListMap(json: String?, kCls: Class<K>?, cls: Class<T>?, dateFormat: String? = null): List<Map<K, T>> {
        val list: MutableList<Map<K, T>> = ArrayList()
        val array = JsonParser.parseString(json).asJsonArray
        for (elem in array) {
            list.add(toMap(elem.toString(), kCls, cls, dateFormat))
        }
        return list
    }

    /**
     * json格式化
     *
     * @param str 目标字符串
     * @return 被格式化的字符串
     */
    @JvmStatic
    fun format(str: String?): String? {
        return try {
            val element = JsonParser.parseString(str)
            getGsonFormat().toJson(element)
        } catch (e: Exception) {
            str
        }
    }

    /**
     * 可以获取泛类类型
     * val type = object : TypeReference<T>() {}.type
     */
    abstract class TypeReference<T> : Comparable<TypeReference<T>> {
        val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        override fun compareTo(other: TypeReference<T>) = 0
    }

    /**
     * 实体转JSON
     */
    fun Any.toJson(): String {
        return gson.toJson(this)
    }

    /**
     * 实体转JSON
     */
    fun Any.encodeJson(): String {
        return gson.toJson(this)
    }

    /**
     * 判断字符串是否是json
     */
    fun String.isJson(): Boolean {
        return try {
            JsonParser.parseString(this)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * JSON字符串转实体
     */
    inline fun <reified T : Any> decodeJson(json: String): T? {
        val type = object : TypeToken<T>() {}.type
        //val type = object : TypeReference<T>() {}.type  //if (type is ParameterizedType)type.actualTypeArguments.forEach { println(it.typeName) }
        return try {
            when {
                "byte[]" == type.toString() -> json.toByteArray() as T
                String::class.java == type -> this as T
                else -> gson.fromJson(json, type) as T
            }
        } catch (e: Exception) {
            YLog.e("实体转换错误：${e.message}", e)
            null
        }
    }

    /**
     * JSON字符串转实体
     */
    inline fun <reified T : Any> String.toEntity(): T? {
        return decodeJson(this)
    }

    /**
     * json格式化
     */
    fun String.jsonFormat(): String? {
        return YUtils.jsonFormat(this)
    }
}