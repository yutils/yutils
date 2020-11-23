package com.yujing.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 相机类
 *
 * @author yujing 2020年11月23日16:22:38
 */
/*
用法：
    YCamera yCamera1= new YCamera(this, textureView1, 800, 600, CameraCharacteristics.LENS_FACING_FRONT);
    yCamera1.setScreenStopTimeLimit(10);

    //错误回调
    yCamera1.setErrorListener(msg -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());

    yCamera1.setCaptureListener(bitmap ->
          //拍照回调
    );

    yCamera1.setFrameListener(bitmap ->
           //每帧回调
    );

    findViewById(R.id.takePicture).setOnClickListener(v -> {
        yCamera1.capture(90);//拍照
    });
}

@Override
protected void onPause() {
    yCamera1.onPause();
    super.onPause();
}

@Override
protected void onResume() {
    yCamera1.onResume();
    super.onResume();
}

@Override
protected void onDestroy() {
    yCamera1.onDestroy();
    super.onDestroy();
}
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class YCamera {
    private static final String TAG = "YCamera";
    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    private Activity activity;
    private String mCameraId;
    private Size mPreviewSize;
    private ImageReader mImageReader;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private AutoFitTextureView textureView;
    private SurfaceTexture mSurfaceTexture;
    private Surface mPreviewSurface;

    private boolean active = true;//页面是否是活跃的
    private int screenStopTimeLimit = -1;//屏幕无响应重启时间,单位：秒,如果<0,则不重启播放
    private int screenStopTime = 0;//当前已经有多少秒屏幕没有响应
    private CaptureListener captureListener;//拍照监听
    private FrameListener frameListener;//每帧监听
    private ErrorListener errorListener;//错误监听
    private boolean initSuccess = false;//是否初始化成功
    private int width = 800;//宽
    private int height = 600;//高
    private int cameraId = CameraCharacteristics.LENS_FACING_BACK;//默认后置

    protected YCamera(Activity activity, AutoFitTextureView textureView) {
        this.activity = activity;
        this.textureView = textureView;
        textureView.setSurfaceTextureListener(textureListener);
    }

    public YCamera(Activity activity, AutoFitTextureView textureView, int width, int height, int cameraId) {
        this.activity = activity;
        this.textureView = textureView;
        this.width = width;
        this.height = height;
        this.cameraId = cameraId;
        textureView.setSurfaceTextureListener(textureListener);
    }

    // Surface状态回调
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            initSuccess = true;
            setupCamera(YCamera.this.width, YCamera.this.height);
            configureTransform(YCamera.this.width, YCamera.this.height);
            openCamera();
            thread.start();
        }


        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            screenStopTime = 0;
            if (frameListener != null) {
                //  Bitmap bitmap = Bitmap.createBitmap(YCamera.this.width, YCamera.this.height, Bitmap.Config.ARGB_8888);
                //  textureView.getBitmap(bitmap)
                Bitmap bitmap = textureView.getBitmap();
                activity.runOnUiThread(() -> frameListener.value(bitmap));
            }
        }
    };
    // 摄像头状态回调
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            //开启预览
            startPreview();
        }


        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(TAG, "摄像头设备断开连接");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            String msg = "摄像头设备异常";
            Log.e(TAG, msg);
            if (errorListener != null) {
                activity.runOnUiThread(() -> {
                    errorListener.value(msg);
                });
            }
        }
    };
    private final CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {

        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {

        }
    };

    private void setupCamera(int width, int height) {
        // 获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.i(TAG, "相机个数：" + manager.getCameraIdList().length);
            // 遍历所有摄像头
            for (String item : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(item);
                // 默认打开后置摄像头 - 忽略前置摄像头
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == cameraId)
                    continue;
                // 获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                int orientation = activity.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    textureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }
                mCameraId = item;
                break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        //获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        //检查权限
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                return;
            //打开相机，第一个参数指示打开哪个摄像头，第二个参数stateCallback为相机的状态回调接口，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            manager.openCamera(mCameraId, stateCallback, null);
        } catch (Exception e) {
            String msg = "相机打开失败";
            Log.e(TAG, msg, e);
            if (errorListener != null) {
                errorListener.value(msg);
            }
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == mPreviewSize) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        //matrix.setScale(1, -1);上下镜像
        //matrix.postTranslate(0, textureView.getHeight());
        if (cameraId == CameraCharacteristics.LENS_FACING_BACK) {
            matrix.setScale(-1, 1);//左右镜像
            matrix.postTranslate(textureView.getWidth(), 0);
        }
        //设置旋转
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) viewHeight / mPreviewSize.getHeight(), (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    //开启预览
    private void startPreview() {
        //前三个参数分别是需要的尺寸和格式，最后一个参数代表每次最多获取几帧数据
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 1);
        //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
        mImageReader.setOnImageAvailableListener(reader -> {
            //"拍照获取一张图片"
            Image image = reader.acquireLatestImage();
            new Thread(() -> {
                if (captureListener != null && image.getPlanes().length > 0) {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null)
                        activity.runOnUiThread(() -> {
                            captureListener.value(bitmap);
                        });
                }
                image.close();//一定要关闭
            }).start();
        }, null);

        mSurfaceTexture = textureView.getSurfaceTexture();
        //设置TextureView的缓冲区大小
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        //获取Surface显示预览数据
        mPreviewSurface = new Surface(mSurfaceTexture);
        try {
            // 创建预览请求的Builder（TEMPLATE_PREVIEW表示预览请求）
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //设置预览的显示界面
            mPreviewRequestBuilder.addTarget(mPreviewSurface);
            MeteringRectangle[] meteringRectangles = mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_REGIONS);
            if (meteringRectangles != null && meteringRectangles.length > 0) {
                Log.d(TAG, "PreviewRequestBuilder: AF_REGIONS=" + meteringRectangles[0].getRect().toString());
            }
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);

            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mCaptureSession = session;
                    repeatPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //重新预览
    private void repeatPreview() {
        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mPreviewCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 选择sizeMap中大于并且最接近width和height的size
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, (lhs, rhs) -> Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight()));
        }
        return sizeMap[0];
    }

    // 拍照
    public void capture() {
        capture(ORIENTATION.get(activity.getWindowManager().getDefaultDisplay().getRotation()));
    }

    // 拍照
    public void capture(Integer rotation) {
        try {
            //首先我们创建请求拍照的CaptureRequest
            final CaptureRequest.Builder mCaptureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            //获取屏幕方向
            mCaptureBuilder.addTarget(mPreviewSurface);
            mCaptureBuilder.addTarget(mImageReader.getSurface());
            //设置拍照方向
            mCaptureBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotation);
            //停止预览
            mCaptureSession.stopRepeating();
            //开始拍照，然后回调上面的接口重启预览，因为mCaptureBuilder设置ImageReader作为target，所以会自动回调ImageReader的onImageAvailable()方法保存图片
            mCaptureSession.capture(mCaptureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    //继续预览
                    repeatPreview();
                }
            }, null);
        } catch (Exception e) {
            String msg = "拍照失败";
            Log.e(TAG, msg, e);
            if (errorListener != null) {
                activity.runOnUiThread(() -> {
                    errorListener.value(msg);
                });
            }
        }
    }

    public void onPause() {
        active = false;
        try {
            mCaptureSession.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        active = true;
        if (initSuccess) {
            repeatPreview();
        }
    }

    public void onDestroy() {
        try {
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (Exception ignored) {
        }
        thread.interrupt();
    }


    public void setScreenStopTimeLimit(int screenStopTimeLimit) {
        this.screenStopTimeLimit = screenStopTimeLimit;
    }

    public interface CaptureListener {
        void value(Bitmap bitmap);
    }

    public interface FrameListener {
        void value(Bitmap bitmap);
    }

    public interface ErrorListener {
        void value(String msg);
    }

    public void setCaptureListener(CaptureListener captureListener) {
        this.captureListener = captureListener;
    }

    public void setFrameListener(FrameListener frameListener) {
        this.frameListener = frameListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!thread.isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    if (activity.isFinishing()) break;
                    if (!active) continue;
                    //如果超过screenStopTimeLimit秒无响应，自动重启视频流
                    if (textureView != null && screenStopTimeLimit > 0) {
                        screenStopTime++;
                        if (screenStopTime >= screenStopTimeLimit) {
                            String msg = "屏幕无响应时间大于" + screenStopTimeLimit + "秒";
                            Log.e(TAG, msg);
                            screenStopTime = 0;
                            if (errorListener != null) {
                                activity.runOnUiThread(() -> errorListener.value(msg));
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        }
    });
}