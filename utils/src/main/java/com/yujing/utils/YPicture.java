package com.yujing.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 拍照，剪切，相册选择照片
 *
 * @author yujing 2018年12月10日12:05:17
 * @version 1.1
 */
@SuppressWarnings("unused")
public class YPicture {
    private Activity activity;

    public static final int REQUEST_CODE_CAMERA = 16000;
    public static final int REQUEST_CODE_ALBUM = 17000;
    public static final int REQUEST_CODE_CROP = 18000;

    private File cameraFile = null;//拍照的文件储存路径
    private File cropFile = null;//剪切的文件储存路径

    private Uri cameraUri;//拍照URI
    private Uri cropUri;//剪切URI

    private PictureFromCameraListener pictureFromCameraListener;
    private PictureFromCropListener pictureFromCropListener;
    private PictureFromAlbumListener PictureFromAlbumListener;

    private Object flagCamera;
    private Object flagCrop;
    private Object flagAlbum;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    /**
     *  打开相机
     * @param activity 页面
     */
    public void gotoCamera(Activity activity) {
        this.activity = activity;
        try {
            String str = dateFormat.format(new Date(System.currentTimeMillis()));
            //路径默认，若修改则不能保存照片
            cameraFile = createImageFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Y" + File.separator + "CAMERA" + File.separator + "J_" + str + ".jpg");
            cameraUri = createImageUri(activity, cameraFile);
            //跳转到照相机拍照
            Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            it.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            activity.startActivityForResult(it, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            Toast.makeText(activity, "请开启摄像权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开相机
     * @param activity activity
     * @param flag 标记
     */
    public void gotoCamera(Activity activity, Object flag) {
        flagCamera = flag;
        gotoCamera(activity);
    }

    /**
     * 打开默认相册
     * @param activity activity
     */
    public void gotoAlbumDefault(Activity activity) {
        this.activity = activity;
        Intent it = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(it, REQUEST_CODE_ALBUM);
    }

    /**
     * 打开默认相册
     * @param activity activity
     * @param flag 标记
     */
    public void gotoAlbumDefault(Activity activity, Object flag) {
        flagAlbum = flag;
        gotoAlbumDefault(activity);
    }

    /**
     * 打开相册，获取图片，支持的软件皆可
     * @param activity activity
     */
    public void gotoAlbum(Activity activity) {
        this.activity = activity;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * 打开相册，获取图片，支持的软件皆可
     * @param activity activity
     * @param flag 标记
     */
    public void gotoAlbum(Activity activity, Object flag) {
        flagAlbum = flag;
        gotoAlbum(activity);
    }

    /**
     * 剪切图片
     * @param activity activity
     * @param uri 图片uri
     * @param outputX 宽度
     * @param outputY 高度
     */
    public void gotoCrop(Activity activity, Uri uri, int outputX, int outputY) {
        this.activity = activity;
        String str = dateFormat.format(new Date(System.currentTimeMillis()));
        cropFile = createImageFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Y" + File.separator + "CROP" + File.separator + "J_" + str + ".jpg");
        cropUri = createImageUri(activity, cropFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 宽高和比例都不设置时,裁剪框比例和大小都可以随意调整
        if (outputX > 0 && outputY > 0) {
            // aspectX aspectY 是裁剪框宽高的比例
            intent.putExtra("aspectX", outputX);
            intent.putExtra("aspectY", outputY);
            // outputX outputY 是裁剪后生成图片的宽高
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        activity.startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    /**
     * 剪切图片
     * @param activity activity
     * @param uri 图片uri
     * @param outputX 宽度
     * @param outputY 高度
     * @param flag 标记
     */
    public void gotoCrop(Activity activity, Uri uri, int outputX, int outputY, Object flag) {
        flagCrop = flag;
        gotoCrop(activity, uri, outputX, outputY);
    }

    /**
     * 获取Activity返回信息
     * @param requestCode 请求code
     * @param resultCode 响应code
     * @param data 数据
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA && pictureFromCameraListener != null) {
                pictureFromCameraListener.result(cameraUri, cameraFile, flagCamera);
            } else if (requestCode == REQUEST_CODE_CROP && pictureFromCropListener != null) {
                pictureFromCropListener.result(cropUri, cropFile, flagCrop);
            } else if (requestCode == REQUEST_CODE_ALBUM && PictureFromAlbumListener != null) {
                Uri uri = data.getData();
                File file = new File(uri2ImagePath(activity, uri));
                PictureFromAlbumListener.result(uri, file, flagAlbum);
            }
        }
    }

    /**
     * 根据URI获图片路径
     * @param context context
     * @param uri 图片URI
     * @return String路径
     */
    public String uri2ImagePath(Context context, Uri uri) {
        String mImgPath;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mImgPath = picturePath;
        } else {
            mImgPath = uri.getPath();
        }
        return mImgPath;
    }

    /**
     * 根据图片路径获取URI
     * @param context context
     * @param imageFile 文件
     * @return URI图片
     */
    public Uri imageFile2Uri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Uri uri = null;
        try (Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? ", new String[]{filePath}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int values1 = cursor.getInt(cursor.getColumnIndex("_id"));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + values1);
            } else if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put("_data", filePath);
                uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        } catch (Exception e) {
            return null;
        }
        return uri;
    }

    /**
     * 创建图片File
     * @param path 路径
     * @return File文件
     */
    public File createImageFile(String path) {
        // 判断是否有SD卡
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.e("警告", "SD卡不存在！！");
        }
        File file = new File(path);
        if (!Objects.requireNonNull(file.getParentFile()).exists()) {//如果文件夹不存在就创建
            if (file.getParentFile().mkdirs()) {
                Log.i("ImageUtil.path2Uri", "创建图片文件夹成功");
            } else {
                Log.e("ImageUtil.path2Uri", "创建图片文件夹失败");
            }
        }
        if (file.exists()) {
            if (file.delete()) {
                Log.i("ImageUtil.path2Uri", "删除源文件成功");
            } else {
                Log.e("ImageUtil.path2Uri", "删除源文件失败");
            }
        }
        return file;
    }

    /**
     * 创建适用于图片的Uri
     * @param activity activity
     * @param file file要存放的路径
     * @return URI图片
     */
    private Uri createImageUri(Activity activity, File file) {
        Uri uri;
        if (android.os.Build.VERSION.SDK_INT < 24) {
            uri = Uri.fromFile(file);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        return uri;
    }


    public interface PictureListener {
        void result(Uri uri, File file, Object Flag);
    }

    public interface PictureFromCameraListener extends PictureListener {
    }

    public interface PictureFromCropListener extends PictureListener {
    }

    public interface PictureFromAlbumListener extends PictureListener {
    }

    public void setPictureFromCameraListener(PictureFromCameraListener pictureFromCameraListener) {
        this.pictureFromCameraListener = pictureFromCameraListener;
    }

    public void setPictureFromCropListener(PictureFromCropListener pictureFromCropListener) {
        this.pictureFromCropListener = pictureFromCropListener;
    }

    public void setPictureFromAlbumListener(YPicture.PictureFromAlbumListener pictureFromAlbumListener) {
        PictureFromAlbumListener = pictureFromAlbumListener;
    }
}








