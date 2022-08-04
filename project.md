# Yutils #

工具类 采用java8.0，安卓12.0，API31 ，androidx，gradle7.4。

安卓各种工具详见...源码。  
主要包含：各种基类，蓝牙控制，自定义总线，加密解密，SQLite数据库，Socket保持长连接，UDP通信，图片处理，日期处理，弹出自定义对话框，GSP获取，计时器，通知栏下载，处理队列，延迟操作，防粘连操作，文件处理保存，对象保存，线程池处理，单例toast，APP版本更新，快速拍照、选择相册、截图，webView封装，相机封装，多媒体播放封装，弹窗popupWindow快速实现，各种类型转换，APP启动，重启，shell执行，等...  
不断完善中。

[![platform](https://img.shields.io/badge/platform-Android-lightgrey.svg)](https://developer.android.google.cn/studio/index.html)
![Gradle](https://img.shields.io/badge/Gradle-7.1-brightgreen.svg)
[![last commit](https://img.shields.io/github/last-commit/yutils/yutils.svg)](https://github.com/yutils/yutils/commits/master)
![repo size](https://img.shields.io/github/repo-size/yutils/yutils.svg)
![android studio](https://img.shields.io/badge/android%20studio-2020.3.1-green.svg)
[![maven](https://img.shields.io/badge/maven-address-green.svg)](https://search.maven.org/artifact/com.kotlinx/yutils)

## 已经从jitpack.io仓库移动至maven中央仓库

**[releases里面有AAR包。点击前往](https://github.com/yutils/yutils/releases)**

## Gradle 引用

[添加依赖，当前最新版：————> 2.0.4　　　　![最新版](https://img.shields.io/badge/%E6%9C%80%E6%96%B0%E7%89%88-2.0.4-green.svg)](https://search.maven.org/artifact/com.kotlinx/yutils)

```
dependencies {
     //更新地址  https://github.com/yutils/yutils 建议过几天访问看下有没有新版本
     implementation 'com.kotlinx:yutils:2.0.4'
}
```

注：如果引用失败，看下面方案
```
allprojects {
    repositories {
        //google()
        //mavenCentral()
        
        //阿里云镜像
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven { url 'https://maven.aliyun.com/repository/google' }

        //如果还是不容易拉取,可以试试直接用maven.org
        maven { url 'https://repo1.maven.org/maven2' }
    }
```

Github地址：[https://github.com/yutils/yutils](https://github.com/yutils/yutils)

我的CSDN：[https://blog.csdn.net/Yu1441](https://blog.csdn.net/Yu1441)

感谢关注微博：[细雨若静](https://weibo.com/32005200)

## 部分功能需要传入application

```
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        YUtils.init(this)
    }
}
```

# 下面列举一些主要功能用法

### 具体每个类方法和和使用，请看文档或源码，每个类上面注释都有用法，欢迎给我提出修改意见。

----

### 显示一个toast

不用考虑是否在线程中调用toast，YToast会自动回到主线程，连续调用不阻塞

```kotlin
    YToast.show("你好")
```

----

### 显示一个条Log

YLog打印日志，如果数据太长会在logcat分段打印，并且会输出调用日志的类和对应行数

```kotlin
YLog.d("你好")
YLog.i("tag", "你好")
//向上偏移一级，输出调用类和行数时，显示上级调用改函数的类和对应行数
YLog.i("tag", "你好", 1)
//日志监听
YLog.setLogListener { type, tag, msg -> }
//保存日志开
YLog.saveOpen(YPath.getFilePath(this, "log"))
//保存日志监听,不保存DEBUG
YLog.setLogSaveListener { type, tag, msg -> return@setLogSaveListener type != YLog.DEBUG }
//删除30天以前日志
YLog.delDaysAgo(30)
```

输出内容

```text
★main, com.xx.xx.MainActivity.init(MainActivity.kt:15) 
你好
```

### YUtils

```kotlin
//是否是Debug
YUtils.isDebug()
//获取版本名称
YUtils.getVersionName()
//获取版本号
YUtils.getVersionCode()
//获取IP
YUtils.getIPv4()
//获取
YUtils.getAndroidId()
//获取剪切板
YUtils.getClipboardAll()
//是否有网络连接
YUtils.isNetConnected()
//是否是wifi连接
YUtils.isWifiConnected()
//APP是否更新、版本号更新后第一次运行返回true
YUtils.isUpdate()
//ping
YUtils.ping("www.baidu.com") { s -> }
//执行命令
YUtils.shell("ls")
//以root权限执行权限
YUtils.shellRoot("tcpip 5566")

//... 更多请查看源码
```

----

### bus 总线通信 工具类 ，快速不阻死，异常捕获显示实际异常，异常不影响队列

```kotlin
//注册该类
YBusUtil.init(this)

//发送消息
YBusUtil.post("tag1", "123456789")

//接收消息
@YBus("tag1")
fun message(message: Any) {
    YLog.i("收到：$message")
}

//接收全部消息
@YBus
fun message(key: Any, message: Any) {
    YLog.i("收到：$key:$message")
    textView1.text = "收到：$key:$message"
}

//接收全部消息
@YBus
fun message(yMessage: YMessage<Any>) {
    YLog.i("收到：$key:$message")
    textView1.text = "收到：$key:$message"
}

//解绑该类
YBusUtil.onDestroy(this)
```

----

### 弹出半透明等待对话框

不必考虑线程问题，会自动回到主线程

```kotlin
//弹出 正在加载 转圈圈
YShow.show("正在加载")
//弹出 正在加载 转圈圈 下面显示请稍后
YShow.show("正在加载", "请稍后...")
//弹出 正在加载 转圈圈 下面显示请稍后，并且不允许关闭
YShow.show("正在加载", "请稍后...", false)
//更新文字
YShow.getDialog().message1 = "正在加载"
YShow.getDialog().message2 = "加载进度 50%"
//关闭 对话框
YShow.finish()
```

----

### 对象储存，比如保存一些参数到本地

```kotlin
//保存用户
var user: User
    get() = YSave.get("user", User::class.java)
    set(obj) = YSave.put( "user", obj)

//或
var user: User
    get() = YSave.get(YApp.get(), "user", User::class.java)
    set(obj) = YSave.put(YApp.get(), "user", obj)

//或
var bl: Boolean
    get() = YSave.getInstance().get("b", Boolean::class.java)
    set(obj) = YSave.getInstance().put("b", obj)

//或
var bl: Boolean
    get() = YSave.create(YPath.get(),".txt").get("test", Boolean::class.java)
    set(obj) = YSave.create(YPath.get(),".txt").put("test", obj)
```

java:

```java
public static String getIP(){return YSave.get(YApp.get(),"ip",String.class);}
public static void setIP(String value){YSave.put(YApp.get(),"ip",value);}
```

----

### 权限请求

```kotlin
    //请求全部Manifest中注册的权限，不判断成功
YPermissions.requestAll(this)

//实例化权限监听
val yPermissions = YPermissions(this)
yPermissions.setSuccessListener {
    YLog.i("成功$it")
}.setFailListener {
    YLog.i("失败$it")
}.setAllSuccessListener {
    YLog.i("全部成功")
}.request(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA
)
```

----

### 图片显示对话框

弹出一张图片dialog，可以设置全屏 不必考虑线程问题，会自动回到主线程

```java
YImageDialog.show(activity,bitmap,true)

//或者
YImageDialog yDialog=new YImageDialog(RunPretestActivity.this);
yDialog.setBitmap(bitmap);
yDialog.setCancelable(true);
yDialog.show();

//或者
YImageDialog yDialog=new YImageDialog(RunPretestActivity.this);
yDialog.show();

RequestOptions options=new RequestOptions();
options.placeholder(R.mipmap.add_img);
options.error(R.mipmap.add_img);
Glide.with(RunPretestActivity.this).load(getUrl()).apply(options).into(yDialog.getImageView());
```

----

### 弹窗

```kotlin

//提示,有确定按钮，有取消按钮
YAlertDialogUtils().showMessageCancel("测试", "确定删除？删除后不可撤销。") {
    //确定事件
}

//提示,有确定按钮
YAlertDialogUtils().showMessage("测试", "确定删除？删除后不可撤销。") {
    //确定事件
}

//提示,无按钮，标题为null时不显示标题
YAlertDialogUtils().showMessage(null, "确定删除？删除后不可撤销。")


//单选
YAlertDialogUtils().showSingleChoice("请选择一个", listOf("123", "456", "789", "000").toTypedArray(), 1) {
    //YLog.i("选择了：$it")
}

//多选
val listName: MutableList<String> = ArrayList()
listName.add("项目1")
listName.add("项目2")
listName.add("项目3")
val checked = BooleanArray(listName.size) { i -> false } //默认选中项，最终选中项
YAlertDialogUtils().showMultiChoice("请选择", listName.toTypedArray(), checked) {
    //筛选选中项
    val newList: MutableList<String> = ArrayList()
    for (index in checked.indices) {
        if (checked[index]) newList.add(listName[index])
    }
    //newList
}

//列表
YAlertDialogUtils().showList("请选择一个", listOf("123", "456", "789", "000").toTypedArray()) {
    //YLog.i("选择了：$it")
}

//输入框
YAlertDialogUtils().showEdit("测试", "请输入内容") {
    //YLog.i("输入了：$it")
}
```

----

### 拍照

```kotlin
//拍照
YTake.take(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

//拍照并剪切
YTake.takeAndCorp(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

//选择图片
YTake.chosePicture(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

//选择图片并剪切
YTake.chosePictureAndCorp(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}
```

----

### APK安装

APK安装，如果没有请求安装权限，会先跳转到请求安装权限，然后安装

```kotlin
YInstallApk().install(YPath.getSDCard() + "/app.apk")
```

如果是安卓8.0以上先请求打开未知来源

```xml
    权限：
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    1.首先创建res/xml/provider_paths.xml<?xml version="1.0" encoding="utf-8"?><paths xmlns:android="http://schemas.android.com/apk/res/android">
<!-- /storage/emulated/0/Download/${applicationId}/.beta/apk-->
<external-path name="beta_external_path" path="Download/" />
<!--/storage/emulated/0/Android/data/${applicationId}/files/apk/-->
<external-path name="beta_external_files_path" path="Android/data/" />
</paths>

    2.再在AndroidManifest.xml  中的application加入<!--安装app-->
<provider android:name="androidx.core.content.FileProvider" android:authorities="${applicationId}.fileProvider" android:exported="false" android:grantUriPermissions="true">
<meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/provider_paths" />
</provider>
```

----

### 获取当前activity

```kotlin
//获取当前activity
YActivityUtil.getCurrentActivity()
//获取activity堆栈
YActivityUtil.getActivityStack()
//关闭全部activity
YActivityUtil.closeAllActivity()
```

----

### YConvert 各种转换

```kotlin
//byte数组转hexString
var hexString = YConvert.bytesToHexString(byteArrayOf(0x01, 0x02))
//hexString转byte数组
var byteArray = YConvert.hexStringToByte(hexString)

//bytes数组转换成Bitmap
var bitmap = YConvert.bytes2Bitmap(byteArray)
//bitmap转换成byte数组
var byteArray = YConvert.bitmap2Bytes(bitmap)

//InputStream转bytes
YConvert.inputStream2Bytes(inputStream)
//InputStream转String
YConvert.inputStream2String(inputStream)

//bytes转文件
YConvert.bytes2Files(byteArray, File("地址").path)
//文件转bytes
YConvert.fileToByte(File("地址"))

//... 更多请查看源码
```

----

### YNumber 数学常用转换

```kotlin
//判断是否是Int
YNumber.isInt("5")
//判断是否是Double
YNumber.isDouble("5")
//double转字符串，保留2位小数
var doubleString = YNumber.D2S(0.2536)
//字符串转double，保留2位小数
var mDouble = YNumber.S2D("2.256")
//double转字符串，保留2位小数，并且不足用0填充，比如1.1，返回是"1.10"
var doubleString = YNumber.fill(1.1)
//double转字符串，保留指定位小数，并且不足用0填充，比如1.12，返回是"1.12000"
var doubleString = YNumber.fill(1.12, 5)

//... 更多请查看源码
```

----

### YDelay 延迟运行

```kotlin
    YDelay.run(2000) {
    YLog.i("延迟运行")
}
```

java

```java
YDelay.run(2000,new Runnable(){
    @Override
    public void run(){
            System.out.println("触发");
    }
});
```

----

### 循环调用abc方法，替代timer

不必考虑线程问题

```java
public void init(){
    //循环调用abc方法每1000毫秒，abc不能为private，不能有参数
    YLoop.start(this,"abc",1000);
}

//此方法会被调用
public void abc(){
    YLog.d("我被调用了");
}

@Override
protected void onDestroy(){
    super.onDestroy();
    //停止循环调用abc方法
    YLoop.stop(this,"abc");
}
```

或者

```kotlin
val yTimer = YTimer()

//每秒调用一次
yTimer.loopIO(1000) {  }

//每秒调用一次，最多调用5次，或者10秒,回调UI线程
yTimer.loopUI(1000,5,10000) {  }

//退出时关闭
override fun onDestroy() {
    super.onDestroy()
    yTimer.stop()
}
```

----

### Fragment 管理器

```kotlin
private var yFragmentManager: YFragmentManager? = null
private var fragment1: Fragment1? = null
private var fragment2: Fragment2? = null
private var fragment3: Fragment3? = null

//实例化fragment
yFragmentManager = YFragmentManager(R.id.fl_main, this)
fragment1 = Fragment1()
fragment2 = Fragment2()
fragment3 = Fragment3()


//显示/切换fragment
yFragmentManager!!.showFragment(fragment1)

//重新加载fragment，会触发新fragment的onCreateView,旧fragment的onDestroy
yFragmentManager!! replace (fragment1)
```

----

### 设置 RecyclerView 滚动方式

```kotlin
//设置recyclerView为垂直滚动布局
YSetRecyclerView.initVertical(binding.recyclerView)
//设置recyclerView为水平滚动布局
YSetRecyclerView.initHorizontal(binding.recyclerView)
//设置多行多列布局，如：垂直滚动，每行3个item
YSetRecyclerView.init(this, binding.recyclerView, RecyclerView.VERTICAL, 3)
//设置多行多列布局，如：水平滚动，每行4个item
YSetRecyclerView.init(this, binding.recyclerView, RecyclerView.HORIZONTAL, 3)
```

----

### RecyclerView 适配器

```kotlin
class MyAccountAdapter<T>(context: Context, list: List<T>) : YBaseYRecyclerViewAdapter<T>(context, list) {
    override fun setLayout(): Int {
        return R.layout.activity_my_account_item
    }
    override fun setViewHolder(itemView: View?): BaseViewHolder {
        return object : BaseViewHolder(itemView) {
            lateinit var binding: ActivityMyAccountItemBinding
            override fun findView(view: View) {
                binding = DataBindingUtil.bind(view)!!
            }

            override fun setData(position: Int, obj: Any?, adapterList: MutableList<Any?>?, adapter: YBaseYRecyclerViewAdapter<*>?) {
                //这儿赋值
                var user = obj as User
                binding.textView1.text = "名称"
                binding.textView2.text = user.phone
            }
        }
    }
}

```

----

### 弹出一个日期选择器

```kotlin
YDateDialog.setDefaultFullScreen(true);
YDateDialog yDateDialog = new YDateDialog(activity);
yDateDialog.setFormat("yyyy年MM月dd日");// 设置日期格式（如："yyyy年MM月dd日HH:mm"）
yDateDialog.initTime("2022年2月22日");//设置初始化日期，必须和设置格式相同（如："2016年07月01日15:19"）
yDateDialog.setShowDay(true);// 设置是否显示日滚轮,默认显示
yDateDialog.setShowTime(false);// 设置是否显示时间滚轮,默认显示
yDateDialog.setShowMonth(true);// 设置是否显示时间滚轮,默认显示
yDateDialog.setWindowListener(window -> );
yDateDialog.show((format, calendar, date, yyyy, MM, dd, HH, mm) -> {

});
```

----

### 各种检查验证数据是否合法

```kotlin
//判断是否是年龄0-12
YCheck.isAge("50")
//校验中文
YCheck.isChinese("你好")
//效验英文
YCheck.isEnglish("ABCDEF")
//判断字段是否为数字 正负整数 正负浮点数 符合返回ture
YCheck.isNumber("5.25")
//判断是否是整数
YCheck.isInteger("55")
//判断是否是double
YCheck.isDouble("123.564")
//判断是否是email
YCheck.isEmail("3373217@qq.com")
//判断是否是银行卡号
YCheck.isBankCard("6222848136846824")
//判断是否是日期，支持 YYYY-MM-DD ， YYYY/MM/DD ， YYYY_MM_DD ， YYYYMMDD ，  YYYY.MM.DD
YCheck.isDate("2022-02-22")
//判断是否是http
YCheck.isHttp("http://www.baidu.com")
//判断是否是IPv4
YCheck.isIPv4("192.168.1.5")
//判断是否是IPv6
YCheck.isIPv6("0000:0000:0000:0000:0000:0000:0000:0000")
//判断是否是手机号
YCheck.isMobile("13888888888")

//... 更多请查看源码
```

----

### YPath 获取各种目录

```kotlin
//常用目录
val path = YPath.getFilePath(App.get(), "配置") + "/" + "name.txt"
val file = File(YPath.getFilePath(App.get()) + "/" + "name.txt")
YPath.getDCIM()
YPath.getDOWNLOADS()
YPath.getALARMS()
YPath.getCacheSdcard()
YPath.getMOVIES()
YPath.getMUSIC()
YPath.getDOCUMENTS()
YPath.getRoot()

//... 更多请查看源码
```

----

### 规定时间内只能运行一次

```kotlin
//这句语音每20秒只能说一次
YRunOnceOfTime.run(1000 * 20, str) {
    speak("语音播报：" + str)
}

//1秒内只能运行一次，防抖
if (YRunOnceOfTime.check(1000, "tag1")) {
    YLog.i("运行内容")
}
```

----

### 屏幕常用操作

```kotlin
YScreenUtil.dp2px(15.0F)
YScreenUtil.px2dp(15)
YScreenUtil.sp2px(14.0F)
YScreenUtil.px2sp(14)
//获取系统dp尺寸密度值
YScreenUtil.getDensity()
//获取DPI
YScreenUtil.getDensityDpi()
//获取屏幕宽度（物理），单位为px
YScreenUtil.getScreenWidth()
//获取屏幕高度（物理），单位为px
YScreenUtil.getScreenHeight()
//获得状态栏（顶部）的高度
YScreenUtil.getStatusHeight()
//获得导航栏（底部）的高度
YScreenUtil.getNavigationHeight()
//设置全屏
YScreenUtil.setFullScreen(this, true)
//设置开启沉浸式
YScreenUtil.setImmersive(this, true)
//设置屏幕变暗 0.0-1.0 ，0.0为黑 1.0为亮
YScreenUtil.setAlpha(this, 0.5F)
//获取当前屏幕截图，不包含状态栏
YScreenUtil.snapShotWithoutStatusBar(this)
```

----

### SoundPool 快捷使用

```kotlin
//添加资源
YSound.getInstance().put(0, R.raw.di)
//播放资源
YSound.getInstance().play(0)
//释放资源
YSound.getInstance().onDestroy()
```

----

### 线程常用

```kotlin
//任意位置回到UI线程
YThread.ui{ }
//统计当前有多少线程
YThread.countThread()
//获取全部线程
YThread.getAllThread()
//判断是否是在主线程（UI线程）
YThread.isMainThread()
//在主线程中运行
YThread.runOnUiThread { YLog.i("主线程") }
//在主线程中运行,延迟2秒后
YThread.runOnUiThreadDelayed({ YLog.i("主线程") }, 2000) 
```

----

### TTS语音使用

```kotlin
//播放语音
TTS.speak("你是张三吗？")
//语音队列
TTS.speakQueue("是的，你是谁？")


//速度
TTS.speechRate=1.1F
//音调
TTS.pitch=1.1F
//任意位置可以设置过滤器
TTS.filter={ it.replace("张三", "李四") }

//退出时关闭，释放资源
TTS.destroy()
```

----

### 弹窗提示版本更新

```kotlin
    /*使用说明,举例
    val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"
    //实例化
    var yVersionUpdate = YVersionUpdate()
    //服务器版本号, 是否强制更新, apk下载地址
    yVersionUpdate?.update(999, true, url)
    
    //注意：需要有安装权限，文件写入权限，启用通知栏下载需要调用onDestroy()
```

----

### 基础activity，简化Activity，activity当参数传递可以获取生命周期

```kotlin
    //kotlin
class AboutActivity : YBaseActivity<ActivityAboutBinding>(R.layout.activity_about) {
    override fun init() {
        binding.include.ivBack.setOnClickListener { finish() }
        binding.include.tvTitle.text = "关于我们"
    }
}
```

java

```java
    public class OldActivity extends YBaseActivity<Activity1101Binding> {
    public OldActivity() {
        super(R.layout.activity_1101);
    }

    @Override
    protected void init() {
    }
}
```

----

### 基础Fragment，简化Fragment，Fragment当参数传递可以获取生命周期

```kotlin
//kotlin
class AboutActivity : YBaseFragment<ActivityAboutBinding>(R.layout.activity_about) {
    override fun init() {}
}
```

java

```java
    public class OldFragment extends YBaseFragment<Activity1101Binding> {
    public OldFragment() {
        super(R.layout.activity_1101);
    }

    @Override
    protected void init() {

    }
}
```

----

### 其他

- YUsb类：USB使用通用方法，包含连接，打开，发送数据，读取数据
- YWeb：对webView二次封装，实现播放视频等操作
- YPlayer：多功能播放器
- YPopupWindow：自定义弹窗控制
- YRecycleViewKeyControl：物理按键映射到RecycleView实现滚动，确定等。如：按W就上滚动，按S就下滚动，按就选中
- YSocket：套接字连接，套接字断开重新连接，读数据可以通过接口设置自己协议解析
- YUdp：实现UDP收发数据
- YDB：SQLite简化使用
- YBase64、YAes、YDes、YGzip、YMd5、YRsa、YSha1 加密或者哈希算法 标准模式
- 其他就不一一罗列了

Github地址：[https://github.com/yutils/yutils](https://github.com/yutils/yutils)  
我的CSDN：[https://blog.csdn.net/Yu1441](https://blog.csdn.net/Yu1441)  
感谢关注微博：[细雨若静](https://weibo.com/32005200)  
我的QQ：[3373217](http://wpa.qq.com/msgrd?v=3&uin=3373217&site=qq&menu=yes) (可技术交流)  

