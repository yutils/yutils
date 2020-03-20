package com.yujing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * 各种类型转换
 * @author yujing 2019年12月12日14:09:50
 */
@SuppressWarnings("unused")
public class YConvert {
    /**
     * bytesToHexString
     *
     * @param bArray bArray
     * @return String
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase(Locale.US));
        }
        return sb.toString();
    }

    /**
     * hexStringToByte
     *
     * @param hex hexString
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
        if (hex != null) {
            hex = hex.toUpperCase(Locale.US);
        } else {
            return new byte[0];
        }
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] aChar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(aChar[pos]) << 4 | toByte(aChar[pos + 1]));
        }
        return result;
    }

    /**
     * 提供给上面方法调用
     *
     * @param c char
     * @return byte
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * asciiToString
     *
     * @param value asciiString
     * @return String
     */
    public static String asciiToString(String value) {
        StringBuilder sbu = new StringBuilder();
        String[] chars = value.split(",");
        for (String aChar : chars) {
            sbu.append((char) Integer.parseInt(aChar));
        }
        return sbu.toString();
    }

    /**
     * stringToAscii
     *
     * @param value String
     * @return asciiString
     */
    public static String stringToAscii(String value) {
        StringBuilder sbu = new StringBuilder();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    /**
     * 半角转全角
     *
     * @param input String
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 将list转化为数组
     *
     * @param list list
     * @param <T>  泛类
     * @return 泛类数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] List2Array(List<T> list) {
        if (list == null) return null;
        Object[] result = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return (T[]) result;
    }

    /**
     * uri2FilePath
     *
     * @param context context
     * @param uri     转换成文件路径
     * @return String
     */
    public static String uri2FilePath(final Context context, Uri uri) {
        return YUri.getPath(context,uri);
    }

    /**
     * uri2FilePath
     *
     * @param context context
     * @param uri     转换成文件路径
     * @return String
     */
    public static String uri2FilePathForN(final Context context, Uri uri) {
        return YUri.getPathForN(context,uri);
    }

    /**
     * file转Uri
     *
     * @param context context
     * @param file    file
     * @return Uri
     */
    public static Uri file2Uri(Context context, File file) {
        return YUri.getUri(context,file);
    }

    /**
     * 保存文件返回uri
     *
     * @param path    保存的路径
     * @param bitmap 保存的文件
     * @return Uri
     */
    public synchronized static Uri saveBitmap2uri(String path, Bitmap bitmap) {
        return YUri.saveBitmap2uri(path,bitmap);
    }

    /**
     * uri转换成Bitmap
     *
     * @param context context
     * @param uri     uri
     * @return Bitmap
     */
    public synchronized static Bitmap uri2Bitmap(Context context, Uri uri) {
        return YUri.getBitmap(context,uri);
    }


    /**
     * path2Bitmap
     *
     * @param path 文件路径
     * @return Bitmap
     */
    public synchronized static Bitmap path2Bitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回 bm 为空
        options.inJustDecodeBounds = false; // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = (int) (options.outHeight / (float) 320);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; // 重新读入图片，注意此时已经把 options.inJustDecodeBounds，inSampleSize可以使BitmapFactory分配更少的空间以消除该错误。
        // 设回 false 了
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 资源文件转换成Bitmap
     *
     * @param context   context
     * @param Resources Resources
     * @return Bitmap
     */
    public synchronized static Bitmap resources2Bitmap(Context context,
                                                       int Resources) {
        android.content.res.Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, Resources);
    }

