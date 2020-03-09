package com.yujing.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import androidx.annotation.ColorInt;

import java.io.ByteArrayOutputStream;

/**
 * 对bitmap的一些处理方法
 *
 * @author yujing 2019年4月2日10:32:37
 */
@SuppressWarnings("unused")
public class YBitmapUtil {

    /**
     * 放大缩小图片
     *
     * @param bitmap 需要处理的图片
     * @param w      输出宽度
     * @param h      输出的高度
     * @return 新的图片
     */
    public static Bitmap zoom(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
    }

    /**
     * 图片压缩返回byte[]
     *
     * @param image Bitmap image
     * @param Kb    大小kb
     * @return byte[]
     */
    public synchronized static byte[] compressToBytes(Bitmap image, int Kb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;//质量
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (options >= 10 && baos.toByteArray().length > 1024 * Kb) { // 循环判断如果压缩后图片是否大于Kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        Log.d("图片压缩后大小", baos.toByteArray().length / 1024d + "KB  options:" + options);
        return baos.toByteArray();
    }


    /**
     * 获得圆角图片的方法
     * @param bitmap bitmap
     * @param roundPx 圆弧的的像素量（从开始变幻曲线到顶点的距离）
     * @return Bitmap
     */
    public static Bitmap getRounded(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获得带倒影的图片方法
     * @param bitmap bitmap
     * @return bitmap
     */
    public static Bitmap getReflection(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * 替换eBitmap中某颜色值
     *
     * @param oldBitmap 需要替换颜色的图片
     * @param oldColor  旧的颜色ARGB
     * @param newColor  新的颜色ARGB
     * @return 新的图片
     */
    public static Bitmap replaceColor(Bitmap oldBitmap, @ColorInt int oldColor, @ColorInt int newColor) {
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        //循环获得bitmap所有像素点
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        int mArrayColorLength = mBitmapWidth * mBitmapHeight;
        int[] mArrayColor = new int[mArrayColorLength];
        int count = 0;
        for (int i = 0; i < mBitmapHeight; i++) {
            for (int j = 0; j < mBitmapWidth; j++) {
                //获得Bitmap 图片中每一个点的color颜色值
                //在这说明一下 如果color 是全透明或者全黑返回值为0，全透明是0x00000000
                int color = mBitmap.getPixel(j, i);
                //将颜色值存在一个数组中 方便后面修改
                if (color == oldColor) {
                    mBitmap.setPixel(j, i, newColor);  //将白色替换成透明色
                }
            }
        }
        return mBitmap;
    }
}
