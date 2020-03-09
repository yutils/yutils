package com.yujing.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 初始化View，找控件
 *
 * @author yujing 2018年7月18日16:11:11
 */
@SuppressWarnings("unused")
public class YInitView {
    //************************Activity****************************
    public static void init(Activity activity) {
        try {
            // 找自定义方法注释
            Method[] methods = activity.getClass().getDeclaredMethods();
            for (final Method method : methods) {
                method.setAccessible(true);//允许调用私有方法
                setOnclickByActivity(activity, method);// 设置监听
            }
            // 找自定义变量注释
            Field[] fields = activity.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);//允许调用私有变量
                setFindviewByActivity(activity, field);// 找到R.id.XXX
                setbackByActivity(activity, field);
                setstartActivityByActivity(activity, field);
            }
        } catch (IllegalAccessException e) {
            System.out.println("调用方法权限不足");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("接口接收参数个数不匹配");
            e.printStackTrace();
        }
    }

    private static void setOnclickByActivity(final Object object, final Method method) {
        // 判断这个methods上是否有这个注解
        if (method.isAnnotationPresent(OnClick.class)) {
            OnClick onclick = method.getAnnotation(OnClick.class);
            if (onclick != null) {
                for (int id : onclick.value()) {//onclick.value()为数组
                    View view = ((Activity) object).findViewById(id);
                    if (view == null) {
                        Log.e("错误", "设置OnClickListener时没有找到该View");
                        continue;
                    }
                    view.setOnClickListener(view1 -> {
                        try {
                            Class<?>[] value = method.getParameterTypes();
                            if (value.length == 1) {//判断参数个数
                                method.invoke(object, view1);
                            } else {
                                method.invoke(object);
                            }
                        } catch (IllegalAccessException e) {
                            System.out.println("调用方法权限不足");
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            System.out.println("接口接收参数个数不匹配");
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            System.out.println("调用目标异常");
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private static void setFindviewByActivity(Object object, Field field)
            throws IllegalAccessException, IllegalArgumentException {
        // 判断这个Field上是否有这个注解
        if (field.isAnnotationPresent(FindView.class)) {
            // 如果有这个注解，则获取注解类
            FindView findview = field.getAnnotation(FindView.class);
            if (findview != null) {
                View view = ((Activity) object).findViewById(findview.value());
                if (view == null) {
                    Log.e("错误", "设置Findview时没有找到该View");
                    return;
                }
                field.set(object, view);
            }
        }
    }

    private static void setbackByActivity(final Object object, Field field)
            throws IllegalAccessException, IllegalArgumentException {
        // 判断这个Field上是否有这个注解
        if (field.isAnnotationPresent(Back.class)) {
            // 如果有这个注解，则获取注解类
            Back back = field.getAnnotation(Back.class);
            if (back != null) {
                View view = ((Activity) object).findViewById(back.value());
                field.set(object, view);
                view.setOnClickListener(arg0 -> ((Activity) object).finish());
            }
        }
    }

    private static void setstartActivityByActivity(final Object object, Field field)
            throws IllegalAccessException, IllegalArgumentException {
        // 判断这个Field上是否有这个注解
        if (field.isAnnotationPresent(StartActivity.class)) {
            // 如果有这个注解，则获取注解类
            final StartActivity startActivity = field.getAnnotation(StartActivity.class);
            if (startActivity != null) {
                View view = ((Activity) object).findViewById(startActivity.value());
                field.set(object, view);
                view.setOnClickListener(arg0 -> {
                    Intent intent = new Intent(((Activity) object), startActivity.activity());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ((Activity) object).startActivity(intent);
                });
            }
        }
    }

    //************************Fragment****************************
    public static void initFragment(Fragment fragment, View view) {
        try {
            // 找自定义方法注释
            Method[] methods = fragment.getClass().getDeclaredMethods();
            for (final Method method : methods) {
                method.setAccessible(true);//允许调用私有方法
                setOnclickByView(fragment, method, view);// 设置监听
            }
            // 找自定义变量注释
            Field[] fields = fragment.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);//允许调用私有变量
                setFindviewByView(fragment, field, view);// 找到R.id.XXX
                setbackByFragment(fragment, field, view);
                setstartActivityByView(fragment, field, view);
            }
        } catch (IllegalAccessException e) {
            System.out.println("调用方法权限不足");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("接口接收参数个数不匹配");
            e.printStackTrace();
        }
    }

    private static void setOnclickByView(final Object object, final Method method, View v) {
        // 判断这个methods上是否有这个注解
        if (method.isAnnotationPresent(OnClick.class)) {
            OnClick onclick = method.getAnnotation(OnClick.class);
            if (onclick != null) {
                for (int id : onclick.value()) {//onclick.value()为数组
                    View view = v.findViewById(id);
                    if (view == null) {
                        Log.e("错误", "设置OnClickListener时没有找到该View");
                        continue;
                    }
                    view.setOnClickListener(view1 -> {
                        try {
                            Class<?>[] value = method.getParameterTypes();
                            if (value.length == 1) {//判断参数个数
                                method.invoke(object, view1);
                            } else {
                                method.invoke(object);
                            }
                        } catch (IllegalAccessException e) {
                            System.out.println("调用方法权限不足");
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            System.out.println("接口接收参数个数不匹配");
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            System.out.println("调用目标异常");
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private static void setFindviewByView(Object object, Field field, View v)
            throws IllegalAccessException, IllegalArgumentException {
        // 判断这个Field上是否有这个注解
        if (field.isAnnotationPresent(FindView.class)) {
            // 如果有这个注解，则获取注解类
            FindView findview = field.getAnnotation(FindView.class);
            if (findview != null) {
                View view = v.findViewById(findview.value());
                if (view == null) {
                    Log.e("错误", "设置Findview时没有找到该View");
                    return;
                }
                field.set(object, view);
            }
        }
    }

    private static void setbackByFragment(final Object object, Field field, View v)
            throws IllegalAccessException, IllegalArgumentException {
        // 判断这个Field上是否有这个注解
        if (field.isAnnotationPresent(Back.class)) {
            // 如果有这个注解，则获取注解类
            Back back = field.getAnnotation(Back.class);
            if (back != null) {
                View view = v.findViewById(back.value());
                field.set(object, view);
                view.setOnClickListener(arg0 -> {
                    if (object instanceof Fragment) {
                        Objects.requireNonNull(((Fragment) object).getActivity()).finish();
                    } else if (object instanceof Activity) {
                        ((Activity) object).finish();
                    }
                });
            }
        }
    }

    private static void setstartActivityByView(final Object object, Field field, View v)
            throws IllegalAccessException, IllegalArgumentException {
        // 判断这个Field上是否有这个注解
        if (field.isAnnotationPresent(StartActivity.class)) {
            // 如果有这个注解，则获取注解类
            final StartActivity startActivity = field.getAnnotation(StartActivity.class);
            if (startActivity != null) {
                View view = v.findViewById(startActivity.value());
                field.set(object, view);
                view.setOnClickListener(arg0 -> {
                    if (object instanceof Fragment) {
                        Intent intent = new Intent(((Fragment) object).getActivity(), startActivity.activity());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Objects.requireNonNull(((Fragment) object).getActivity()).startActivity(intent);
                    } else if (object instanceof Activity) {
                        Intent intent = new Intent(((Activity) object), startActivity.activity());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ((Activity) object).startActivity(intent);
                    }
                });
            }
        }
    }

    //************************View Tag****************************
    public static View initByTag(final Context context, int layout, int tag) {
        View view = LayoutInflater.from(context).inflate(layout, null);
        initByTag(context, view, tag);
        return view;
    }

    public static void initByTag(final Object object, View view, int tag) {
        try {
            // 找自定义方法注释
            Method[] methods = object.getClass().getDeclaredMethods();
            for (final Method method : methods) {
                method.setAccessible(true);//允许调用私有方法
                //设置按键
                if (method.isAnnotationPresent(OnClickByTag.class)) {
                    OnClickByTag onclick = method.getAnnotation(OnClickByTag.class);
                    if (onclick != null && onclick.tag() == tag) {
                        for (int id : onclick.id()) {//onclick.value()为数组
                            View viewTemp = view.findViewById(id);
                            if (viewTemp == null) {
                                Log.e("错误", "设置OnClickListener时没有找到该View");
                                continue;
                            }
                            viewTemp.setOnClickListener(view1 -> {
                                try {
                                    Class<?>[] value = method.getParameterTypes();
                                    if (value.length == 1) {//判断参数个数
                                        method.invoke(object, view1);
                                    } else {
                                        method.invoke(object);
                                    }
                                } catch (IllegalAccessException e) {
                                    System.out.println("调用方法权限不足");
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    System.out.println("接口接收参数个数不匹配");
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    System.out.println("调用目标异常");
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            }

            // 找自定义变量注释
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);//允许调用私有变量
                //返回
                if (field.isAnnotationPresent(BackByTag.class)) {
                    //关闭 如果有这个注解，则获取注解类
                    BackByTag back = field.getAnnotation(BackByTag.class);
                    if (back != null && back.tag() == tag) {
                        View viewTemp1 = view.findViewById(back.id());
                        field.set(object, viewTemp1);
                        viewTemp1.setOnClickListener(arg0 -> {
                            if (object instanceof Activity) {
                                ((Activity) object).finish();
                            }
                        });
                    }
                } else if (field.isAnnotationPresent(StartActivityByTag.class)) {
                    // 跳转 如果有这个注解，则获取注解类
                    final StartActivityByTag startActivity = field.getAnnotation(StartActivityByTag.class);
                    if (startActivity != null && startActivity.tag() == tag) {
                        View viewTemp1 = view.findViewById(startActivity.id());
                        field.set(object, viewTemp1);
                        viewTemp1.setOnClickListener(arg0 -> {
                            if (object instanceof Context) {
                                Context context = (Context) object;
                                Intent intent = new Intent(context, startActivity.activity());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });
                    }
                } else if (field.isAnnotationPresent(FindViewByTag.class)) {
                    //找到View 如果有这个注解，则获取注解类
                    FindViewByTag findview = field.getAnnotation(FindViewByTag.class);
                    if (findview != null && findview.tag() == tag) {
                        View viewTemp1 = view.findViewById(findview.id());
                        if (viewTemp1 == null) {
                            Log.e("错误", "设置Findview时没有找到该View");
                        }
                        field.set(object, viewTemp1);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println("调用方法权限不足");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("接口接收参数个数不匹配");
            e.printStackTrace();
        }
    }


    /*___________________________________华丽的分割线,下面是注解______________________________________*/
    // @Target(ElementType.TYPE) //接口、类、枚举、注解
    // @Target(ElementType.FIELD) //字段、枚举的常量
    // @Target(ElementType.METHOD) //方法
    // @Target(ElementType.PARAMETER) //方法参数
    // @Target(ElementType.CONSTRUCTOR) //构造函数
    // @Target(ElementType.LOCAL_VARIABLE)//局部变量
    // @Target(ElementType.ANNOTATION_TYPE)//注解
    // @Target(ElementType.PACKAGE) ///包

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface OnClick {
        int[] value();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface OnClickByTag {
        int[] id();

        int tag();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface FindView {
        int value();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface FindViewByTag {
        int id();

        int tag();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface Back {
        int value();
    }


    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface BackByTag {
        int id();

        int tag();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface StartActivity {
        int value();

        @SuppressWarnings("rawtypes")
        Class activity();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
    public @interface StartActivityByTag {
        int id();

        int tag();

        @SuppressWarnings("rawtypes")
        Class activity();
    }
}
