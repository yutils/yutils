package com.yujing.bus

import android.os.Build
import com.yujing.utils.YLog
import com.yujing.utils.YThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.reflect.Method
import java.util.Vector
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * YBus 改进版：注册期建索引，运行期零扫描
 *
 * 用法保持不变：
 *  - YBusUtil.init(this) / register(this) / unregister(this)
 *  - YBusUtil.post(tag, value) / postSticky(tag, value)
 */
@Suppress("unused")
class YBusUtil {
    companion object {
        // 与旧版保持字段名一致（若外部有引用）
        @JvmStatic
        var listObject: Vector<Any> = Vector()

        @JvmStatic
        var anySticky: Vector<YMessage<Any>> = Vector()

        // -------- 索引与缓存 --------
        // 类 -> 该类的订阅方法（已提取注解信息、参数信息）
        private val methodCache = ConcurrentHashMap<Class<*>, List<SubscriberMethod>>()

        // tag -> 订阅（只含显式 tag 的方法）
        private val tagIndex = ConcurrentHashMap<String, CopyOnWriteArrayList<Subscription>>()

        // 通配（@YBus() 或 value=[""]）
        private val wildcardIndex = CopyOnWriteArrayList<Subscription>()

        // 对象 -> 该对象的所有订阅（用于快速注销/定向分发）
        private val objectSubs = ConcurrentHashMap<Any, List<Subscription>>()

        //创建作用域  busScope?.launch(Dispatchers.IO) {}
        var busScope: CoroutineScope? = null
            get() {
                // 检查当前作用域是否有效（非空且未取消）
                if (field != null && field!!.coroutineContext[Job]?.isCancelled == false) {
                    return field
                }
                // 无效则创建新作用域（添加默认调度器，如Dispatchers.Default）
                field = CoroutineScope(SupervisorJob() + Dispatchers.Default)
                return field
            }

        // -------- 对外 API --------
        @JvmStatic
        fun init(anyObject: Any) = register(anyObject)

        @JvmStatic
        fun register(anyObject: Any) {
            if (listObject.contains(anyObject)) return
            listObject.add(anyObject)

            // 1) 取或建该类的方法索引
            val clazz = anyObject.javaClass
            val methods = methodCache.getOrPut(clazz) { findSubscriberMethods(clazz) }

            // 2) 将对象实例化后的订阅放入全局索引
            val subs = ArrayList<Subscription>(methods.size)
            for (m in methods) {
                val sub = Subscription(anyObject, m)
                subs.add(sub)
                if (m.isWildcard) {
                    // 通配
                    if (!wildcardIndex.contains(sub)) wildcardIndex.add(sub)
                } else {
                    for (tag in m.tags) {
                        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            tagIndex.computeIfAbsent(tag) { CopyOnWriteArrayList() }
                        } else {
                            // 低版本兼容实现
                            synchronized(tagIndex) { // 同步处理，保证线程安全
                                var list = tagIndex[tag]
                                if (list == null) {
                                    list = CopyOnWriteArrayList()
                                    tagIndex[tag] = list
                                }
                                list
                            }
                        }
                        if (!list.contains(sub)) list.add(sub)
                    }
                }
            }
            objectSubs[anyObject] = subs

            // 3) 补发 sticky（仅发给该对象自身）
            if (anySticky.isNotEmpty()) {
                for (msg in anySticky) {
                    dispatchToTarget(anyObject, msg)
                }
            }
        }

        @JvmStatic
        fun unregister(anyObject: Any) {
            listObject.remove(anyObject)
            val subs = objectSubs.remove(anyObject) ?: return
            for (sub in subs) {
                val sm = sub.subscriberMethod
                if (sm.isWildcard) {
                    wildcardIndex.remove(sub)
                } else {
                    for (tag in sm.tags) {
                        tagIndex[tag]?.remove(sub)
                    }
                }
            }
        }

        @JvmStatic
        fun onDestroy(anyObject: Any) = unregister(anyObject)

        @JvmStatic
        fun destroyAll() {
            listObject.clear()
            objectSubs.clear()
            tagIndex.clear()
            wildcardIndex.clear()
        }

