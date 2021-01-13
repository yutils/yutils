package com.yujing.bus

import java.lang.annotation.Inherited

/**
 * bus注解
 * @author yujing 2021年1月12日11:09:26
 */
/*用法

//注册该类
YBusUtil.init(this)

//发送消息
YBusUtil.post("tag1","123456789")

//接收消息
@YBus("tag1")
fun message(message: Any) {
    YLog.i("收到：$message")
}

//接收全部消息
@YBus()
fun message(key: Any,message: Any) {
    YLog.i("收到：$key:$message")
    textView1.text = "收到：$key:$message"
}

//解绑该类
YBusUtil.onDestroy(this)
*/
@MustBeDocumented
@Inherited
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.LOCAL_VARIABLE
)
annotation class YBus(vararg val tag: String)
/*___________________________________华丽的分割线,下面是注解______________________________________
 @Target(ElementType.TYPE) //接口、类、枚举、注解
 @Target(ElementType.FIELD) //字段、枚举的常量
 @Target(ElementType.METHOD) //方法
 @Target(ElementType.PARAMETER) //方法参数
 @Target(ElementType.CONSTRUCTOR) //构造函数
 @Target(ElementType.LOCAL_VARIABLE)//局部变量
 @Target(ElementType.ANNOTATION_TYPE)//注解
 @Target(ElementType.PACKAGE) ///包
*/