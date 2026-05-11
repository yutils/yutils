package com.kotlinx.apkUpdate

import java.io.Serializable

data class ApkInfo(
    var appName: String = "", // app名称
    var changelog: String = "", // 说明
    var downloadCount: Int = 0, // 0
    var downloadUrl: String = "", // http://apk.kotlinx.com:9999/appserver/apk/download?packageName=包名&versionCode=版本号
    var fileSize: Int = 0, // 17897779
    var forceUpdate: Boolean = false, // false
    var iconUrl: String = "", // http://apk.kotlinx.com:9999/appserver/files/包名-v版本号.png
    var id: Int = 0, // 3
    var md5: String = "", // 66f4967f86f8ee230dbc51f50103f418
    var packageName: String = "", // com.hn.test
    var sha256: String = "", // 5da17174e984425d9ceb8039185b5a2067ff60c232d91dac58722c518436519d
    var updateTime: String = "", // 2026-03-18T09:42:50.875
    var uploader: String = "", // 管理员
    var versionCode: Int = 0, // 88
    var versionName: String = "" // 1.8.8
) : Serializable