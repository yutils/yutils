package com.yujing.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;

import com.yujing.utils.YLog;

/**
 * 人脸识别类
 *
 * @author yujing 2021年8月19日10:09:05
 */
public class YFace {
    /**
     * 找bitmap是否包含人脸
     *
     * @param bitmap bitmap
     * @return boolean
     */
    public static boolean findFace(Bitmap bitmap) {
        return findFaceRectF(bitmap) != null;
    }

    /**
     * 找bitmap中的人脸，并且返回最像脸的坐标
     *
     * @param bitmap bitmap
     * @return RectF
     * RectF rf = YFace.findFaceRectF(bitmap);
     * if (rf != null){  };
     */
    public static RectF findFaceRectF(Bitmap bitmap) {
        RectF[] rfs = findFaceRectFs(bitmap);
        if (rfs == null) return null;
        return rfs[0];
    }

    /**
     * 找bitmap中的人脸列表，并且返回全部人脸的坐标
     *
     * @param bitmap bitmap
     * @return RectF[]
     * RectF[] rfs = YFace.findFaceRectFs(bitmap);
     * if (rfs != null) {  };
     */
    public static RectF[] findFaceRectFs(Bitmap bitmap) {
        return findFaceRectFs(bitmap, 10);
    }

    /**
     * 找bitmap中的人脸列表，并且返回全部人脸的坐标
     *
     * @param bitmap   bitmap
     * @param maxFaces 最多几张脸
     * @return RectF[]
     * RectF[] rfs = YFace.findFaceRectFs(bitmap);
     * if (rfs != null) {  };
     */
    public static RectF[] findFaceRectFs(Bitmap bitmap, int maxFaces) {
        if (bitmap.isRecycled()) return null;
        RectF[] rs = null;
        try {
            //Bitmap必须是Bitmap.Config.RGB_565格式的bitmap
            if (!bitmap.getConfig().equals(Bitmap.Config.RGB_565))
                bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
            FaceDetector faceDetector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), maxFaces);
            FaceDetector.Face[] face = new FaceDetector.Face[maxFaces];
            int faces = faceDetector.findFaces(bitmap, face);
            if (faces > 0) {
                rs = new RectF[faces];
                for (int i = 0; i < faces; i++) {
                    //找到脸了
                    FaceDetector.Face item = face[i];
                    PointF pf = new PointF();
                    item.getMidPoint(pf);
                    RectF r = new RectF();
                    //两眼之间距离item.eyesDistance()
                    r.left = pf.x - item.eyesDistance() * 1.2f;
                    r.right = pf.x + item.eyesDistance() * 1.2f;
                    r.top = pf.y - item.eyesDistance() * 1.2f;
                    r.bottom = pf.y + item.eyesDistance() * 1.2f;
                    rs[i] = r;
                }
            }
        } catch (Throwable t) {
            YLog.e("找bitmap中的人脸时发生异常", t);
        }
        return rs;
    }

    /**
     * 画框，画人脸信息
     *
     * @param startX 开始X
     * @param startY 开始Y
     * @param endX   结束X
     * @param endY   结束Y
     * @param width  图片宽度
     * @param height 图片高度
     * @return Bitmap
     * val r = YFace.findFaceRectF(bitmap)
     * if (r != null) bitmap=YFace.drawFaceInfo(r.left, r.top, r.right, r.bottom, PREVIEW_WIDTH, PREVIEW_HEIGHT)
     */
    public static Bitmap drawFaceInfo(float startX, float startY, float endX, float endY, int width, int height) {
        return drawFaceInfo(startX, startY, endX, endY, Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
    }

    /**
     * 画框，画人脸信息
     *
     * @param startX 开始X
     * @param startY 开始Y
     * @param endX   结束X
     * @param endY   结束Y
     * @param bitmap 绘制的图片
     * @return Bitmap
     * val r = YFace.findFaceRectF(bitmap)
     * if (r != null) YFace.drawFaceInfo(r.left, r.top, r.right, r.bottom, bitmap)
     */
    public static Bitmap drawFaceInfo(float startX, float startY, float endX, float endY, Bitmap bitmap) {
        return drawFaceInfo(new RectF(startX, startY, endX, endY), bitmap);
    }

    /**
     * 画框，画人脸信息
     *
     * @param rf     坐标
     * @param bitmap 绘制的图片
     * @return Bitmap
     * val rf = YFace.findFaceRectF(bitmap)
     * if (rf != null) YFace.drawFaceInfo(rf, bitmap)
     */
    public static Bitmap drawFaceInfo(RectF rf, Bitmap bitmap) {
        return drawFaceInfos(new RectF[]{rf}, bitmap);
    }

    /**
     * 画框，画人脸信息
     *
     * @param rfs    所有人脸坐标
     * @param bitmap 绘制的图片
     * @return Bitmap
     * val rfs = YFace.findFaceRectFs(bitmap)
     * if (rfs != null) YFace.drawFaceInfos(rfs, bitmap)
     */
    public static Bitmap drawFaceInfos(RectF[] rfs, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);  //创建画布
        for (int i = 0; i < rfs.length; i++) {
            RectF rf = rfs[i];
            float startX = rf.left, startY = rf.top, endX = rf.right, endY = rf.bottom;
            int strokeWidth = 2;//线宽。单位为像素
            //控制线长短,如果起点X-终点X大于200就是50，否则就是起点到终点的1/4长度
            int lineLengthX = ((endX - startX) > 200) ? 50 : (int) ((endX - startX) * 0.25f);//x轴长度
            int lineLengthY = ((endY - startY) > 200) ? 50 : (int) ((endY - startY) * 0.25f);//y轴长度

            Paint paint = new Paint();  //画笔
            paint.setStrokeWidth(strokeWidth);  //设置线宽。单位为像素
            paint.setAntiAlias(true); //抗锯齿
            paint.setColor(Color.RED);  //画笔颜色

            //画框左上角
            canvas.drawLine(startX - strokeWidth / 2f, startY, startX + lineLengthX, startY, paint);
            canvas.drawLine(startX, startY - strokeWidth / 2f, startX, startY + lineLengthY, paint);
            //画框左下角
            canvas.drawLine(startX - strokeWidth / 2f, endY, startX + lineLengthX, endY, paint);
            canvas.drawLine(startX, endY + strokeWidth / 2f, startX, endY - lineLengthY, paint);
            //画框右上角
            canvas.drawLine(endX + strokeWidth / 2f, startY, endX - lineLengthX, startY, paint);
            canvas.drawLine(endX, startY - strokeWidth / 2f, endX, startY + lineLengthY, paint);
            //画框右下角
            canvas.drawLine(endX + strokeWidth / 2f, endY, endX - lineLengthX, endY, paint);
            canvas.drawLine(endX, endY + strokeWidth / 2f, endX, endY - lineLengthY, paint);
        }
        return bitmap;
    }
}