    /**
     * bitmap转换成byte数组
     *
     * @param bm BM
     * @return byte[]
     */
    public synchronized static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * bytes数组转换成Bitmap
     *
     * @param b byte[]
     * @return Bitmap
     */
    public synchronized static Bitmap bytes2Bitmap(byte[] b) {
        if (b != null && b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 将Base64字符串转换成Bitmap类型
     *
     * @param string string
     * @return Bitmap
     */
    public synchronized static Bitmap string2Bitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将Bitmap转换成Base64字符串
     *
     * @param bitmap bitmap
     * @return String
     */
    public synchronized static String bitmap2String(Bitmap bitmap) {
        String string;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;

    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable drawable
     * @return Bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * yuv420格式图片数据转成bitmap
     *
     * @param data   数据
     * @param width  宽
     * @param height 高
     * @return Bitmap
     */
    public static Bitmap yuv420spToBitmap(byte[] data, int width, int height) {
        YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
        Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 文件转换成byte数组
     *
     * @param f 目标文件
     * @return byte[]
     */
    public static byte[] fileToByte(File f) {
        return YFileUtil.fileToByte(f);
    }

    /**
     * bytes转换成文件
     *
     * @param b        目标数组
     * @param filePath 路径
     * @return File
     */
    public static File bytes2Files(byte[] b, String filePath) {
        File file = new File(filePath);
        boolean success = YFileUtil.byteToFile(b, file);
        return success ? file : null;
    }

    /**
     * 把Base64的字符串转换成Object
     *
     * @param Base64String Base64String
     * @return Object
     */
    public synchronized static Object base642Object(String Base64String) {
        if (Base64String == null)
            return null;
        return bytes2Object(Base64.decode(Base64String, Base64.DEFAULT));
    }

    /**
     * 把Object转换成Base64的字符串
     *
     * @param object object
     * @param <T>    Serializable
     * @return String
     */
    public synchronized static <T extends Serializable> String object2Base64(T object) {
        if (object == null)
            return null;
        return new String(Base64.encode(object2Bytes(object), Base64.DEFAULT));
    }

    /**
     * 把bytes数组换成Object
     *
     * @param bytes bytes
     * @return Object
     */
    public synchronized static Object bytes2Object(byte[] bytes) {
        if (bytes == null)
            return null;
        Object object;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(byteIn);
            object = in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("对象转换失败：", "Base64转对象时候发生错误,确保包名是否一致，ClassNotFoundException：" + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("对象转换失败：", "Base64转对象时候发生错误IOException：" + e.getMessage());
            return null;
        }
        return object;
    }

    /**
     * 把Object转换成bytes数组
     *
     * @param object object
     * @param <T>    Serializable
     * @return byte[]
     */
    public synchronized static <T extends Serializable> byte[] object2Bytes(T object) {
        if (object == null)
            return null;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(object);
        } catch (IOException e) {
            Log.e("对象转换失败", "错误:请检查" + object.getClass().getName() + "类是否序列化，如果没有请实现Serializable接口：" + e.getMessage());
            return null;
        }
        return byteOut.toByteArray();
    }

    /**
     * View转bitmap
     *
     * @param v View
     * @return Bitmap
     */
    public static Bitmap view2Bitmap(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        v.draw(c);
        return b;
    }

    /**
     * bytes转InputStream
     *
     * @param bytes byte[]
     * @return InputStream
     */
    public static InputStream bytes2InputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    /**
     * InputStream转bytes
     *
     * @param inputStream inputStream
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] inputStream2Bytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            bs.write(buffer, 0, len);
        }
        bs.flush();
        return bs.toByteArray();
    }

    /**
     * inputStream转String
     *
     * @param inputStream inputStream
     * @return String
     * @throws IOException IOException
     */
    public static String inputStream2String(InputStream inputStream) throws IOException {
        return new String(inputStream2Bytes(inputStream), StandardCharsets.UTF_8);
    }


    /**
     * string 转换BCD嘛
     *
     * @param asc 目标
     * @return bcd
     */
    public static byte[] string2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte[] abt;
        if (len >= 2) {
            len = len / 2;
        }

        byte[] bbt = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * BCD转String 大于9的bcd用*表示
     *
     * @param bytes 目标
     * @return string
     */
    public static String bcd2String(byte[] bytes) {
        return bcd2String(bytes, "*");
    }

    /**
     * BCD转String 大于9的bcd用error表示
     *
     * @param bytes 目标
     * @param error 错误的符号
     * @return String
     */
    public static String bcd2String(byte[] bytes, String error) {
        StringBuilder temp = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            int bcd1 = ((bytes[i] & 0xf0) >>> 4);
            int bcd2 = (bytes[i] & 0x0f);
            temp.append(bcd1 > 9 ? error : bcd1);
            temp.append(bcd2 > 9 ? error : bcd2);
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
    }
}
