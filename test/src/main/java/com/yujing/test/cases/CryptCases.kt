package com.yujing.test.cases

import com.yujing.crypt.Y3des
import com.yujing.crypt.YAes
import com.yujing.crypt.YDes
import com.yujing.crypt.YEncrypt
import com.yujing.crypt.YGzip
import com.yujing.crypt.YMd5
import com.yujing.crypt.YOneBitCrypt
import com.yujing.crypt.YRsa
import com.yujing.crypt.YSha1
import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory

object CryptCases {
    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("crypt.aes.ecb", "YAes ECB 加解密往返", TestCategory.CRYPT) {
            val plain = "余静AES测试".toByteArray(Charsets.UTF_8)
            val key = "123456".toByteArray()
            val en = YAes.encryptECB(plain, key)
            val de = YAes.decryptECB(en, key)
            require(plain.contentEquals(de)) { "ECB 往返失败" }
        },
        AutoTestCase("crypt.aes.cbc", "YAes CBC 加解密往返", TestCategory.CRYPT) {
            val plain = "CBC测试内容".toByteArray(Charsets.UTF_8)
            val key = "123456".toByteArray()
            val iv = "8516144119920625"
            val en = YAes.encryptCBC(plain, key, iv)
            val de = YAes.decryptCBC(en, key, iv)
            require(plain.contentEquals(de)) { "CBC 往返失败" }
        },
        AutoTestCase("crypt.aes.createKey", "YAes createKey(password) 确定性", TestCategory.CRYPT) {
            val pwd = "mypassword".toByteArray()
            val k1 = YAes.createKey(pwd)
            val k2 = YAes.createKey(pwd)
            require(k1.encoded.contentEquals(k2.encoded)) { "同密码密钥应一致" }
            val en = YAes.encrypt("hello".toByteArray(), k1)
            val de = YAes.decrypt(en, k2)
            require(String(de) == "hello")
        },
        AutoTestCase("crypt.md5", "YMd5 固定向量", TestCategory.CRYPT) {
            // MD5("") = d41d8cd98f00b204e9800998ecf8427e
            val empty = YMd5.MD5("")
            require(empty.equals("d41d8cd98f00b204e9800998ecf8427e", true)) { "MD5空串=$empty" }
            val hello = YMd5.MD5("hello")
            require(hello.equals("5d41402abc4b2a76b9719d911017c592", true)) { "MD5(hello)=$hello" }
        },
        AutoTestCase("crypt.sha1", "YSha1 固定向量", TestCategory.CRYPT) {
            // SHA1("") = da39a3ee5e6b4b0d3255bfef95601890afd80709
            val empty = YSha1.SHA1("")
            require(empty.equals("da39a3ee5e6b4b0d3255bfef95601890afd80709", true)) { "SHA1空串=$empty" }
        },
        AutoTestCase("crypt.gzip", "YGzip 压缩解压往返", TestCategory.CRYPT) {
            val raw = ("Gzip测试-" + "x".repeat(200)).toByteArray(Charsets.UTF_8)
            val compressed = YGzip.compress(raw)
            require(compressed.isNotEmpty())
            val back = YGzip.decompress(compressed)
            require(raw.contentEquals(back)) { "Gzip 往返失败" }
        },
        AutoTestCase("crypt.encrypt.roundtrip", "YEncrypt 有密码往返", TestCategory.CRYPT) {
            val ye = YEncrypt()
            val plain = "加密内容ABC".toByteArray(Charsets.UTF_8)
            val enc = ye.encode(plain, "pass123")
            val dec = ye.decode(enc, "pass123")
            require(dec != null && plain.contentEquals(dec)) { "解密失败" }
        },
        AutoTestCase("crypt.encrypt.emptyPass", "YEncrypt 空密码应抛异常", TestCategory.CRYPT) {
            val ye = YEncrypt()
            var threw = false
            try {
                ye.encode("hi".toByteArray(), "")
            } catch (_: IllegalArgumentException) {
                threw = true
            }
            require(threw) { "空密码应抛 IllegalArgumentException" }
        },
        AutoTestCase("crypt.encrypt.badPass", "YEncrypt 错误密码返回 null", TestCategory.CRYPT) {
            val ye = YEncrypt()
            val enc = ye.encode("data".toByteArray(), "right")
            val dec = ye.decode(enc, "wrong")
            require(dec == null) { "错误密码应返回 null" }
        },
        AutoTestCase("crypt.3des", "Y3des 加解密往返", TestCategory.CRYPT) {
            val key = Y3des.getKey()
            val plain = "3DES测试".toByteArray(Charsets.UTF_8)
            val enc = Y3des.encode(plain, key)
            val dec = Y3des.decode(enc, key)
            require(plain.contentEquals(dec)) { "3DES 往返失败" }
        },
        AutoTestCase("crypt.des.nopad", "YDes NOPadding 往返", TestCategory.CRYPT) {
            // DESedeKeySpec 需 24 字节 key；NOPadding 要求数据长度整除 8
            val key = "123456789012345678901234".toByteArray(Charsets.UTF_8)
            val plain = "12345678".toByteArray(Charsets.UTF_8)
            val enc = YDes.encode(plain, key)
            require(enc != null) { "YDes.encode 返回 null" }
            val dec = YDes.decode(enc, key)
            require(dec != null && plain.contentEquals(dec)) { "YDes 往返失败" }
        },
        AutoTestCase("crypt.onebit", "YOneBitCrypt 异或往返", TestCategory.CRYPT) {
            val plain = "onebit".toByteArray(Charsets.UTF_8)
            val pwd: Byte = 0x5A
            val enc = YOneBitCrypt.encrypt(plain, pwd)
            val dec = YOneBitCrypt.decrypt(enc, pwd)
            require(plain.contentEquals(dec))
        },
        AutoTestCase("crypt.rsa.roundtrip", "YRsa 公私钥往返", TestCategory.CRYPT) {
            val map = YRsa.getKey(1024)
            val pub = YRsa.getPublicKey(map)
            val priv = YRsa.getPrivateKey(map)
            val plain = "rsa-demo".toByteArray(Charsets.UTF_8)
            val enc = YRsa.encryptPublicKey(plain, pub)
            val dec = YRsa.decryptPrivateKey(enc, priv)
            require(plain.contentEquals(dec)) { "RSA 往返失败" }
            val sign = YRsa.sign(plain, priv)
            require(YRsa.verify(plain, pub, sign)) { "签名校验失败" }
        },
        AutoTestCase("crypt.aes.base64.hex", "YAes Base64/Hex 字符串加解密", TestCategory.CRYPT) {
            val plain = "hello-aes"
            val pwd = "123456"
            val b64 = YAes.encryptToBase64(plain, pwd)
            require(b64.isNotBlank())
            require(YAes.decryptFromBase64(b64, pwd) == plain) { "Base64 解密失败" }
            val hex = YAes.encryptToHex(plain, pwd)
            require(hex.isNotBlank() && hex.matches(Regex("^[0-9a-fA-F]+$"))) { "hex=$hex" }
            require(YAes.decryptFromHex(hex, pwd) == plain) { "Hex 解密失败" }
        },
        AutoTestCase("crypt.sha1.file", "YSha1 文件摘要", TestCategory.CRYPT) {
            val f = java.io.File(com.yujing.utils.YApp.get().cacheDir, "sha1_${System.nanoTime()}.txt")
            f.writeText("hello")
            try {
                val sha = YSha1.getFileSha1(f)
                require(sha == "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d") { "sha1=$sha" }
            } finally {
                f.delete()
            }
        },
    )
}
