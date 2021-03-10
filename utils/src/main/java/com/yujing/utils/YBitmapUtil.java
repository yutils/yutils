package com.yujing.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
     * 旋转图片
     *
     * @param bitmap 要旋转的图片
     * @param angle  旋转角度
     * @return bitmap
     */
    public static Bitmap rotate(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
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
     * bitmap  Bitmap.Config.ARGB_8888 转 Bitmap.Config.RGB_565
     *
     * @param bitmap 输入bitmap
     * @return 转换后的bitmap
     */
    public synchronized static Bitmap bitmap888To565(Bitmap bitmap) {
        Bitmap bitmap565 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap565);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return bitmap565;
    }


    /**
     * 获得圆角图片的方法
     *
     * @param bitmap  bitmap
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
     *
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
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
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
                if (color == oldColor) mBitmap.setPixel(j, i, newColor);  //将白色替换成透明色
            }
        }
        return mBitmap;
    }

    //判断是否是空图片
    public static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    /**
     * 截取中心正方形
     *
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     * 需要注的是bitmap参数一定要是从原图得到的，如果是已经经过BitmapFactory inSampleSize压缩过的，可能会不是到正方形。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0)
            return null;
        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                YLog.e("裁剪正方形bitmap异常", e);
                return null;
            }
            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;
            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                YLog.e("裁剪正方形bitmap异常", e);
                return null;
            }
        }
        return result;
    }

    /**
     * 添加文字到图片上（文字水印）
     *
     * @param src      原始图片
     * @param content  文字
     * @param textSize 大小
     * @param color    颜色
     * @param x        横坐标
     * @param y        纵坐标
     * @return 最终添加文字的图片
     */
    public static Bitmap addText(final Bitmap src,
                                 final String content,
                                 final int textSize,
                                 @ColorInt final int color,
                                 final float x,
                                 final float y) {
        return addText(src, content, textSize, color, x, y, false);
    }

    /**
     * 添加文字到图片上（文字水印）
     *
     * @param src      原始图片
     * @param content  文字
     * @param textSize 大小
     * @param color    颜色
     * @param x        横坐标
     * @param y        纵坐标
     * @param recycle  最后是否释放图片
     * @return 最终添加文字的图片
     */
    public static Bitmap addText(final Bitmap src,
                                 final String content,
                                 final float textSize,
                                 @ColorInt final int color,
                                 final float x,
                                 final float y,
                                 final boolean recycle) {
        if (isEmptyBitmap(src) || content == null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 添加图片到图片上（图片水印）
     *
     * @param src       原始图片
     * @param watermark 水印图片
     * @param x         横坐标
     * @param y         纵坐标
     * @param alpha     透明度【0..255】
     * @return 最终添加图片的图片
     */
    public static Bitmap addImage(final Bitmap src,
                                  final Bitmap watermark,
                                  final int x, final int y,
                                  final int alpha) {
        return addImage(src, watermark, x, y, alpha, false);
    }

    /**
     * 添加图片到图片上（图片水印）
     *
     * @param src       原始图片
     * @param watermark 水印图片
     * @param x         横坐标
     * @param y         纵坐标
     * @param alpha     透明度【0..255】
     * @param recycle   最后是否释放原图片
     * @return 最终添加图片的图片
     */
    public static Bitmap addImage(final Bitmap src,
                                  final Bitmap watermark,
                                  final int x,
                                  final int y,
                                  final int alpha,
                                  final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(ret);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 返回带透明度的图片
     *
     * @param src 原始图片
     * @return 带透明度的图片
     */
    public static Bitmap toAlpha(final Bitmap src) {
        return toAlpha(src, false);
    }

    /**
     * 返回带透明度的图片
     *
     * @param src     原始图片
     * @param recycle 最后是否释放原图片
     * @return 带透明度的图片
     */
    public static Bitmap toAlpha(final Bitmap src, final Boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = src.extractAlpha();
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 返回黑白图片，灰阶度图片
     *
     * @param src 原始图片
     * @return 灰阶度图片
     */
    public static Bitmap toGray(final Bitmap src) {
        return toGray(src, false);
    }

    /**
     * Return the gray bitmap.
     *
     * @param src     原始图片
     * @param recycle 最后是否释放原图片
     * @return 灰阶度图片
     */
    public static Bitmap toGray(final Bitmap src, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(src, 0, 0, paint);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 判断是否是黑白图片（灰阶度图片），需要格式为 Bitmap.Config.ARGB_8888
     *
     * @param bitmap 原始图片
     * @return 是否是灰阶度图片
     */
    public static boolean isGray(Bitmap bitmap) {
        if (isEmptyBitmap(bitmap)) return false;
        //取三点，都是黑白就是黑白
        // 中心原点
        int centX = bitmap.getWidth() / 2;
        int centY = bitmap.getHeight() / 2;
        int px1 = bitmap.getPixel(centX, centY);
        //4象限中心点
        int px2 = bitmap.getPixel(centX + (bitmap.getWidth() - centX) / 2, centY + (bitmap.getHeight() - centY) / 2);
        //2象限中心点
        int px3 = bitmap.getPixel(centX - (bitmap.getWidth() - centX) / 2, centY - (bitmap.getHeight() - centY) / 2);
        return ((Color.red(px1) == Color.green(px1) && Color.red(px1) == Color.blue(px1))
                && (Color.red(px2) == Color.green(px2) && Color.red(px2) == Color.blue(px2))
                && (Color.red(px3) == Color.green(px3) && Color.red(px3) == Color.blue(px3)));
    }
}
