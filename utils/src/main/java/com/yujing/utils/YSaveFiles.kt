@file:Suppress("NonAsciiCharacters", "ObjectPropertyName", "HasPlatformType", "MayBeConstant")

package com.yujing.utils

import java.io.File

/**
 * 保存文件到:Android/data/包名/files/文件名.txt  中
 */
/*用法举例
//初始化
YUtils.init(this)

//ip
var IP: String?
    get() =  YSaveFiles.get("服务器IP", "192.168.1.170")
    set(value) =  YSaveFiles.set("服务器IP", value)

//端口
var PORT: String?
    get() =  YSaveFiles.get("服务器端口", "10136")
    set(value) =  YSaveFiles.set("服务器端口", value)

//删除
YSaveFiles.remove("服务器IP")

 */
object YSaveFiles {
    /**
     * 读取文件
     * @param fileName  文件名
     * @param default 默认值
     */
    @JvmStatic
    fun get(fileName: String, default: String? = null): String? {
        val file = File(YPath.getFilePath(YApp.get()) + "/" + fileName + ".txt")
        if (!file.exists()) {
            if (default != null) YFileUtil.stringToFile(file, default)
            return default
        }
        return YFileUtil.fileToString(file)
    }

    /**
     * 写入文件
     * @param fileName  文件名
     * @param value 值
     */
    @JvmStatic
    fun set(fileName: String, value: String?) {
        if (value == null) {
            remove(fileName)
            return
        }
        val file = File(YPath.getFilePath(YApp.get()) + "/" + fileName + ".txt")
        YFileUtil.stringToFile(file, value)
    }

    /**
     * 删除文件
     * @param fileName  文件名
     */
    @JvmStatic
    fun remove(fileName: String): Boolean {
        val file = File(YPath.getFilePath(YApp.get()) + "/" + fileName + ".txt")
        return if (file.exists())
            file.delete()
        else false
    }

    /**
     * 读取文件
     * @param fileName  文件名
     * @param default 默认值
     */
    @JvmStatic
    fun getBytes(fileName: String, default: ByteArray? = null): ByteArray? {
        val file = File(YPath.getFilePath(YApp.get()) + "/" + fileName + ".bytes")
        if (!file.exists()) {
            if (default != null) YFileUtil.byteToFile(file, default)
            return default
        }
        return YFileUtil.fileToByte(file)
    }

    /**
     * 写入文件
     * @param fileName  文件名
     * @param value 值
     */
    @JvmStatic
    fun setBytes(fileName: String, value: ByteArray?) {
        if (value == null) {
            remove(fileName)
            return
        }
        val file = File(YPath.getFilePath(YApp.get()) + "/" + fileName + ".bytes")
        YFileUtil.byteToFile(file, value)
    }

    /**
     * 删除文件
     * @param fileName  文件名
     */
    @JvmStatic
    fun removeBytes(fileName: String): Boolean {
        val file = File(YPath.getFilePath(YApp.get()) + "/" + fileName + ".bytes")
        return if (file.exists())
            file.delete()
        else false
    }
}