        // 发送事件（零扫描分发）
        @JvmStatic
        fun post(tag: String, value: Any?) = subscribe(YMessage(tag, value))

        @JvmStatic
        fun post(tag: String) = subscribe(YMessage(tag, null))

        //串行
        @OptIn(ExperimentalCoroutinesApi::class)
        private val serialScope = CoroutineScope(Dispatchers.Default.limitedParallelism(1))

        //串行消息
        fun postSerial(tag: String, value: Any?) {
            serialScope.launch {
                subscribe(YMessage(tag, value))
            }
        }

        @JvmStatic
        private fun subscribe(yMessage: YMessage<Any>) {
            try {
                // 命中显式 tag + 通配
                val targets = ArrayList<Subscription>()
                tagIndex[yMessage.type ?: ""]?.let { targets.addAll(it) }
                if (wildcardIndex.isNotEmpty()) targets.addAll(wildcardIndex)

                if (targets.isEmpty()) return
                for (sub in targets) {
                    // 显式 tag 的方法天然匹配；通配方法需要按签名做一次轻量判断
                    if (!matches(sub.subscriberMethod, yMessage)) continue
                    invokeByThreadMode(sub, yMessage)
                }
            } catch (e: Throwable) {
                YLog.e("YBus", "分发异常：${e.message}", e)
            }
        }

        // 发送 sticky
        @JvmStatic
        fun postSticky(tag: String, value: Any?) {
            var msg: YMessage<Any>? = null
            for (m in anySticky) {
                if (m.type == tag) {
                    m.data = value
                    msg = m
                    break
                }
            }
            if (msg == null) {
                msg = YMessage(tag, value)
                anySticky.add(msg)
            }
            subscribe(msg)
        }

        @JvmStatic
        fun postSticky(tag: String) = postSticky(tag, null)

        @JvmStatic
        fun removeSticky(tag: String) {
            for (i in anySticky.size - 1 downTo 0) if (anySticky[i].type == tag) anySticky.removeAt(i)
        }

        @JvmStatic
        fun clearSticky() = anySticky.clear()

        // -------- 提供给 Utils.execute 的“定向分发”，兼容旧调用点 --------
        internal fun dispatchToTarget(targetObj: Any, yMessage: YMessage<Any>) {
            val subs = objectSubs[targetObj] ?: return
            for (sub in subs) {
                val sm = sub.subscriberMethod
                if (!sm.isWildcard && (yMessage.type == null || !sm.tags.contains(yMessage.type))) continue
                if (!matches(sm, yMessage)) continue
                invokeByThreadMode(sub, yMessage)
            }
        }

        // ===== 索引构建 =====
        private fun findSubscriberMethods(clazz: Class<*>): List<SubscriberMethod> {
            val list = ArrayList<SubscriberMethod>()
            var c: Class<*>? = clazz
            while (c != null && c != Any::class.java && c != Object::class.java) {
                for (m in c.declaredMethods) {
                    val ann = m.getAnnotation(YBus::class.java) ?: continue
                    m.isAccessible = true
                    val tags = ann.value
                    val tagList = if (tags.isEmpty() || (tags.size == 1 && tags[0].isEmpty())) emptyList() else tags.toList()
                    val isWildcard = tagList.isEmpty()
                    val paramTypes = m.parameterTypes
                    val pattern = computePattern(isWildcard, paramTypes)
                    val dataType = when (pattern) {
                        CallPattern.WILDCARD_DATA1 -> paramTypes[0]
                        CallPattern.WILDCARD_TAG_DATA2 -> if (paramTypes.size >= 2) paramTypes[1] else null
                        else -> null
                    }
                    list.add(
                        SubscriberMethod(
                            method = m,
                            threadMode = ann.threadMode,
                            tags = tagList,
                            isWildcard = isWildcard,
                            paramTypes = paramTypes,
                            pattern = pattern,
                            expectedDataType = dataType
                        )
                    )
                }
                c = c.superclass
            }
            return list
        }

