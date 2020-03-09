package com.yujing.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 循环调用某一个类中的某一个方法
 * 被调用的方法需要用public修辞，而且被调用的方法没有形参和返回值
 *
 * @author yujing 2019年1月8日10:20:40
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class YLoop {
    // 记录哪些类的哪些方法正在被调用
    private static final Map<String, Boolean> MethodStatus = new HashMap<>();

    public static void stop(Object obj, String methodName) {
        MethodStatus.put(sign(obj, methodName), false);
    }

    /**
     * @param obj        哪个类
     * @param methodName 哪个方法名称
     * @param interval   间隔时间，每次执行该方法的时间间隔
     */
    public static void start(final Object obj, final String methodName, final int interval) {
        start(obj, methodName, interval, 0);
    }

    /**
     * @param obj        对象
     * @param methodName 方法名
     * @param interval   间隔时间，每次执行该方法的时间间隔
     * @param cycleNum   循环次数，小于等于0就无线循环，否者循环自定次数
     */
    @SuppressWarnings("WeakerAccess")
    public static void start(final Object obj, final String methodName, final int interval, final int cycleNum) {
        try {
            final Method method = getMethod(obj, methodName);// obj.getClass().getDeclaredMethod(string);//找到对应名称的方法
            if (method == null) {
                System.err.println("LoopERROR\nmethodNameNoFind：" + methodName);
                return;
            }
            method.setAccessible(true);// 允许调用private权限的方法
            final String sign = sign(obj, methodName);
            MethodStatus.put(sign, true);// 记录该方法被启用
            Thread thread = new Thread(new Runnable() {
                int num = cycleNum;// 倒计时剩下执行次数

                @Override
                public void run() {
                    while (MethodStatus.get(sign) != null && MethodStatus.get(sign)) {
                        try {
                            // 判断循环次数是否到了。循环次数，cycleNum小于等于0就无线循环，否者循环自定次数
                            if (cycleNum > 0 && num <= 0) {
                                break;
                            }
                            num--;// 循环次数减1
                            method.invoke(obj);
                            Thread.sleep(interval);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    MethodStatus.put(sign, false);
                    MethodStatus.remove(sign);
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据obj和方法名创建唯一标识符
     *
     * @param obj        对象
     * @param methodName 方法名
     * @return sign唯一标示
     */
    private static String sign(Object obj, String methodName) {
        return obj.hashCode() + obj.getClass().getName() + methodName;
    }

    /**
     * 根据名称获取方法
     *
     * @param obj        要获取的对象
     * @param methodName 方法名
     * @return Method
     */
    private static Method getMethod(Object obj, String methodName) {
        Method method = null;
        // 循环读取类，直到读取到Object类为止
        beakLoop:
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (clazz == null) {
                return null;
            }
            // 获取遍历当前类全部方法
            Method[] methods = clazz.getMethods();
            for (Method method1 : methods) {
                // 判断当前方法名，是不是正好需要用的方法
                if (method1.getName().equals(methodName)) {
                    // 获取该方法的该类有多少参数
                    Class<?>[] pt = method1.getParameterTypes();
                    // 判断参数正好只有0个参数
                    if (pt.length == 0) {
                        method = method1;
                        // 直接跳出外层循环
                        break beakLoop;
                    }
                }
            }
        }
        return method;
    }
}
