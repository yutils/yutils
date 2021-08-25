package com.yujing.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;

/**
 * 人脸识别类
 * @author yujing 2021年8月19日10:09:05
 */
public class YFace {
    /**
     * 找bitmap中的人脸，并且返回最像脸的坐标
     *
     * @param bitmap bitmap
     * @return RectF
     */
    public static RectF findFaceRectF(Bitmap bitmap) {
        //Bitmap必须是Bitmap.Config.RGB_565格式的bitmap
        if (!bitmap.getConfig().equals(Bitmap.Config.RGB_565))
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        FaceDetector faceDetector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 1);
        FaceDetector.Face[] face = new FaceDetector.Face[1];
        int faces = faceDetector.findFaces(bitmap, face);
        RectF r = null;
        if (faces > 0) {
            //找到脸了
            FaceDetector.Face item = face[0];
            PointF pf = new PointF();
            item.getMidPoint(pf);
            r = new RectF();
            //两眼之间距离item.eyesDistance()
            r.left = pf.x - item.eyesDistance() * 1.2f;
            r.right = pf.x + item.eyesDistance() * 1.2f;
            r.top = pf.y - item.eyesDistance() * 1.2f;
            r.bottom = pf.y + item.eyesDistance() * 1.2f;
        }
        return r;
    }

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
     * 画框，画人脸信息
     *
     * @param startX 开始X
     * @param startY 开始Y
     * @param endX   结束X
     * @param endY   结束Y
     * @param width  图片宽度
     * @param height 图片高度
     * @return Bitmap
     */
    public synchronized static Bitmap drawFaceInfo(float startX, float startY, float endX, float endY, int width, int height) {
        int strokeWidth = 2;//线宽。单位为像素
        //控制线长短,如果起点X-终点X大于200就是50，否则就是起点到终点的1/4长度
        int lineLengthX = ((endX - startX) > 200) ? 50 : (int) ((endX - startX) * 0.25f);//x轴长度
        int lineLengthY = ((endY - startY) > 200) ? 50 : (int) ((endY - startY) * 0.25f);//y轴长度

        Paint paint = new Paint();  //画笔
        paint.setStrokeWidth(strokeWidth);  //设置线宽。单位为像素
        paint.setAntiAlias(true); //抗锯齿
        paint.setColor(Color.RED);  //画笔颜色

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);  //创建画布
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
        return bitmap;
    }

}