        private fun computePattern(isWildcard: Boolean, params: Array<Class<*>>): CallPattern {
            return if (isWildcard) {
                when (params.size) {
                    0 -> CallPattern.WILDCARD_0
                    1 -> if (YMessage::class.java.isAssignableFrom(params[0]))
                        CallPattern.WILDCARD_MSG else CallPattern.WILDCARD_DATA1

                    else -> CallPattern.WILDCARD_TAG_DATA2 // (tag, data)
                }
            } else {
                when (params.size) {
                    0 -> CallPattern.TAG_0
                    1 -> CallPattern.TAG_DATA1         // (data)
                    else -> CallPattern.TAG_TAGDATA2   // (tag, data)
                }
            }
        }

        // ===== 运行期匹配 & 调用 =====
        private fun matches(sm: SubscriberMethod, msg: YMessage<Any>): Boolean {
            return when (sm.pattern) {
                CallPattern.WILDCARD_0,
                CallPattern.WILDCARD_MSG,
                CallPattern.TAG_0,
                CallPattern.TAG_DATA1,
                CallPattern.TAG_TAGDATA2,
                    -> true

                CallPattern.WILDCARD_DATA1 -> {
                    val d = msg.data ?: return false
                    sm.expectedDataType?.isAssignableFrom(d.javaClass) == true
                }

                CallPattern.WILDCARD_TAG_DATA2 -> {
                    val d = msg.data ?: return true /* 允许 (tag, null) */
                    sm.expectedDataType?.isAssignableFrom(d.javaClass) == true
                }
            }
        }

        private fun invokeByThreadMode(sub: Subscription, msg: YMessage<Any>) {
            val sm = sub.subscriberMethod
            val r = { safeInvoke(sub.target, sm, msg) }

            when (sm.threadMode) {
                ThreadMode.CURRENT -> r()
                ThreadMode.MAIN -> {
                    if (YThread.isMainThread()) {
                        r()
                    } else {
                        // 切回主线程执行
                        busScope?.launch(Dispatchers.Main) { r() }
                    }
                }

                ThreadMode.IO -> {
                    if (YThread.isMainThread()) {
                        // 切到 IO 线程池
                        busScope?.launch(Dispatchers.IO) { r() }
                    } else {
                        r()
                    }
                }
            }
        }

        private fun safeInvoke(target: Any, sm: SubscriberMethod, msg: YMessage<Any>) {
            try {
                val m = sm.method
                when (sm.pattern) {
                    CallPattern.WILDCARD_0,
                    CallPattern.TAG_0,
                        -> m.invoke(target)

                    CallPattern.WILDCARD_MSG -> m.invoke(target, msg)

                    CallPattern.WILDCARD_DATA1 -> m.invoke(target, msg.data)

                    CallPattern.WILDCARD_TAG_DATA2 -> m.invoke(target, msg.type, msg.data)

                    CallPattern.TAG_DATA1 -> m.invoke(target, msg.data)

                    CallPattern.TAG_TAGDATA2 -> m.invoke(target, msg.type, msg.data)
                }
            } catch (t: Throwable) {
                // 复用你原来的错误提示语义
                val methodName = sm.method.name
                val clsName = target.javaClass.name
                YLog.e("YBus", "调用异常：类=$clsName，方法=$methodName，消息=$msg", t)
            }
        }
    }
}

// ======== 内部结构体 ========
private enum class CallPattern {
    // 无 tag（通配）
    WILDCARD_0,           // ()
    WILDCARD_MSG,         // (YMessage)
    WILDCARD_DATA1,       // (data)
    WILDCARD_TAG_DATA2,   // (tag, data)

    // 显式 tag
    TAG_0,                // ()
    TAG_DATA1,            // (data)
    TAG_TAGDATA2          // (tag, data)
}

private data class SubscriberMethod(
    val method: Method,
    val threadMode: ThreadMode,
    val tags: List<String>,
    val isWildcard: Boolean,
    val paramTypes: Array<Class<*>>,
    val pattern: CallPattern,
    val expectedDataType: Class<*>?,
)

private data class Subscription(
    val target: Any,
    val subscriberMethod: SubscriberMethod,
)
