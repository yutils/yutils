package com.yujing.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import android.util.Size
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.yujing.contract.YListener1
import com.yujing.utils.YApp
import com.yujing.utils.YFace
import com.yujing.utils.YLog
import com.yujing.utils.YThread
import java.nio.ByteBuffer
import java.util.*


/**
 * 相机类
 *
 * @author yujing 2020年11月23日16:22:38
 */
/*
用法：

<com.yujing.view.AutoFitTextureView
android:id="@+id/textureView1"
android:layout_width="match_parent"
android:layout_height="match_parent" />

class MainActivity : YBaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    var yCamera1: YCamera? = null

    override fun init() {
        requestAll(this)
        yCamera1 = YCamera(binding.textureView1, YCamera.CAMERA_ID_BACK)
        //初始化
        yCamera1?.init()
        //5秒无响应提示
        yCamera1?.screenStopTimeLimit = 5
        //每2帧取一数据
        yCamera1?.analysisInterval = 2
        //最多几张人脸
        yCamera1?.maxFaces = 5

        //错误监听
        yCamera1?.errorListener = YListener1 { msg -> YToast.show(msg) }

        //拍照监听
        yCamera1?.captureListener = YListener1 { bitmap ->
            YImageDialog.show(bitmap)
            YToast.show("分辨率:${bitmap?.width}x${bitmap?.height}")
        }

        //分析处理帧监听
        yCamera1?.analysisListener = YListener1 { bitmap ->
            binding.imageView.setImageBitmap(bitmap)
        }

        //人脸监听，没人脸不回调
        yCamera1?.faceListener = YListener1 { bitmap ->
           //binding.imageView.setImageBitmap(bitmap)
        }

        //拍照
        binding.takePicture.setOnClickListener { v ->
            yCamera1?.capture()
        }

        //切换摄像头
        binding.btSwitch.setOnClickListener {
            yCamera1?.let {
                if (it.id == YCamera.CAMERA_ID_BACK) {
                    it.switch(YCamera.CAMERA_ID_FRONT)
                } else {
                    it.switch(YCamera.CAMERA_ID_BACK)
                }
            }
        }
    }

    override fun onPause() {
        yCamera1?.onPause()
        super.onPause()
    }

    override fun onResume() {
        yCamera1?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        yCamera1?.onDestroy()
        YImageDialog.finish()
        super.onDestroy()
    }
}
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@Deprecated("废弃")
class YCamera(var textureView: AutoFitTextureView, var id: String?) {
    companion object {
        private const val TAG = "YCamera"
        const val CAMERA_ID_FRONT = "1"
        const val CAMERA_ID_BACK = "0"
    }

    var previewWidth: Int = 800 //预览宽
    var previewHeight: Int = 600 //预览高
    var frameWidth: Int = 640 //抓取每帧宽
    var frameHeight: Int = 480 //抓取每帧高
    var captureWidth: Int = 1024 //拍照宽
    var captureHeight: Int = 768 //拍照高
    var analysisInterval = 1//分析处理间隔,1每一帧，2每两帧
    var active = true //页面是否是活跃的
    var screenStopTimeLimit = -1 //屏幕无响应重启时间,单位：秒,如果<0,则不重启播放
    var initSuccess = false //是否初始化成功
    var mSensorOrientation = 0 //传感器方向
    var captureListener: YListener1<Bitmap>? = null    //拍照监听
    var analysisListener: YListener1<Bitmap>? = null //分析处理帧监听
    var errorListener: YListener1<String>? = null//错误监听
    var maxFaces = 1//最多几张人脸
    var showFacesRectF = true//显示人脸框
    var faceListener: YListener1<Bitmap>? = null//人脸识别监听

    private var analysisCount = 0L
    private var mCameraId: String? = null//实际调用的摄像头ID
    private var mPreviewSize: Size? = null//实际预览宽高
    private var mAnalysisReader: ImageReader? = null//分析每帧监听
    private var mImageReader: ImageReader? = null//拍照监听
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mPreviewSurface: Surface? = null
    private var screenStopTime = 0 //当前已经有多少秒屏幕没有响应
    private var checkThread: Thread? = null  //检查屏幕无响应线程

    /**
     * 切换摄像头
     */
    fun switch(id: String?) {
        if (!initSuccess) return
        onDestroy()
        this.id = id
        setupCamera()
        configureTransform(previewWidth, previewHeight)
        openCamera()
        checkStart()
        onResume()
    }

    /**
     * 初始化
     */
    fun init() {
        // Surface状态回调
        textureView.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                initSuccess = true
                setupCamera()
                configureTransform(previewWidth, previewHeight)
                openCamera()
                checkStart()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                configureTransform(previewWidth, previewHeight)
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                screenStopTime = 0
            }
        }
    }

    /**
     * 初始化摄像头
     */
    private fun setupCamera() {
        // 获取摄像头的管理者CameraManager
        val manager = YApp.get().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            YLog.d(TAG, "相机个数：${manager.cameraIdList.size} 分别：${manager.cameraIdList.contentToString()} 准备启用:${id}")
            // 遍历所有摄像头
            for (item in manager.cameraIdList) {
                if (id == item) {
                    val characteristics = manager.getCameraCharacteristics(item)
                    // 获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    mPreviewSize = getOptimalSize(map?.getOutputSizes(SurfaceTexture::class.java)!!, previewWidth, previewHeight)

                    //防止预览拉变形
                    if (YApp.get().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        textureView.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
                    } else {
                        textureView.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
                    }

                    YLog.d("预览分辨率：${mPreviewSize!!.width}x${mPreviewSize!!.height}")
                    mCameraId = item
                    mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                    break
                }
            }
        } catch (e: CameraAccessException) {
            val msg = "初始化摄像头失败"
            YLog.e(TAG, msg + ":" + e.message, e)
            if (errorListener != null) errorListener?.value(msg)
        }
    }

    /**
     * 打开摄像头
     */
    @SuppressLint("MissingPermission")
    private fun openCamera() {
        if (mCameraId == null) return
        //获取摄像头的管理者CameraManager
        val manager = YApp.get().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        //检查权限
        try {
            if (ActivityCompat.checkSelfPermission(YApp.get(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return
            //打开相机，第一个参数指示打开哪个摄像头，第二个参数stateCallback为相机的状态回调接口，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            manager.openCamera(mCameraId!!, object : CameraDevice.StateCallback() {

                override fun onOpened(camera: CameraDevice) {
                    mCameraDevice = camera
                    //开启预览
                    startPreview()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    YLog.e(TAG, "摄像头设备断开连接")
                    onDestroy()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    val msg = "摄像头设备异常：${error}"
                    YLog.e(TAG, msg)
                    YThread.runOnUiThread { errorListener?.value(msg) }
                }
            }, null)
        } catch (e: Exception) {
            val msg = "相机打开失败"
            YLog.e(TAG, msg + ":" + e.message, e)
            if (errorListener != null) errorListener?.value(msg)
        }
    }

    /**
     * 配置外观
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        if (null == mPreviewSize) return
        val rotation = (YApp.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        val matrix = Matrix()
        //if (mCameraId == CAMERA_ID_FRONT) {
        //matrix.setScale(-1f, 1f) //左右镜像  //matrix.setScale(1, -1);上下镜像
        //matrix.postTranslate(textureView.width.toFloat(), 0f)
        //}
        //设置旋转
        val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = mPreviewSize?.height?.let { mPreviewSize?.width?.let { it1 -> RectF(0F, 0F, it.toFloat(), it1.toFloat()) } }
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect?.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = (viewHeight.toFloat() / mPreviewSize?.height!!).coerceAtLeast(viewWidth.toFloat() / mPreviewSize!!.width)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureView.setTransform(matrix)
    }

    /**
     * 开启预览
     */
    private fun startPreview() {
        //处理，当有图像流数据可用时会回调onImageAvailable方法，可以对这帧数据进行处理
        mAnalysisReader = ImageReader.newInstance(frameWidth, frameHeight, ImageFormat.YUV_420_888, 2)
        mAnalysisReader!!.setOnImageAvailableListener({ reader ->
            //获取下一张图片
            val image = reader!!.acquireNextImage()
            if (analysisCount++ % analysisInterval == 0L) {
                //val startTime = System.currentTimeMillis()
                if (image.format == ImageFormat.YUV_420_888) {
                    val bitmap = ImageUtil.imageToBitmap_YUV_420_888(YApp.get(), image)
                    Thread {
                        if (bitmap == null) return@Thread
                        //预览相对于原数据可能有旋转
                        val matrix = Matrix()
                        val ori = getCameraOri(mCameraId!!)
                        matrix.postRotate(if (CAMERA_ID_BACK == mCameraId) ori.toFloat() else -ori.toFloat())
                        // 对于前置数据，镜像处理
                        if (CAMERA_ID_FRONT == mCameraId) matrix.postScale(-1f, 1f)
                        // 和预览画面相同的bitmap
                        val previewBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)

                        //如果打开了人脸监听
                        if (faceListener != null) {
                            val rfs = YFace.findFaceRectFs(previewBitmap, maxFaces)
                            if (rfs != null) {
                                //显示人脸框
                                if (showFacesRectF) YFace.drawFaceInfos(rfs, previewBitmap)
                                YThread.runOnUiThread { faceListener?.value(previewBitmap) }
                            }
                        }
                        YThread.runOnUiThread { analysisListener?.value(previewBitmap) }
                    }.start()
                }
                //Log.d(TAG, "耗时:${System.currentTimeMillis() - startTime}")
            }
            image.close()
        }, null)

        //拍照，当有图像流数据可用时会回调onImageAvailable方法，可以对这帧数据进行处理
        mImageReader = ImageReader.newInstance(captureWidth, captureHeight, ImageFormat.JPEG, 1)
        mImageReader?.setOnImageAvailableListener({ reader: ImageReader ->
            //获取最后一张图片
            val image = reader.acquireLatestImage()
            if (image.format == ImageFormat.JPEG) {
                if (captureListener != null && image.planes.isNotEmpty()) {
                    val buffer = image.planes[0].buffer
                    val data = ByteArray(buffer.remaining())
                    buffer[data]
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

                    //预览相对于原数据可能有旋转
                    val matrix = Matrix()
                    val ori = getCameraOri(mCameraId!!)
                    matrix.postRotate(if (CAMERA_ID_BACK == mCameraId) ori.toFloat() else -ori.toFloat())
                    // 对于前置数据，镜像处理
                    if (CAMERA_ID_FRONT == mCameraId) matrix.postScale(-1f, 1f)
                    // 和预览画面相同的bitmap
                    val previewBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
                    YThread.runOnUiThread { captureListener?.value(previewBitmap) }
                }
                image.close() //一定要关闭
            }
        }, null)

        //设置surfaceTexture
        mSurfaceTexture = textureView.surfaceTexture
        //设置TextureView的缓冲区大小
        mSurfaceTexture?.setDefaultBufferSize(mPreviewSize?.width!!, mPreviewSize?.height!!)
        //获取Surface显示预览数据
        mPreviewSurface = Surface(mSurfaceTexture)
        try {
            //TEMPLATE_PREVIEW：适用于配置预览的模板
            //TEMPLATE_RECORD：适用于视频录制的模板。
            //TEMPLATE_STILL_CAPTURE：适用于拍照的模板。
            //TEMPLATE_VIDEO_SNAPSHOT：适用于在录制视频过程中支持拍照的模板。
            //TEMPLATE_MANUAL：适用于希望自己手动配置大部分参数的模板。
            mPreviewRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

            //设置预览的显示界面
            mPreviewRequestBuilder?.addTarget(mPreviewSurface!!)
            mPreviewRequestBuilder?.addTarget(mAnalysisReader?.surface!!)

            //设置自动聚焦
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            //设置自动曝光
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            //设置控制模式自动
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)

            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice?.createCaptureSession(listOf(mPreviewSurface, mAnalysisReader?.surface!!, mImageReader?.surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    mCaptureSession = session
                    //开启预览
                    repeatPreview()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {}
            }, null)

        } catch (e: CameraAccessException) {
            val msg = "开启预览失败"
            YLog.e(TAG, msg + ":" + e.message, e)
            if (errorListener != null) errorListener?.value(msg)
        }
    }

    /**
     * 获取图片方向
     */
    private fun getCameraOri(cameraId: String): Int {
        val rotation = (YApp.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        var degrees = rotation * 90
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        val result: Int = if (CAMERA_ID_FRONT == cameraId) {
            (360 - ((mSensorOrientation + degrees) % 360)) % 360
        } else {
            (mSensorOrientation - degrees + 360) % 360
        }
        return result
    }

    /**
     * 重新预览
     */
    private fun repeatPreview() {
        try {
            mCaptureSession?.setRepeatingRequest(mPreviewRequestBuilder?.build()!!, object : CaptureCallback() {}, null)
        } catch (e: Exception) {
            val msg = "预览失败"
            YLog.e(TAG, msg + ":" + e.message, e)
            if (errorListener != null) errorListener?.value(msg)
        }
    }

    /**
     * 选择sizeMap中大于并且最接近width和height的size
     */
    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size {
        val sizeList: MutableList<Size> = ArrayList()
        for (option in sizeMap) {
            if (width > height) {
                if (option.width > width && option.height > height) sizeList.add(option)
            } else {
                if (option.width > height && option.height > width) sizeList.add(option)
            }
        }
        return if (sizeList.size > 0) {
            Collections.min(sizeList) { lhs: Size, rhs: Size -> java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong()) }
        } else sizeMap[0]
    }

    /**
     * 拍照
     */
    fun capture() {
        try {
            //首先我们创建请求拍照的CaptureRequest
            val mCaptureBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            //自动对焦
            mCaptureBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            //设置自动曝光
            mCaptureBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            //设置控制模式自动
            mCaptureBuilder?.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)

            //添加surface
            mCaptureBuilder?.addTarget(mPreviewSurface!!)
            mCaptureBuilder?.addTarget(mImageReader?.surface!!)

            //设置拍照方向
            //val manager = YApp.get().getSystemService(Context.CAMERA_SERVICE) as CameraManager
            //val cameraCharacteristics = manager.getCameraCharacteristics(mCameraId!!)
            //mCaptureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(cameraCharacteristics, rotation))

            //停止预览
            mCaptureSession?.stopRepeating()
            //开始拍照，然后回调上面的接口重启预览，因为mCaptureBuilder设置ImageReader作为target，所以会自动回调ImageReader的onImageAvailable()方法保存图片
            mCaptureSession?.capture(mCaptureBuilder?.build()!!, object : CaptureCallback() {
                //拍照完成
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    //继续预览
                    repeatPreview()
                }

                //拍照失败
                override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
                    //继续预览
                    repeatPreview()
                }
            }, null)
        } catch (e: Exception) {
            val msg = "拍照失败"
            YLog.e(TAG, msg + ":" + e.message, e)
            YThread.runOnUiThread { errorListener?.value(msg) }
        }
    }

    /**
     * 暂停
     */
    fun onPause() {
        active = false
        try {
            mCaptureSession?.stopRepeating()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 继续
     */
    fun onResume() {
        active = true
        if (initSuccess) repeatPreview()
    }

    /**
     * 退出并释放全部
     */
    fun onDestroy() {
        try {
            mCaptureSession?.close()
            mCaptureSession = null
            mCameraDevice?.close()
            mCameraDevice = null
            mImageReader?.close()
            mImageReader = null
            mAnalysisReader?.close()
            mAnalysisReader = null
        } catch (ignored: Exception) {
        }
        checkStop()
    }

    //开始检查，即使多次调用start，也能保证只有一个readThread线程
    @Synchronized
    private fun checkStart() {
        checkThread?.interrupt()
        checkThread = Thread {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(1000)
                    if (!active) continue
                    //如果超过screenStopTimeLimit秒无响应，自动重启视频流
                    if (screenStopTimeLimit > 0) {
                        screenStopTime++
                        if (screenStopTime >= screenStopTimeLimit) {
                            val msg = "屏幕无响应时间大于" + screenStopTimeLimit + "秒"
                            YLog.e(TAG, msg)
                            screenStopTime = 0
                            YThread.runOnUiThread { errorListener?.value(msg) }
                        }
                    }
                } catch (e: Exception) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
            YLog.d("退出线程")
        }
        checkThread?.name = "YCamera-检查屏幕无响应时间"
        checkThread?.start()
    }

    //停止检查
    private fun checkStop() {
        checkThread?.interrupt()
    }

    /**
     * 工具类
     */
    @Suppress("FunctionName")
    class ImageUtil {
        companion object {
            /**
             * image 转  Bitmap
             * 图片格式 YUV_420_888
             */
            fun imageToBitmap_YUV_420_888(context: Context?, image: Image): Bitmap? {
                val yuvBytes = imageToByteBuffer(image)
                val rs = RenderScript.create(context)
                val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
                val allocationRgb = Allocation.createFromBitmap(rs, bitmap)
                val allocationYuv = Allocation.createSized(rs, Element.U8(rs), yuvBytes.array().size)
                allocationYuv.copyFrom(yuvBytes.array())
                val scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
                scriptYuvToRgb.setInput(allocationYuv)
                scriptYuvToRgb.forEach(allocationRgb)
                allocationRgb.copyTo(bitmap)
                return try {
                    bitmap
                } finally {
                    allocationYuv.destroy()
                    allocationRgb.destroy()
                    rs.destroy()
                    image.close()
                }
            }

            /**
             * image转ByteBuffer
             */
            private fun imageToByteBuffer(image: Image): ByteBuffer {
                val crop = image.cropRect
                val width = crop.width()
                val height = crop.height()
                val planes = image.planes
                val rowData = ByteArray(planes[0].rowStride)
                val bufferSize = width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8
                val output = ByteBuffer.allocateDirect(bufferSize)
                var channelOffset = 0
                var outputStride = 0
                for (planeIndex in 0..2) {
                    if (planeIndex == 0) {
                        channelOffset = 0
                        outputStride = 1
                    } else if (planeIndex == 1) {
                        channelOffset = width * height + 1
                        outputStride = 2
                    } else if (planeIndex == 2) {
                        channelOffset = width * height
                        outputStride = 2
                    }
                    val buffer = planes[planeIndex].buffer
                    val rowStride = planes[planeIndex].rowStride
                    val pixelStride = planes[planeIndex].pixelStride
                    val shift = if (planeIndex == 0) 0 else 1
                    val widthShifted = width shr shift
                    val heightShifted = height shr shift
                    buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
                    for (row in 0 until heightShifted) {
                        val length: Int
                        if (pixelStride == 1 && outputStride == 1) {
                            length = widthShifted
                            buffer[output.array(), channelOffset, length]
                            channelOffset += length
                        } else {
                            length = (widthShifted - 1) * pixelStride + 1
                            buffer[rowData, 0, length]
                            for (col in 0 until widthShifted) {
                                output.array()[channelOffset] = rowData[col * pixelStride]
                                channelOffset += outputStride
                            }
                        }
                        if (row < heightShifted - 1) {
                            buffer.position(buffer.position() + rowStride - length)
                        }
                    }
                }
                return output
            }
        }
    }
}