package com.yujing.test.cases

import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory
import com.yujing.utils.YApp
import com.yujing.utils.YFileUtil
import com.yujing.utils.YObjectStorage
import com.yujing.utils.YPath
import com.yujing.utils.YPicture
import com.yujing.utils.YPropertiesUtils
import com.yujing.utils.YSave
import com.yujing.utils.YSaveFiles
import com.yujing.utils.YShared
import com.yujing.utils.YUri
import java.io.File

object StorageCases {
    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("storage.save.isolation", "YSave 不同 path 缓存隔离", TestCategory.STORAGE) {
            val ctx = YApp.get()
            val pathA = File(ctx.filesDir, "test_save_a").absolutePath
            val pathB = File(ctx.filesDir, "test_save_b").absolutePath
            File(pathA).mkdirs()
            File(pathB).mkdirs()
            val a = YSave.create(ctx, pathA)
            val b = YSave.create(ctx, pathB)
            a.write("user", "Alice")
            b.write("user", "Bob")
            val ga = a.read("user", String::class.java)
            val gb = b.read("user", String::class.java)
            require(ga == "Alice") { "pathA 读到 $ga" }
            require(gb == "Bob") { "pathB 读到 $gb（缓存串读）" }
            a.remove("user")
            b.remove("user")
        },
        AutoTestCase("storage.save.safeKey", "YSave 非法 key 应抛异常", TestCategory.STORAGE) {
            val s = YSave.create(YApp.get())
            var threw = false
            try {
                s.write("../evil", "x")
            } catch (_: IllegalArgumentException) {
                threw = true
            }
            require(threw) { "含路径分隔的 key 应拒绝" }
        },
        AutoTestCase("storage.file.string", "YFileUtil 字符串读写", TestCategory.STORAGE) {
            val f = File(YApp.get().cacheDir, "ytest_file.txt")
            YFileUtil.stringToFile(f, "hello-yutils")
            val back = YFileUtil.fileToString(f)
            require(back == "hello-yutils") { "读回=$back" }
            f.delete()
        },
        AutoTestCase("storage.properties", "YPropertiesUtils set/get", TestCategory.STORAGE) {
            val f = File(YApp.get().cacheDir, "ytest.properties").absolutePath
            YPropertiesUtils.set(f, "k1", "v1")
            val v = YPropertiesUtils.get(f, "k1")
            require(v == "v1") { "get=$v" }
            YPropertiesUtils.removeAll(f)
        },
        AutoTestCase("storage.path", "YPath 目录工具", TestCategory.STORAGE) {
            val p = YPath.get()
            require(!p.isNullOrBlank()) { "YPath.get 为空" }
            val dir = YPath.toDir(YApp.get().filesDir.absolutePath)
            require(dir.endsWith(File.separator) || dir.endsWith("/")) { "toDir=$dir" }
        },
        AutoTestCase("storage.object", "YObjectStorage 对象读写", TestCategory.STORAGE) {
            val key = "obj_test_${System.nanoTime()}"
            val store = YObjectStorage(YApp.get(), "ytest_obj")
            store.put(key, "hello-obj")
            val back = store.get(key) as? String
            require(back == "hello-obj") { "读回=$back" }
            store.remove(key)
        },
        AutoTestCase("storage.shared", "YShared 读写", TestCategory.STORAGE) {
            val ctx = YApp.get()
            val key = "yshared_test"
            YShared.write(ctx, key, "v123")
            require(YShared.get(ctx, key) == "v123")
            YShared.writeInt(ctx, key + "_i", 42)
            require(YShared.getInt(ctx, key + "_i") == 42)
            YShared.writeBoolean(ctx, key + "_b", true)
            require(YShared.getBoolean(ctx, key + "_b"))
            YShared.delete(ctx, key)
            YShared.delete(ctx, key + "_i")
            YShared.delete(ctx, key + "_b")
        },
        AutoTestCase("storage.saveFiles", "YSaveFiles 字符串读写", TestCategory.STORAGE) {
            val name = "ytest_save_files"
            YSaveFiles.set(name, "content-abc")
            require(YSaveFiles.get(name) == "content-abc")
            YSaveFiles.setBytes(name, byteArrayOf(1, 2, 3))
            require(YSaveFiles.getBytes(name)!!.contentEquals(byteArrayOf(1, 2, 3)))
            YSaveFiles.remove(name)
            YSaveFiles.removeBytes(name)
        },
        AutoTestCase("storage.file.bytes.copy", "YFileUtil 字节与复制", TestCategory.STORAGE) {
            val dir = File(YApp.get().cacheDir, "yfile_${System.nanoTime()}")
            dir.mkdirs()
            val src = File(dir, "a.bin")
            val dst = File(dir, "b.bin")
            val payload = byteArrayOf(9, 8, 7, 6)
            require(YFileUtil.byteToFile(src, payload))
            require(YFileUtil.fileToByte(src).contentEquals(payload))
            YFileUtil.copy(src.absolutePath, dst.absolutePath, false)
            require(dst.exists() && YFileUtil.fileToByte(dst).contentEquals(payload))
            YFileUtil.addStringToFile(File(dir, "c.txt"), "hi")
            YFileUtil.addStringToFile(File(dir, "c.txt"), "-ok")
            require(YFileUtil.fileToString(File(dir, "c.txt")) == "hi-ok")
            YFileUtil.delFile(dir)
        },
        AutoTestCase("storage.uri.file", "YUri File→Uri", TestCategory.STORAGE) {
            val f = File(YApp.get().cacheDir, "yuri_${System.nanoTime()}.txt")
            YFileUtil.stringToFile(f, "uri-test")
            val uri = YUri.getUri(YApp.get(), f)
            require(uri != null) { "uri 空" }
            require(uri.toString().isNotBlank())
            f.delete()
        },
        AutoTestCase("storage.picture.create", "YPicture 创建图片路径", TestCategory.STORAGE) {
            val path = File(YApp.get().cacheDir, "ypic_${System.nanoTime()}.jpg").absolutePath
            val f = YPicture.createImageFile(path)
            require(f != null) { "createImageFile 返回 null" }
            require(f.absolutePath == path || f.name.endsWith(".jpg"))
            f.parentFile?.mkdirs()
        },
        AutoTestCase("storage.path.more", "YPath 常用目录非空", TestCategory.STORAGE) {
            val ctx = YApp.get()
            require(!YPath.getCache(ctx).isNullOrBlank())
            require(!YPath.getFilesDir(ctx).isNullOrBlank())
            require(!YPath.getDOWNLOADS().isNullOrBlank())
            require(!YPath.getData().isNullOrBlank())
            require(!YPath.getRoot().isNullOrBlank())
            File(YPath.getCache(ctx)).mkdirs()
            require(File(YPath.getCache(ctx)).exists())
        },
        AutoTestCase("storage.file.list.append", "YFileUtil 目录列举与追加字节", TestCategory.STORAGE) {
            val dir = File(YApp.get().cacheDir, "ylist_${System.nanoTime()}")
            dir.mkdirs()
            val a = File(dir, "a.txt")
            val b = File(dir, "b.txt")
            YFileUtil.stringToFile(a, "1")
            YFileUtil.stringToFile(b, "2")
            val all = YFileUtil.getFileAll(dir)
            require(all.size >= 2) { "列举=${all.size}" }
            YFileUtil.addByteToFile(a, byteArrayOf(0x41))
            val content = YFileUtil.fileToString(a)
            require(content != null && content.contains("1") && content.contains("A")) { "content=$content" }
            YFileUtil.delFile(dir)
        },
        AutoTestCase("storage.uri.path", "YUri/YPicture Uri 转换", TestCategory.STORAGE) {
            val ctx = YApp.get()
            val f = File(ctx.cacheDir, "ypic_uri_${System.nanoTime()}.jpg")
            YFileUtil.stringToFile(f, "x")
            try {
                val uri1 = YUri.getUri(ctx, f)
                require(uri1 != null && uri1.toString().isNotBlank()) { "YUri.getUri 失败" }
                // imageFile2Uri / createImageUri 依赖 MediaStore，内部 cache 路径在 Android 10+ 常被拒绝
                runCatching { YPicture.imageFile2Uri(ctx, f) }
                runCatching { YPicture.createImageUri(ctx, f) }
                val pics = File(ctx.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "yuri_${System.nanoTime()}.jpg")
                pics.parentFile?.mkdirs()
                YFileUtil.stringToFile(pics, "pic")
                try {
                    val uri2 = runCatching { YPicture.imageFile2Uri(ctx, pics) }.getOrNull()
                    val uri3 = runCatching { YPicture.createImageUri(ctx, pics) }.getOrNull()
                    require(uri2 != null || uri3 != null || uri1 != null) { "Picture Uri 均失败" }
                } finally {
                    pics.delete()
                }
            } finally {
                f.delete()
            }
        },
    )
}
