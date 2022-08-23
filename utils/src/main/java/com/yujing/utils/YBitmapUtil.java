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

import androidx.annotation.ColorInt;

import java.io.ByteArrayOutputStream;

/**
 * 对bitmap的一些处理方法
 *
 * @author 余静 2019年4月2日10:32:37
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
     * 图片压缩返回byte[]，不一定绝对小于对应大小
     *
     * @param image Bitmap image
     * @param Kb    大小kb
     * @return byte[]
     */
    public static byte[] compressToBytes(Bitmap image, int Kb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        YLog.d("图片压缩", "图片原始大小:" + baos.toByteArray().length / 1024d + "KB");
        if (baos.toByteArray().length < 1024 * Kb) return baos.toByteArray();
        //开始质量减少一点点，体积会减少很多，后面减少影响不大。质量低于10，就可能压缩成黑白照片。
        int[] qualityList = new int[]{100, 95, 88, 80, 70, 58, 44, 28, 10};
        for (int i = 0; i < qualityList.length && baos.toByteArray().length > 1024 * Kb; i++) {
            int quality = qualityList[i];
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            YLog.d("图片压缩", "图片压缩后大小:" + baos.toByteArray().length / 1024d + "KB  质量:" + quality);
        }
        return baos.toByteArray();
    }

    /**
     * bitmap  Bitmap.Config.ARGB_8888 转 Bitmap.Config.RGB_565
     *
     * @param bitmap 输入bitmap
     * @return 转换后的bitmap
     */
    public static Bitmap bitmap888To565(Bitmap bitmap) {
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
        return addText(src, content, textSize, color, x, y, Paint.Align.LEFT, false);
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
     * @param align    字体对齐方式，如：Paint.Align.CENTER
     * @return 最终添加文字的图片
     */
    public static Bitmap addText(final Bitmap src,
                                 final String content,
                                 final int textSize,
                                 @ColorInt final int color,
                                 final float x,
                                 final float y,
                                 final Paint.Align align) {
        return addText(src, content, textSize, color, x, y, align, false);
    }

    /**
     * 添加文字到图片上（文字水印）
     * 举例：
     * Bitmap bitmap = YBitmapUtil.addText(backgroundBitmap, "内容", 50, Color.WHITE, backgroundBitmap.getWidth()/2F, backgroundBitmap.getHeight()/2F,Paint.Align.CENTER, true);
     *
     * @param src      原始图片
     * @param content  文字
     * @param textSize 大小，像素
     * @param color    颜色
     * @param x        横坐标
     * @param y        纵坐标
     * @param align    字体对齐方式，如：Paint.Align.CENTER
     * @param recycle  最后是否释放图片
     * @return 最终添加文字的图片
     */
    public static Bitmap addText(final Bitmap src,
                                 final String content,
                                 final float textSize,
                                 @ColorInt final int color,
                                 final float x,
                                 final float y,
                                 final Paint.Align align,
                                 final boolean recycle) {
        if (isEmptyBitmap(src) || content == null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setTextAlign(align);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 添加图片到图片上（图片水印）
     *
     * @param background 背景图片
     * @param watermark  水印图片
     * @param x          横坐标
     * @param y          纵坐标
     * @param alpha      透明度【0..255】，255为不透明
     * @return 最终添加图片的图片
     */
    public static Bitmap addImage(final Bitmap background,
                                  final Bitmap watermark,
                                  final int x, final int y,
                                  final int alpha) {
        return addImage(background, watermark, x, y, alpha, false);
    }

    /**
     * 添加图片到图片上（图片水印）
     * 举例：
     * Bitmap bitmap = YBitmapUtil.addImage(backgroundBitmap, qrBitmap, 176, 481, 255,true);
     *
     * @param background 背景图片
     * @param watermark  水印图片
     * @param x          横坐标
     * @param y          纵坐标
     * @param alpha      透明度【0..255】，255为不透明
     * @param recycle    最后是否释放原图片
     * @return 最终添加图片的图片
     */
    public static Bitmap addImage(final Bitmap background,
                                  final Bitmap watermark,
                                  final int x,
                                  final int y,
                                  final int alpha,
                                  final boolean recycle) {
        if (isEmptyBitmap(background)) return null;
        Bitmap ret = background.copy(background.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setAlpha(alpha);
            Canvas canvas = new Canvas(ret);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !background.isRecycled() && ret != background) background.recycle();
        return ret;
    }

    /**
     * 添加图片到图片上（图片水印）
     * 举例：
     * Matrix matrix = new Matrix();
     * matrix.postScale(0.5F, 0.5F); //缩放
     * matrix.postTranslate(x,y); //平移
     * Bitmap bitmap = YBitmapUtil.addImage(backgroundBitmap, qrBitmap, matrix, 255,true);
     *
     * @param background 背景图片
     * @param watermark  水印图片
     * @param matrix     矩阵控制
     * @param alpha      透明度【0..255】，255为不透明
     * @param recycle    最后是否释放原图片
     * @return 最终添加图片的图片
     */
    public static Bitmap addImage(final Bitmap background,
                                  final Bitmap watermark,
                                  final int alpha,
                                  final Matrix matrix,
                                  final boolean recycle) {
        if (isEmptyBitmap(background)) return null;
        Bitmap ret = background.copy(background.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setAlpha(alpha);
            Canvas canvas = new Canvas(ret);
            canvas.drawBitmap(watermark, matrix, paint);
        }
        if (recycle && !background.isRecycled() && ret != background) background.recycle();
        return ret;
    }

    /**
     * 向图片中心添加logo图片 (推荐)
     * 举例：
     * Bitmap bitmap = YBitmapUtil.addLogo(backgroundBitmap, qrBitmap, 0.5F, 0, 0, 255);
     *
     * @param background 背景图片
     * @param logoBitmap logo图片
     * @param percent    比例,0.0-1.0F,小于0或大于1，默认0.2
     * @param deviationX 偏移X
     * @param deviationY 偏移Y
     * @param alpha      透明度【0..255】，255为不透明
     * @return 添加logo的图片
     */
    private static Bitmap addImage(Bitmap background, Bitmap logoBitmap, float percent, float deviationX, float deviationY, int alpha) {
        if (background == null) return null;
        if (logoBitmap == null) return background;

        int srcWidth = background.getWidth();
        int srcHeight = background.getHeight();
        int logoWidth = logoBitmap.getWidth();
        int logoHeight = logoBitmap.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(background, 0, 0, null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(alpha);
        //传值不合法时使用0.2F
        if (percent < 0F || percent > 1F) percent = 0.2F;
        canvas.scale(percent, percent, srcWidth / 2F, srcHeight / 2F);
        canvas.drawBitmap(logoBitmap, srcWidth / 2F - logoWidth / 2F + deviationX, srcHeight / 2F - logoHeight / 2F + deviationY, paint);
        return bitmap;
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

    /**
     * bitmap转点阵图
     * 黑色为true，白色为false
     *
     * @param bitmap bitmap
     * @return boolean二维数组，[列][行]
     */
    public static boolean[][] bitmapToDot(Bitmap bitmap) {
        return bitmapToDot(bitmap, 200);
    }

    /**
     * bitmap转点阵图
     * 黑色为true，白色为false
     *
     * @param bitmap    bitmap
     * @param threshold 阈值，低于这个值就默认是黑色，0-255
     * @return boolean二维数组，[列][行]
     */
    public static boolean[][] bitmapToDot(Bitmap bitmap, int threshold) {
        if (YBitmapUtil.isEmptyBitmap(bitmap)) return null;
        bitmap = YBitmapUtil.toGray(bitmap);
        //转boolean 黑true
        boolean[][] blacks = new boolean[bitmap.getHeight()][bitmap.getWidth()];
        for (int y = 0; y < blacks.length; y++) {
            for (int x = 0; x < blacks[y].length; x++) {
                int px = bitmap.getPixel(x, y);
                blacks[y][x] = Color.red(px) <= threshold;//小于阈值就为黑色
            }
        }
        return blacks;
    }
}
