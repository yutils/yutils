# utils #

工具类
采用java8.0，安卓10.0，API29，androidx。

安卓各种工具详见doc文档。

##当前最新版：————>[![](https://jitpack.io/v/yutils/yutils.svg)](https://jitpack.io/#yutils/yutils)

**[releases里面有JAR包。点击前往](https://github.com/yutils/yutils/releases)**

## Gradle 引用

1. 在根build.gradle中添加
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. 子module添加依赖，当前最新版：————> [![](https://jitpack.io/v/yutils/yutils.svg)](https://jitpack.io/#yutils/yutils)

```
dependencies {
     implementation 'com.github.yutils:yutils:1.2.7'
}
```


Github地址：[https://github.com/yutils/yutils](https://github.com/yutils/yutils)

我的CSDN：[https://blog.csdn.net/Yu1441](https://blog.csdn.net/Yu1441)

感谢关注微博：[细雨若静](https://weibo.com/32005200)

# 下面列举一些主要类的方法，具体每个类方法和和使用，请看文档或源码，欢迎给我提出修改意见。

## 类 YUtils
```
static void	closeSoftKeyboard(android.app.Activity activity)
关闭软键盘
static <T> T	copyObject(T date)
对象复制,深度复制,被复制的对象必须序列化或是基本类型
static void	copyToClipboard(android.content.Context context, java.lang.String text)
复制文本到粘贴板
static android.view.View	foreground(android.view.View view, int color, int start, int end)
字体高亮
static java.lang.String	getAndroidId(android.content.Context context)
获取设备的唯一驱动id
static java.util.List<java.lang.String>	getClipboardAll(android.content.Context context)
获取粘贴板全部数据
static java.lang.String	getClipboardLast(android.content.Context context)
获取粘贴板最后一条数据
static int	getConnectedType(android.content.Context context)
获取连接类型
static java.lang.String	getDataCachePath(android.content.Context context)
/data/data/PackageName/cache的路径
static java.lang.String	getDataFilePath(android.content.Context context)
获取应用程序的/data/data目录
static java.lang.String	getImei(android.content.Context context)
获取设备的imei
static java.lang.String	getImei(android.content.Context context, int index)
获取设备的imei
static java.util.List<java.lang.String>	getIpv4()
获取ipv4
static java.util.List<java.lang.String>	getIpv6()
获取ipv6
static long	getRomAvailableSize()
获得手机可用内存
static long	getRomTotalSize()
获得手机内存总大小
static long	getSDCardAvailableSize()
获取SD卡可用大小,SD卡存在返回大小；SD卡不存在返回-1
static java.io.File	getSDCardFile()
获取SD卡路径文件
static java.lang.String	getSDCardPath()
获取SD卡路径
static java.io.File	getSDCardRootFile()
获取系统存储路径文件
static java.lang.String	getSDCardRootPath()
获取系统存储路径
static long	getSDCardSize()
获取SD卡大小
static int	getVersionCode(android.content.Context context)
获取当前版本code
static int	getVersionCode(android.content.Context context, java.lang.String packageName)
获取当前版本code
static java.lang.String	getVersionName(android.content.Context context)
获取当前版本名
static java.lang.String	getVersionName(android.content.Context context, java.lang.String packageName)
获取当前版本名
static void	installApk(android.content.Context context, java.io.File file)
安装app
static void	installApk(android.content.Context context, java.lang.String apkPath)
安装app
static void	installApk(android.content.Context context, android.net.Uri apkUri)
安装app
static boolean	isDebug(android.content.Context context)
当前是否是debug模式
boolean	isMobileConnected(android.content.Context context)
判断MOBILE网络是否可用
static boolean	isNetConnected(android.content.Context context)
判断当前是否有网络连接,但是如果该连接的网络无法上网，也会返回true 需要权限android.permission.ACCESS_NETWORK_STATE
static boolean	isSDCardEnable()
判断SD卡是否可用
static boolean	isUpdate(android.content.Context context)
判断APP版本是否是更新后第一次启动
static boolean	isWifiConnected(android.content.Context context)
判断WIFI网络是否可用
static void	makeCall(android.app.Activity activity, java.lang.String phone)
打电话
static boolean	ping(java.lang.String ip)
ping 一个ip地址
static boolean	pingBaidu()
ping 百度
static void	resetListViewHeight(android.widget.ListView listView)
重新计算listView高度
static void	resetListViewHeight(android.widget.ListView listView, java.lang.Integer maxHeight, java.lang.Integer itemHeight)
重新计算listView高度
static void	sendMessage(android.content.Context context, java.lang.String phoneNumber, java.lang.String text)
后台实现发送短信
static void	sendMessage(android.content.Context context, java.lang.String phoneNumber, java.lang.String text, android.content.BroadcastReceiver sendMessage, android.content.BroadcastReceiver receiver)
后台实现发送短信
static void	sendSMS(android.app.Activity activity, java.lang.String tel, java.lang.String content)
发短信
static void	setFullScreen(android.app.Activity activity, boolean isFullScreen)
设置全屏
static void	setImmersive(android.app.Activity activity, boolean isFullScreen)
设置开启沉浸式
```

## 类 YActivityUtil
```
static void	closeActivityByName(java.lang.String name)
通过名称关闭Activity
static void	closeAllActivity()
关闭所有Activity
static void	finishActivity(android.app.Activity activity)
关闭当前Activity
static YActivityUtil.YActivityLifecycleCallbacks	getActivityLifecycleCallbacks() 
static java.util.Stack<android.app.Activity>	getActivityStack()
获取Activity栈
static android.app.Activity	getCurrentActivity()
获得当前栈顶Activity
static java.lang.String	getCurrentActivityName()
获得当前Activity名字
```

## 类 YBitmapUtil
```
static byte[]	compressToBytes(android.graphics.Bitmap image, int Kb)
图片压缩返回byte[]
static android.graphics.Bitmap	getReflection(android.graphics.Bitmap bitmap)
获得带倒影的图片方法
static android.graphics.Bitmap	getRounded(android.graphics.Bitmap bitmap, float roundPx)
获得圆角图片的方法
static android.graphics.Bitmap	replaceColor(android.graphics.Bitmap oldBitmap, int oldColor, int newColor)
替换eBitmap中某颜色值
static android.graphics.Bitmap	zoom(android.graphics.Bitmap bitmap, int w, int h)
放大缩小图片
```

## 类 YBytes
```
YBytes	addByte(byte b)
在byte数组末尾添加一个byte
YBytes	addByte(byte[] bs)
在byte数组末尾添加一个byte[]
YBytes	addByte(byte[] bs, int length)
在byte数组末尾添加一个byte[],给定添加的长度
YBytes	addByte(byte[] bs, int start, int length)
在byte数组末尾添加一个byte[],给定添加的长度
YBytes	addByte(java.util.List<java.lang.Byte> bs)
在byte数组末尾添加一组Byte
YBytes	addByte(java.util.List<java.lang.Byte> bs, int length)
在byte数组末尾添加一个List
YBytes	addByte(java.util.List<java.lang.Byte> bs, int start, int length)
在byte数组末尾添加一个List
YBytes	changeByte(byte[] b, int index)
修改byte数组中第index位起值为b
YBytes	changeByte(byte[] b, int start, int length)
修改byte数组中第start位起值为b，连续修改length位
YBytes	changeByte(byte b, int index)
修改byte数组中一位的值为byte
YBytes	changeByte(java.util.List<java.lang.Byte> b, int index)
修改byte数组中第index位起值为b
YBytes	changeByte(java.util.List<java.lang.Byte> b, int start, int length)
修改byte数组中第start位起值为b，连续修改length位
byte[]	getBytes()
获取bytes数组
void	setBytes(byte[] bytes)
替换bytes数组
```

## 类 YCheck
```
static boolean	isAge(java.lang.String str)
判断字段是否为年龄 符合返回ture
static boolean	isBankCard(java.lang.String str) 
static boolean	isChinese(java.lang.String str)
校验中文
static boolean	isDate(java.lang.String str)
判断字段是否为日期 符合返回ture
static boolean	isDouble_NEGATIVE(java.lang.String str)
判断字段是否为正浮点数正则表达式 大于=0 符合返回ture
static boolean	isDouble_POSITIVE(java.lang.String str)
判断字段是否为负浮点数正则表达式 小于=0 符合返回ture
static boolean	isDouble(java.lang.String str)
判断字段是否为DOUBLE 符合返回ture
static boolean	isEmail(java.lang.String str)
判断字段是否为Email 符合返回ture
static boolean	isENG_NUM_(java.lang.String str)
判断字符串是不是全部是英文字母+数字+下划线
static boolean	isENG_NUM(java.lang.String str)
判断字符串是不是全部是英文字母+数字
static boolean	isEnglish(java.lang.String str)
判断字符串是不是全部是英文字母
static boolean	isHttp(java.lang.String str)
校验http
static boolean	isInteger_NEGATIVE(java.lang.String str)
判断字段是否为正整数正则表达式 大于=0 符合返回ture
static boolean	isInteger_POSITIVE(java.lang.String str)
判断字段是否为负整数正则表达式 小于=0 符合返回ture
static boolean	isInteger(java.lang.String str)
判断字段是否为INTEGER 符合返回ture
static boolean	isIPv4(java.lang.String str)
校验ipv4
static boolean	isIPv6(java.lang.String str)
校验ipv6
static boolean	isMobile(java.lang.String str)
判断是否为手机号码 符合返回ture
static boolean	isNumber(java.lang.String str)
判断字段是否为数字 正负整数 正负浮点数 符合返回ture
static boolean	isPort(java.lang.String str)
校验端口
static boolean	isSTR_NUM(java.lang.String str)
判断字符串是不是数字组成
static boolean	isUrl(java.lang.String str)
判断是否为Url 符合返回ture
static boolean	isZipCode(java.lang.String str)
判断字段是否为邮编 符合返回ture
```

## 类 YConvert
```
static java.lang.String	asciiToString(java.lang.String value)
asciiToString
static java.lang.Object	base642Object(java.lang.String Base64String)
把Base64的字符串转换成Object
static java.lang.String	bcd2String(byte[] bytes)
BCD转String 大于9的bcd用*表示
static java.lang.String	bcd2String(byte[] bytes, java.lang.String error)
BCD转String 大于9的bcd用error表示
static byte[]	bitmap2Bytes(android.graphics.Bitmap bm)
bitmap转换成byte数组
static java.lang.String	bitmap2String(android.graphics.Bitmap bitmap)
将Bitmap转换成Base64字符串
static android.graphics.Bitmap	bytes2Bitmap(byte[] b)
bytes数组转换成Bitmap
static java.io.File	bytes2Files(byte[] b, java.lang.String filePath)
bytes转换成文件
static java.io.InputStream	bytes2InputStream(byte[] bytes)
bytes转InputStream
static java.lang.Object	bytes2Object(byte[] bytes)
把bytes数组换成Object
static java.lang.String	bytesToHexString(byte[] bArray)
bytesToHexString
static android.graphics.Bitmap	drawable2Bitmap(android.graphics.drawable.Drawable drawable)
将Drawable转化为Bitmap
static android.net.Uri	file2Uri(android.content.Context context, java.io.File file)
file转Uri
static byte[]	fileToByte(java.io.File f)
文件转换成byte数组
static byte[]	hexStringToByte(java.lang.String hex)
hexStringToByte
static byte[]	inputStream2Bytes(java.io.InputStream inputStream)
InputStream转bytes
static java.lang.String	inputStream2String(java.io.InputStream inputStream)
inputStream转String
static <T> T[]	List2Array(java.util.List<T> list)
将list转化为数组
static <T extends java.io.Serializable>
java.lang.String	object2Base64(T object)
把Object转换成Base64的字符串
static <T extends java.io.Serializable>
byte[]	object2Bytes(T object)
把Object转换成bytes数组
static android.graphics.Bitmap	path2Bitmap(java.lang.String path)
path2Bitmap
static android.graphics.Bitmap	resources2Bitmap(android.content.Context context, int Resources)
资源文件转换成Bitmap
static android.net.Uri	saveBitmap2uri(java.lang.String path, android.graphics.Bitmap bitmap)
保存文件返回uri
static byte[]	string2Bcd(java.lang.String asc)
string 转换BCD嘛
static android.graphics.Bitmap	string2Bitmap(java.lang.String string)
将Base64字符串转换成Bitmap类型
static java.lang.String	stringToAscii(java.lang.String value)
stringToAscii
static java.lang.String	ToDBC(java.lang.String input)
全角转半角
static java.lang.String	ToSBC(java.lang.String input)
半角转全角
static android.graphics.Bitmap	uri2Bitmap(android.content.Context context, android.net.Uri uri)
uri转换成Bitmap
static java.lang.String	uri2FilePath(android.content.Context context, android.net.Uri uri)
uri2FilePath
static java.lang.String	uri2FilePathForN(android.content.Context context, android.net.Uri uri)
uri2FilePath
static android.graphics.Bitmap	view2Bitmap(android.view.View v)
View转bitmap
static android.graphics.Bitmap	yuv420spToBitmap(byte[] data, int width, int height)
yuv420格式图片数据转成bitmap
```

## 类 YConvertBytes
```
static int	bytes2ToInt(byte[] b)
int 和 网络字节序的 byte[] 数组之间的转换 2位byte
static int	bytes2ToInt(byte[] b, int offset)
int 和 网络字节序的 byte[] 数组之间的转换 2位byte
static double	bytesToDouble(byte[] bytes)
bytes转Double
static double	bytesToDouble(byte[] bytes, int index)
bytes转Double
static float	bytesToFloat(byte[] bytes)
字节转换为浮点
static float	bytesToFloat(byte[] bytes, int index)
字节转换为浮点
static int	bytesToInt(byte[] b)
int 和 网络字节序的 byte[] 数组之间的转换
static int	bytesToInt(byte[] b, int offset)
int 和 网络字节序的 byte[] 数组之间的转换
static long	bytesToLong(byte[] array)
long 和 网络字节序的 byte[] 数组之间的转换
static long	bytesToLong(byte[] array, int offset)
long 和 网络字节序的 byte[] 数组之间的转换
static short	bytesToShort(byte[] b)
short 和 网络字节序的 byte[] 数组之间的转换
static short	bytesToShort(byte[] b, int offset)
short 和 网络字节序的 byte[] 数组之间的转换
static byte[]	doubleToBytes(double d)
double2Bytes
static void	doubleToBytes(double d, byte[] array, int offset)
double 和 网络字节序的 byte[] 数组之间的转换
static byte[]	floatToBytes(float f)
浮点转换为字节
static void	floatToBytes(float f, byte[] array, int offset)
float 和 网络字节序的 byte[] 数组之间的转换
static byte[]	intTo2Bytes(int n)
int 和 网络字节序的 byte[] 数组之间的转换 2位byte
static void	intTo2Bytes(int n, byte[] array, int offset)
int 和 网络字节序的 byte[] 数组之间的转换 2位byte
static byte[]	intToBytes(int n)
int 和 网络字节序的 byte[] 数组之间的转换
static void	intToBytes(int n, byte[] array, int offset)
int 和 网络字节序的 byte[] 数组之间的转换
static byte[]	longToBytes(long n)
long 和 网络字节序的 byte[] 数组之间的转换
static void	longToBytes(long n, byte[] array, int offset)
long 和 网络字节序的 byte[] 数组之间的转换
static byte[]	shortToBytes(short n)
short 和 网络字节序的 byte[] 数组之间的转换
static void	shortToBytes(short n, byte[] array, int offset)
short 和 网络字节序的 byte[] 数组之间的转换
```

## 类 YDate
```
static java.util.Date	calendarToDate(java.util.Calendar calendar)
Calendar转化为Date
static java.lang.String	date2String(java.util.Date date, java.lang.String formatType)
把date转换成指定格式字符串
static java.lang.String	dateConvert(java.lang.String oldDateString, java.lang.String oldDateFormat, java.lang.String newDateFormat)
时间格式转换
static java.util.Calendar	dateToCalendar(java.util.Date date)
Date转化为Calendar
static java.lang.String	dateToWeek(java.util.Calendar calendar)
根据日期获取星期 （2019-05-06 ——> 星期一）
static java.lang.String	dateToWeek(java.util.Date date)
根据日期获取星期 （2019-05-06 ——> 星期一）
static java.util.Calendar	getFirstDayOfMonth()
获取当月第一天
static java.util.Calendar	getFirstDayOfMonth(java.util.Calendar c)
获取一个月第一天
static java.util.Date	getFirstDayOfMonth(java.util.Date date)
获取一个月第一天
static java.util.Calendar	getFirstDayOfWeek()
获取当周第一天
static java.util.Calendar	getFirstDayOfWeek(java.util.Calendar c)
获取一周第一天
static java.util.Date	getFirstDayOfWeek(java.util.Date date)
获取一周第一天
static java.util.Calendar	getFirstDayOfYear()
获取当年第一天
static java.util.Calendar	getFirstDayOfYear(java.util.Calendar c)
获取一年第一天
static java.util.Date	getFirstDayOfYear(java.util.Date date)
获取一年第一天
static java.util.Calendar	getLastDayOfMonth()
获取当月最后一天
static java.util.Calendar	getLastDayOfMonth(java.util.Calendar c)
获取一个月最后一天
static java.util.Date	getLastDayOfMonth(java.util.Date date)
获取一个月最后一天
static java.util.Calendar	getLastDayOfWeek()
获取当周最后一天
static java.util.Calendar	getLastDayOfWeek(java.util.Calendar c)
获取一周最后一天
static java.util.Date	getLastDayOfWeek(java.util.Date date)
获取一周最后一天
static java.util.Calendar	getLastDayOfYear()
获取当年最后一天
static java.util.Calendar	getLastDayOfYear(java.util.Calendar c)
获取一年最后一天
static java.util.Date	getLastDayOfYear(java.util.Date date)
获取一年最后一天
static java.lang.String	getStringDate()
获取详细时间
static java.lang.String	getStringDate(java.util.Date date)
获取详细时间
static java.lang.String	getStringDateChinese()
获取详细时间
static java.lang.String	getStringDateChinese(java.util.Date date)
获取详细时间
static java.lang.String	getStringDateShort()
获取年月日
static java.lang.String	getStringDateShort(java.util.Date date)
获取年月日
static java.lang.String	getStringDateShortChinese()
获取年月日
static java.lang.String	getStringDateShortChinese(java.util.Date date)
获取年月日
static java.lang.String	getTimeShort()
获取时分秒
static java.lang.String	getTimeShort(java.util.Date date)
获取时分秒
static java.util.Date	string2Date(java.lang.String strTime, java.lang.String formatType)
strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 * HH时mm分ss秒， * strTime的时间格式必须要与formatType的时间格式相同
```
## 类 YDateDialog
弹出日期选择对话框

## 类 YDelay
延时运行

## 类 YEventCount
统计单位时间内时间触发次数

## 类 YFileUtil
```
static boolean	byteToFile(byte[] bytes, java.io.File file)
bytes转file
static void	copy(java.lang.String source, java.lang.String target, boolean isFolder)
复制文件/文件夹 若要进行文件夹复制，请勿将目标文件夹置于源文件夹中
static boolean	delFile(java.io.File file)
删除文件或文件夹
static boolean	delFile(java.lang.String filePath)
删除文件或文件夹
static byte[]	fileToByte(java.io.File file)
file转bytes
static java.lang.String	fileToString(java.io.File file)
file转string
static java.util.List<java.io.File>	getFileAll(java.io.File dir)
获取文件夹下全部文件
static void	getFileAll(java.io.File dir, java.io.FileFilter fileFilter)
递归获取dir文件夹下全部文件
static java.io.InputStream	readAssets(android.content.Context context, java.lang.String fileName)
读取Assets下面的文件
static java.io.InputStream	readRaw(android.content.Context context, int resource)
读取RAW文件夹下文件
static void	stringToFile(java.io.File file, java.lang.String str)
string转file
static void	stringToFile(java.io.File file, java.lang.String str, java.nio.charset.Charset charset)
string转file
```

## 类 YGps
```
static double	getLatitude() 
android.location.Location	getLocation()
获取一次定位信息，如果有GPS就获取GPS信息，若没有就获取网络定位
void	getLocationGPS(YGps.GpsLocation gpsLocation)
获取GPS信息
void	getLocationNET(YGps.GpsLocation gpsLocation)
获取网络定位信息
void	getLocationWIFI(YGps.GpsLocation gpsLocation)
获取网络定位信息
static double	getLongitude() 
void	openGPSSettings(android.app.Activity activity) 
void	StopGPS()
停止GPS定位信息
void	StopNET()
停止网络定位信息
```

## 类 YLoop
```
循环调用某一个类中的某一个方法 
static void	start(java.lang.Object obj, java.lang.String methodName, int interval) 
static void	start(java.lang.Object obj, java.lang.String methodName, int interval, int cycleNum) 
static void	stop(java.lang.Object obj, java.lang.String methodName) 
```

## 类 YNoticeDownload
下载类，进度显示通知栏

## 类 YNumber
对int，long，flat，double各种转换，保留小数等

## 类 YPath
```
static java.lang.String	getALARMS()
获取警告文件夹 需要申请权限
static java.lang.String	getCache(android.content.Context context)
获得缓存目录，不需要申请权限 优先 用于获取APP的在SD卡中的cache目录 /mnt/sdcard/Android/data/com.xx.xx/cache /storage/emulated/0/Android/data/com.xx.xx/cache 用于获取APP的cache目录 /data/data/com.xx.xx/cache /data/user/0/com.xx.xx/cache
static java.lang.String	getCacheSdcard()
获得缓存目录 需要申请权限 /cache
static java.lang.String	getData()
获得根目录(内部存储路径) 需要申请权限 /data
static java.io.File	getDatabasePath(android.content.Context context, java.lang.String fileName)
返回通过Context.openOrCreateDatabase 创建的数据库文件，不需要申请权限 /data/data/com.xx.xx/databases/xxFileName /data/user/0/com.xx.xx/databases/xxFileName
static java.lang.String	getDCIM()
获取照片文件夹 需要申请权限
static java.lang.String	getDOCUMENTS()
获取文档文件夹 需要申请权限
static java.lang.String	getDOWNLOADS()
获取下载文件夹 需要申请权限
static java.lang.String	getFilePath(android.content.Context context)
获取存储路径，优先使用外部储存，不需要申请权限 /storage/emulated/0/Android/data/com.xx.xx/files/ /data/data/< package name >/files/ 忽略警告：1,空指针：ConstantConditions
static java.lang.String	getFilePath(android.content.Context context, java.lang.String dir)
获取存储路径，优先使用外部储存，不需要申请权限 /storage/emulated/0/Android/data/com.xx.xx/files/xxDir /data/data/< package name >/files/xxDir 忽略警告：1,返回值不处理：ResultOfMethodCallIgnored，2,空指针：ConstantConditions
static java.lang.String	getFilesDir(android.content.Context context)
用于获取APP的files目录，不需要申请权限 /data/data/com.xx.xx/files /data/user/0/com.xx.xx/files
static java.lang.String	getMOVIES()
获取电影文件夹 需要申请权限
static java.lang.String	getMUSIC()
获取音乐文件夹 需要申请权限
static java.lang.String	getNOTIFICATIONS()
获取通知文件夹 需要申请权限
static java.lang.String	getObbDir(android.content.Context context)
用于获取APP SDK中的obb目录 /mnt/sdcard/Android/obb/com.xx.xx /storage/emulated/0/Android/obb/com.xx.xx
static java.lang.String	getPackageCodePath(android.content.Context context)
获取该程序对应的apk文件的路径，不需要申请权限 /data/app/com.xx.xx/base.apk
static java.lang.String	getPackageResourcePath(android.content.Context context)
获取该程序的安装包路径，不需要申请权限 /data/app/com.xx.xx/base.apk
static java.lang.String	getPICTURES()
获取图片文件夹 需要申请权限
static java.lang.String	getPODCASTS()
获取播客文件夹 需要申请权限
static java.lang.String	getRINGTONES()
获取铃声文件夹 需要申请权限
static java.lang.String	getRoot()
获得系统目录 需要申请权限 /system
static java.lang.String	getSDCard()
获得SD卡目录（获取的是手机外置sd卡的路径） 需要申请权限 /storage/emulated/0
```

## 类 YPermissions
```
static java.lang.String[]	getManifestPermissions(android.app.Activity activity)
获取Manifest中的全部权限
static boolean	hasPermissions(android.content.Context context, java.lang.String... permissions)
判断是否有某些权限
void	onRequestPermissionsResult(int requestCode, java.lang.String[] permissions, int[] grantResults)
权限回调
static void	request(android.app.Activity activity, java.lang.String... permissions)
获取权限
void	request(java.lang.String... permissions)
获取权限
void	requestAll()
获取全部权限
static void	requestAll(android.app.Activity activity)
获取权限
void	setFailListener(YListener1<java.util.List<java.lang.String>> failListener) 
void	setSuccessListener(YListener successListener) 
```

## 类 YPicture
```
java.io.File	createImageFile(java.lang.String path)
创建图片File
void	gotoAlbum(android.app.Activity activity)
打开相册，获取图片，支持的软件皆可
void	gotoAlbum(android.app.Activity activity, java.lang.Object flag)
打开相册，获取图片，支持的软件皆可
void	gotoAlbumDefault(android.app.Activity activity)
打开默认相册
void	gotoAlbumDefault(android.app.Activity activity, java.lang.Object flag)
打开默认相册
void	gotoCamera(android.app.Activity activity)
打开相机
void	gotoCamera(android.app.Activity activity, java.lang.Object flag)
打开相机
void	gotoCrop(android.app.Activity activity, android.net.Uri uri, int outputX, int outputY)
剪切图片
void	gotoCrop(android.app.Activity activity, android.net.Uri uri, int outputX, int outputY, java.lang.Object flag)
剪切图片
android.net.Uri	imageFile2Uri(android.content.Context context, java.io.File imageFile)
根据图片路径获取URI
void	onActivityResult(int requestCode, int resultCode, android.content.Intent data)
获取Activity返回信息
void	setPictureFromAlbumListener(YPicture.PictureFromAlbumListener pictureFromAlbumListener) 
void	setPictureFromCameraListener(YPicture.PictureFromCameraListener pictureFromCameraListener) 
void	setPictureFromCropListener(YPicture.PictureFromCropListener pictureFromCropListener) 
java.lang.String	uri2ImagePath(android.content.Context context, android.net.Uri uri)
根据URI获图片路径
```

## 类 YPropertiesUtils
```
static java.util.Map<java.lang.String,java.lang.String>	getAll(java.lang.String propertyFilePath) 
static java.lang.String	getValue(java.lang.String propertyFilePath, java.lang.String key) 
static java.lang.String	getValue(java.lang.String propertyFilePath, java.lang.String key, boolean isAbsolutePath) 
static void	main(java.lang.String[] args) 
static boolean	removeAll(java.lang.String propertyFilePath) 
static void	removeValue(java.lang.String propertyFilePath, java.lang.String key) 
static boolean	removeValue(java.lang.String propertyFilePath, java.lang.String[] key) 
static void	setValue(java.lang.String propertyFilePath, java.util.HashMap<java.lang.String,java.lang.String> htKeyValue) 
static boolean	setValue(java.lang.String propertyFilePath, java.lang.String key, java.lang.String value) 
```

## 类 YReadInputStream
```
java.io.InputStream	getInputStream() 
int	getPackageTime() 
boolean	isAutoPackage() 
static boolean	isShowLog() 
static YBytes	read(java.io.InputStream mInputStream, int groupPackageTime)
读取InputStream
static YBytes	read(java.io.InputStream mInputStream, int groupPackageTime, int readTimeOut, int readLength)
指定时间内读取指定长度的InputStream
void	setAutoPackage(boolean autoPackage) 
void	setInputStream(java.io.InputStream inputStream) 
void	setLengthAndTimeout(int readLength, int readTimeout) 
void	setPackageTime(int packageTime) 
void	setReadListener(YListener1<byte[]> readListener) 
static void	setShowLog(boolean showLog) 
void	start() 
void	stop() 
```

## 类 YSave
把对象保存到文件
```
static YSave	create(android.content.Context context) 
static YSave	create(android.content.Context context, java.lang.String path) 
static YSave	create(android.content.Context context, java.lang.String path, java.lang.String extensionName) 
static <T> T	get(android.content.Context context, java.lang.String key, java.lang.Class<T> classOfT) 
static <T> T	get(android.content.Context context, java.lang.String key, java.lang.Class<T> classOfT, java.lang.Object defaultObject) 
static java.lang.Object	get(android.content.Context context, java.lang.String key, java.lang.reflect.Type type) 
static java.lang.Object	get(android.content.Context context, java.lang.String key, java.lang.reflect.Type type, java.lang.Object defaultObject) 
<T> T	get(java.lang.String key, java.lang.Class<T> classOfT) 
<T> T	get(java.lang.String key, java.lang.Class<T> classOfT, java.lang.Object defaultObject) 
java.lang.Object	get(java.lang.String key, java.lang.reflect.Type type) 
java.lang.Object	get(java.lang.String key, java.lang.reflect.Type type, java.lang.Object defaultObject) 
java.lang.String	getExtensionName() 
java.io.File	getFile(java.lang.String key) 
static YSave	getInstance(android.content.Context context) 
java.lang.String	getPath() 
static java.lang.String	getString(android.content.Context context, java.lang.String key) 
java.lang.String	getString(java.lang.String key) 
static boolean	isUseCache() 
static <T> void	put(android.content.Context context, java.lang.String key, T data) 
void	put(java.lang.String key, java.lang.Object data) 
static void	remove(android.content.Context context, java.lang.String key) 
void	remove(java.lang.String key) 
void	removeAll() 
static void	removeAll(android.content.Context context) 
YSave	setExtensionName(java.lang.String extensionName) 
YSave	setPath(java.lang.String path) 
static void	setUseCache(boolean useCache) 
```

## 类 YScreenUtil
```
static int	dp2px(android.content.Context context, float dpValue)
dip转换为px大小
static int	dp2px(float dpValue)
dip转px
static float	getDensity(android.content.Context context)
获取系统dp尺寸密度值
static float	getDensityDpi(android.content.Context context)
获取DPI
static android.util.DisplayMetrics	getDisplayMetrics(android.content.Context context)
获取DisplayMetrics对象
static int	getHeightPixels(android.app.Activity activity)
获取屏幕高度
static float	getScaledDensity(android.content.Context context)
获取系统字体sp密度值
static int	getScreenHeight(android.content.Context context)
获取屏幕高度，单位为px
static int	getScreenWidth(android.content.Context context)
获取屏幕宽度，单位为px
static int	getStatusHeight(android.content.Context context)
获得状态栏的高度
static int	getWidthPixels(android.app.Activity activity)
获取屏幕宽度
static int	px2dp(android.content.Context context, int pxValue)
px转换为dp值
static int	px2dp(int pxValue)
px转dip
static int	px2sp(android.content.Context context, int pxValue)
px转换为sp
static android.graphics.Bitmap	snapShotWithoutStatusBar(android.app.Activity activity)
获取当前屏幕截图，不包含状态栏
static android.graphics.Bitmap	snapShotWithStatusBar(android.app.Activity activity)
获取当前屏幕截图，包含状态栏
static int	sp2px(android.content.Context context, float spValue)
sp转换为px
```

## 类 YSharedPreferencesUtils
```
static void	delete(android.content.Context context, java.lang.String key) 
static void	delete(android.content.Context context, java.lang.String fileName, int mode, java.lang.String key) 
static void	delete(android.content.Context context, java.lang.String fileName, java.lang.String key) 
static java.lang.String	get(android.content.Context context, java.lang.String key) 
static java.lang.String	get(android.content.Context context, java.lang.String fileName, int mode, java.lang.String key)
从SharedPreferences文件中读取指定Key的value
static java.lang.String	get(android.content.Context context, java.lang.String fileName, java.lang.String key) 
static boolean	getBoolean(android.content.Context context, java.lang.String key) 
static boolean	getBoolean(android.content.Context context, java.lang.String fileName, int mode, java.lang.String key) 
static boolean	getBoolean(android.content.Context context, java.lang.String fileName, java.lang.String key) 
static int	getInt(android.content.Context context, java.lang.String key) 
static int	getInt(android.content.Context context, java.lang.String fileName, int mode, java.lang.String key) 
static int	getInt(android.content.Context context, java.lang.String fileName, java.lang.String key) 
static void	write(android.content.Context context, java.lang.String fileName, int mode, java.lang.String key, java.lang.String value)
向SharedPreferences文件中写入key和value Example：SharedPreferencesUtils.writeInSharedPreferences (LoginActivity.this, "role", Context.MODE_PRIVATE, "role", resultStr);
static void	write(android.content.Context context, java.lang.String key, java.lang.String value) 
static void	write(android.content.Context context, java.lang.String fileName, java.lang.String key, java.lang.String value) 
static void	writeBoolean(android.content.Context context, java.lang.String key, boolean value) 
static void	writeBoolean(android.content.Context context, java.lang.String fileName, int mode, java.lang.String key, boolean value) 
static void	writeBoolean(android.content.Context context, java.lang.String fileName, java.lang.String key, boolean value) 
static void	writeInt(android.content.Context context, java.lang.String key, int value) 
static void	writeInt(android.content.Context context, java.lang.String fileName, int mode, java.lang.String key, int value) 
static void	writeInt(android.content.Context context, java.lang.String fileName, java.lang.String key, int value) 
```

## 类 YShow
半透明等待对话框

## 类 YString
```
static java.util.List<java.lang.StringBuilder>	group(java.lang.String str, int digit)
字符串分组，每digit位字符拆分一次字符串，中文英文都算一个字符
static java.util.List<java.lang.StringBuilder>	groupDouble(java.lang.String str, int digit)
字符串分组，每digit位字符拆分一次字符串，英文算一个字符，中文算两个字符
static java.lang.String	insert(java.lang.String str, int digit, java.lang.String insertString)
字符串每隔digit位添加一个符号
static java.lang.String	ToDBC(java.lang.String input)
全角转半角
static java.lang.String	ToSBC(java.lang.String input)
半角转全角
```

## 类 YThreadPool
```
static void	add(java.lang.Thread thread)
把一个线程扔进线程池
static int	getPoolSize()
获取当前有多少线程
static void	setThreadNum(int threadNum)
释放当前线程池，并重新创建线程池一个最大值未threadNum的线程池
static void	shutdown()
关闭释放线程池
static void	stopAll()
停止当前队列中全部请求
```

## 类 YToast
```
static int	getQueueTime()
获取队列显示时间
static void	setQueueTime(int queueTime)
设置队列显示时间
static void	show(android.content.Context context, java.lang.String text)
多条toast同时过来，只显示最后一条，显示时间为LENGTH_SHORT
static void	showLong(android.content.Context context, java.lang.String text)
多条toast同时过来，只显示最后一条，显示时间为LENGTH_LONG
static void	showQueue(android.content.Context context, java.lang.String text)
多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
static void	showQueueLong(android.content.Context context, java.lang.String text)
多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
```

## 类 YTts
```
float	getPitch()
获取音调
float	getSpeechRate()
获取播放速度
android.speech.tts.TextToSpeech	getTextToSpeech()
获取textToSpeech对象
static YTts	getYTtsInstance(android.content.Context context) 
static YTts	getYTtsInstance(android.content.Context context, YListener1<java.lang.Boolean> listener) 
boolean	isInitSuccess()
获取初始化状态
void	onDestroy()
关闭，释放资源
void	onStop()
停止
void	setPitch(float pitch)
设置音调
void	setSpeechRate(float speechRate)
设置播放速度
void	setSpeechRatePitch(float speechRate, float pitch)
设置速度和音调
void	speak(java.lang.String speak)
语音播放
void	speak(java.lang.String speak, float speechRate, float pitch)
语音播放
void	speakQueue(java.lang.String speak)
语音队列播放
void	speakQueue(java.lang.String speak, float speechRate, float pitch)
语音队列播放
```

## 类 YUri
```
static android.graphics.Bitmap	getBitmap(android.content.Context context, android.net.Uri uri)
uri转换成Bitmap
static java.lang.String	getPath(android.content.Context context, android.net.Uri uri)
URI转文件路径 全平台处理方法
static java.lang.String	getPathForN(android.content.Context context, android.net.Uri uri)
URI转文件路径 android7.0以上处理方法
static android.net.Uri	getUri(android.content.Context context, java.io.File file)
file转URI
static android.net.Uri	saveBitmap2uri(java.lang.String path, android.graphics.Bitmap mBitmap)
保存文件返回uri
```

## 类 YWebView
```
static void	init(android.webkit.WebView webView, java.lang.String url)
初始化WebView
static void	initBackgroundAlpha(android.webkit.WebView webView, java.lang.String url)
初始化WebView,背景透明
static void	initDefault(android.webkit.WebView webView, java.lang.String url)
初始化WebView
static void	setBackgroundAlpha(android.webkit.WebView webView)
设置WebView背景透明
static void	setClient(android.webkit.WebView webView)
设置WebView跳转拦截
static void	setSettings(android.webkit.WebView webView)
设置WebView
```

## 类 YFragmentManager
```
void	hideCurrent()
隐藏当前fragment
void	hideFragment(Fragment targetFragment)
隐藏fragment
void	showCurrent()
显示当前fragment
void	showFragment(Fragment targetFragment)
显示fragment
```

## 类 Y3des
```
static byte[]	decode(byte[] bytes, byte[] key)
3DES解密
static byte[]	encode(byte[] bytes, byte[] key)
3DES加密
static byte[]	getKey() 
```

## 类 YAes
```
static java.security.Key	createKey()
创建一个随机秘钥
static byte[]	decrypt(byte[] result, java.security.Key key)
解密
static byte[]	decrypt(byte[] bytes, java.lang.String password)
解密
static java.lang.String	decryptFromBase64(java.lang.String base64, java.lang.String password) 
static java.lang.String	decryptFromHex(java.lang.String hexText, java.lang.String password) 
static byte[]	encrypt(byte[] bytes, java.lang.String password)
加密
static byte[]	encrypt(java.lang.String context, java.security.Key key)
加密
static java.lang.String	encryptToBase64(java.lang.String data, java.lang.String password) 
static java.lang.String	encryptToHex(java.lang.String data, java.lang.String password) 
static javax.crypto.spec.SecretKeySpec	getKey() 
```

## 类 YDes
```
static byte[]	decode(byte[] data, byte[] pwd)
DES 解密算法
static byte[]	encode(byte[] data, byte[] pwd)
DES加密算法
```

## 类 YEncrypt
```
自定义加密解密类
byte[]	decode(byte[] byteArray) 
byte[]	decode(byte[] byteArray, java.lang.String passWord) 
java.lang.String	decode(java.lang.String psw) 
java.lang.String	decode(java.lang.String psw, java.lang.String passWord) 
byte[]	decodeFast(byte[] byteArray) 
byte[]	encode(byte[] byteArray) 
byte[]	encode(byte[] byteArray, java.lang.String passWord) 
java.lang.String	encode(java.lang.String str) 
java.lang.String	encode(java.lang.String str, java.lang.String passWord) 
byte[]	encodeFast(byte[] byteArray) 
```

## 类 YGzip
```
static byte[]	compress(byte[] data)
数据压缩
static void	compress(java.io.File file, boolean delete)
文件压缩
static void	compress(java.io.InputStream is, java.io.OutputStream os)
数据压缩
static byte[]	decompress(byte[] data)
数据解压缩
static void	decompress(java.io.File file, boolean delete)
文件解压缩
static void	decompress(java.io.InputStream is, java.io.OutputStream os)
数据解压缩
```

## 类 YMD5Util
static java.lang.String	MD5(java.lang.String strObj) 

## 类 YOneBitCrypt
```
static byte[]	decrypt(byte[] bytes, byte password)
机密算法，与上面加密算法反之
static byte[]	encrypt(byte[] bytes, byte password)
一位加密，第一步奇数偶数位置互换，比如原始byte数组为[1,2,3,4,5,6,7,8],第一步过后数组为[2,1,4,3,5,6,8,7]。
static byte	getPassword(byte password)
计算对称密码
```

## 类 YRsa
```
static byte	asc_to_bcd(byte asc) 
static byte[]	ASCII_To_BCD(byte[] ascii, int asc_len)
ASCII码转BCD码
static java.lang.String	bcd2Str(byte[] bytes)
BCD转字符串
static java.lang.String	decryptByPrivateKey(java.lang.String data, java.security.interfaces.RSAPrivateKey privateKey)
私钥解密
static java.lang.String	encryptByPublicKey(java.lang.String data, java.security.interfaces.RSAPublicKey publicKey)
公钥加密
static java.util.HashMap<java.lang.String,java.lang.Object>	getKeys()
生成公钥和私钥
static java.security.interfaces.RSAPrivateKey	getPrivateKey(java.lang.String modulus, java.lang.String exponent)
使用模和指数生成RSA私钥 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA /None/NoPadding】
static java.security.interfaces.RSAPublicKey	getPublicKey(java.lang.String modulus, java.lang.String exponent)
使用模和指数生成RSA公钥 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA /None/NoPadding】
static byte[][]	splitArray(byte[] data, int len)
拆分数组
static java.lang.String[]	splitString(java.lang.String string, int len)
拆分字符串
```

### 列举了一下主要类的方法，具体每个类方法和和使用，请看文档或源码，欢迎给我提出修改意见。

Github地址：[https://github.com/yutils/yutils](https://github.com/yutils/yutils)

我的CSDN：[https://blog.csdn.net/Yu1441](https://blog.csdn.net/Yu1441)

感谢关注微博：[细雨若静](https://weibo.com/32005200)