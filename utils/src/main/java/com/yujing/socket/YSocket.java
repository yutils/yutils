package com.yujing.socket;

import static java.lang.System.currentTimeMillis;

import com.yujing.utils.YClass;
import com.yujing.utils.YLog;
import com.yujing.utils.YThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * YSocket，套接字连接
 * 1.启动时候，如果服务器没有启动或者检测不到服务器已经启动，就每3秒（默认）重新连接一次，连接成功后回调成功，没有连接成功回调失败。
 * 2.如果使用中途与服务器断开（如断网，服务器重启）立即回到等待连接状态，并且回调连接失败。每隔3秒（默认）重新连接一次服务器，如果此时再次连接上服务器立即回调连接成功。
 * 使用用于保持socket连接，和自动重新连接功能。 首先我们有3主要线程。
 * 读取消息线程：用于读取次socket消息，当socket断开后此线程应该及时关闭，当socket重新连接后，此线程应该重新创建以便于读取新的线程中的数据。
 * 心跳线程：当启动后每一定时间发送一条心跳信息，当心跳发送失败时把连接状态（connect）标记为失败，反之成功。
 * 连接线程：连接线程每一定时间根据（connect）检查一连接，如果连接断开就重新连接，更新socket，并通知连接状态。
 *
 * @author 余静
 * @version 1.5 2021年8月6日10:17:35
 */

/*
使用方法：

// 连接或者重新连接
private void connect() {
    // 断开已有的连接
    YSocket.getInstance().exit();
    // 创建实例
    YSocket.getInstance("192.168.6.154", 8892);
    //设置显示日志总开关
    YSocket.getInstance().setShowLog(true);
    // 显示接收日志
    YSocket.getInstance().setShowReceiveLog(true);
    // 显示发送日志
    YSocket.getInstance().setShowSendLog(true);
    // 设置心跳内容
    YSocket.getInstance().setHearBytes(new byte[]{0x00});
    // InputStream转化成bytes
    YSocket.getInstance().setInputStreamReadListener(inputStreamReadListener);
    // 添加返回消息监听
    YSocket.getInstance().addDataListener(dataListener);
    // 连接状态监听
    YSocket.getInstance().setConnectListener(
            success -> {
                if (success) {
                    // 连接成功
                    System.out.println("连接成功");
                } else {
                    // 连接失败
                    System.out.println("连接失败");
                }
            });
    // 开始运行
    YSocket.getInstance().start();
}

//java 读取解析inputStream中的内容
YSocket.InputStreamReadListener inputStreamReadListener = inputStream -> readOnce(inputStream, 1000 * 10);
//只读一次，读取到就返回。读取不到，一直等直到超时，如果超时则向上抛异常,防止available()卡死
public static byte[] readOnce(InputStream inputStream, long timeOut) throws Exception {
    long startTime = System.currentTimeMillis();
    int count = 0;
    while (count == 0 && System.currentTimeMillis() - startTime < timeOut)
        count = inputStream.available();//获取真正长度
    if (System.currentTimeMillis() - startTime >= timeOut)
        throw new TimeoutException("读取超时");
    byte[] bytes = new byte[count];
    // 一定要读取count个数据，如果inputStream.read(bytes);可能读不完
    int readCount = 0; // 已经成功读取的字节的个数
    while (readCount < count)
        readCount += inputStream.read(bytes, readCount, count - readCount);
    return bytes;
}

//java 组包 读取解析inputStream中的内容
ySocket.setInputStreamReadListener(inputStream -> {
    //读取协议头
    int count = 10;
    byte[] bytes1 = new byte[count];
    // 一定要读取count个数据，如果inputStream.read(bytes);可能读不完
    int readCount = 0; // 已经成功读取的字节的个数
    while (readCount < count) {
        readCount += inputStream.read(bytes1, readCount, count - readCount);
    }
    if (bytes1[0] != 0x5A) {
        return null;
    }
    //读取正文
    int length = YConvertBytes.bytesToInt(bytes1, 6);
    byte[] bytes2 = new byte[length];
    int readContent = 0; // 已经成功读取的字节的个数
    while (readContent < length) {
        readContent += inputStream.read(bytes2, readContent, length - readContent);
    }
    //组装
    byte[] bytes3 = new byte[bytes1.length + bytes2.length];
    System.arraycopy(bytes1, 0, bytes3, 0, bytes1.length);
    System.arraycopy(bytes2, 0, bytes3, bytes1.length, bytes2.length);
    return bytes3;
});
ySocket.start();

//收到数据监听
var dataListener = YSocket.DataListener { bytes ->
    YLog.i("收到：" + YConvert.bytesToHexString(bytes))
}

//发送
fun send(str: String) {
    YSocket.getInstance().send(str.toByteArray(), null)
}

//退出
override fun onDestroy() {
    super.onDestroy()
    YSocket.getInstance().exit()
}
*/

@SuppressWarnings("WeakerAccess")
public class YSocket {
    protected Socket socket;// 套接字
    protected String ip;// 服务器IP
    protected int port;// 服务器端口
    protected ReadThread readThread;// 读取线程
    protected ConnectThread connectThread;// 连接线程
    protected HeartbeatThread heartbeat;// 心跳线程
    protected byte[] hearBytes = new byte[0];// 心跳包
    //发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
    protected int UrgentData = 0xFF;
    //没有设置心跳包时,发送紧急数据
    protected boolean noHeartbeatSendUrgentData = true;
    protected List<StateListener> connectListeners = new ArrayList<>();// 连接监听
    protected List<DataListener> dataListeners = new ArrayList<>();// 数据收到数据监听
    protected boolean connect;// 连接状态
    protected int heartTime = 1000 * 3;// 心跳间隔时间
    protected int CheckConnectTime = 1000 * 3;// 检查连接时间
    protected int timeOut = 1000 * 30; // 每次读取最长时间，防止inputStream.available()卡死
    protected boolean showLog = true;// 显示日志
    protected boolean showReceiveLog = false; // 显示接收日志
    protected boolean showSendLog = false; // 显示发送日志
    protected InputStreamReadListener inputStreamReadListener;//读取InputStream接口
    protected CreateSocketInterceptor createSocketInterceptor;//创建Socket

    /**
     * 构造函数
     *
     * @param ip   服务器IP地址
     * @param port 服务器端口
     */
    public YSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private static volatile YSocket instance;

    /**
     * 单例模式，调用此方法前必须先调用getInstance(String ip, int port)
     */
    public static YSocket getInstance() {
        if (instance == null) {
            synchronized (YSocket.class) {
                if (instance == null)
                    instance = new YSocket(null, 0);
            }
        }
        return instance;
    }

    /**
     * 单例模式
     */
    public static YSocket getInstance(String ip, int port) {
        if (instance == null) {
            synchronized (YSocket.class) {
                if (instance == null)
                    instance = new YSocket(ip, port);
            }
        }
        instance.setIp(ip);
        instance.setPort(port);
        return instance;
    }

    /**
     * 设置是否显示日志
     */
    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    /**
     * 设置是否显示接收日志
     */
    public void setShowReceiveLog(boolean showReceiveLog) {
        this.showReceiveLog = showReceiveLog;
    }

    /**
     * 设置是否显示发送日志
     */
    public void setShowSendLog(boolean showSendLog) {
        this.showSendLog = showSendLog;
    }

    /**
     * 读取到数据监听回调
     */
    public void addDataListener(DataListener dataListener) {
        if (!dataListeners.contains(dataListener))
            dataListeners.add(dataListener);
    }

    /**
     * 删除读取到数据监听回调
     */
    public void removeDataListener(DataListener dataListener) {
        dataListeners.remove(dataListener);
    }

    /**
     * 清空读取到数据监听回调
     */
    public void clearDataListener() {
        dataListeners.clear();
    }


    /**
     * 连接状态监听回调
     */
    public void addConnectListener(StateListener connectListener) {
        if (!connectListeners.contains(connectListener))
            this.connectListeners.add(connectListener);
    }

    /**
     * 设置连接状态监听回调
     */
    public void setConnectListener(StateListener connectListener) {
        this.connectListeners.clear();
        this.connectListeners.add(connectListener);
    }

    /**
     * 删除状态监听回调
     */
    public void removeConnectListener(StateListener connectListener) {
        this.connectListeners.remove(connectListener);
    }

    /**
     * 实现读取InputStream方法,如果未赋值，则采用默认方法。
     *
     * @param inputStreamReadListener 读取inputStream
     */
    public void setInputStreamReadListener(InputStreamReadListener inputStreamReadListener) {
        this.inputStreamReadListener = inputStreamReadListener;
    }

    /**
     * 清空状态监听回调
     */
    public void clearConnectListener() {
        this.connectListeners.clear();
    }

    /**
     * 获取当前ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取当前端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置端口
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 设置心跳数据
     */
    public void setHearBytes(byte[] hearBytes) {
        this.hearBytes = hearBytes;
    }

    /**
     * 设置心跳间隔时间
     */
    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }

    /**
     * 设置检查重新连接时间
     */
    public void setCheckConnectTime(int checkConnectTime) {
        CheckConnectTime = checkConnectTime;
    }

    /**
     * 每次读取最长时间，超过这个时间没数据就重新读取，防止inputStream.available()卡死
     *
     * @return 毫秒
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * 每次读取最长时间，超过这个时间没数据就重新读取，防止inputStream.available()卡死
     *
     * @param timeOut 毫秒
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * 紧急数据
     *
     * @return 1个字节
     */
    public int getUrgentData() {
        return UrgentData;
    }

    /**
     * 紧急数据
     */
    public void setUrgentData(int urgentData) {
        UrgentData = urgentData;
    }

    /**
     * 是否开启没有设置心跳包时，发送紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
     *
     * @return true的时候, 设置心跳包时, 发送紧急数据判断网络状态
     */
    public boolean isNoHeartbeatSendUrgentData() {
        return noHeartbeatSendUrgentData;
    }

    /**
     * 是否开启没有设置心跳包时，发送紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
     * true的时候,设置心跳包时,发送紧急数据判断网络状态
     */
    public void setNoHeartbeatSendUrgentData(boolean noHeartbeatSendUrgentData) {
        this.noHeartbeatSendUrgentData = noHeartbeatSendUrgentData;
    }

    /**
     * 开始，此方法只能调一次，用于启动心跳发送线程和连接线程，当连接线程连接成功后启动读取数据线程，当收到连接断开消息后，关闭读取消息线程。
     */
    public void start() {
        heartbeat = new HeartbeatThread();
        heartbeat.setName("YSocket-心跳线程");
        heartbeat.start();
        connectThread = new ConnectThread();
        connectThread.setConnectListener(success -> {
            if (success) {
                startReadThread();
            } else {
                closeReadThread();
            }
            for (int i = 0; i < connectListeners.size(); i++) {
                backNotice(connectListeners.get(i), success);
            }
        });
        connectThread.setName("YSocket-连接线程");
        connectThread.start();
    }

    /**
     * 心跳类，用于发送心跳包
     */
    protected class HeartbeatThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Thread.sleep(heartTime);
                } catch (InterruptedException e) {
                    interrupt();
                }
                send(hearBytes);
            }
            printLog("退出心跳线程");
        }

        void send(final byte[] bytes) {
            if (socket == null) return;
            try {
                if (bytes == null || bytes.length == 0) {
                    //如果开启了,没有设置心跳包时发送紧急数据
                    if (noHeartbeatSendUrgentData) socket.sendUrgentData(UrgentData);
                    return;
                }
                OutputStream os = socket.getOutputStream();// 获得输出流
                os.write(bytes);
                os.flush();
                connect = true;
            } catch (Exception e) {
                connect = false;
            }
        }
    }

    /**
     * 连接类，保存连接，每3秒检查一次连接状态，如果断开
     */
    protected class ConnectThread extends Thread {
        StateListener connectListener;// 连接监听

        public void setConnectListener(StateListener connectListener) {
            this.connectListener = connectListener;
        }

        @Override
        public void run() {
            // 保持连接
            while (!isInterrupted()) {
                if (socket == null || !connect) {
                    try {
                        socket = (createSocketInterceptor != null) ? createSocketInterceptor.create() : new Socket();
                        SocketAddress socAddress = new InetSocketAddress(ip, port);// 连接
                        socket.connect(socAddress, 1000 * 5);
                        socket.setKeepAlive(true);
                        connect = true;
                        printLog("连接成功...");
                        backNotice(connectListener, true);
                    } catch (Exception e) {
                        backNotice(connectListener, false);
                        closeSocket();
                        printLog("正在重新连接...");
                    }
                }
                try {
                    Thread.sleep(CheckConnectTime);
                } catch (Exception es) {
                    interrupt();
                }
            }
            printLog("退出保持连接线程");
        }
    }

    /**
     * 读取类
     */
    protected class ReadThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    InputStream is = socket.getInputStream();
                    byte[] resultBytes = inputStreamToBytes(is);
                    if (resultBytes == null) {
                        if (showReceiveLog) printLog("resultByte==null");
                        continue;
                    }
                    if (resultBytes.length == 0) {
                        if (showReceiveLog) printLog("resultBytes.length==0");
                        continue;
                    }
                    if (showReceiveLog) printLog("收到:" + Arrays.toString(resultBytes));
                    connect = true;
                    backData(dataListeners, resultBytes);
                } catch (TimeoutException ignored) {
                } catch (Exception e) {
                    printLog("ReadThread：" + e.getMessage());
                    // 失败3秒后重新读取
                    if (!isInterrupted()) {
                        try {
                            Thread.sleep(CheckConnectTime);
                        } catch (InterruptedException e1) {
                            interrupt();
                        }
                    }
                }
            }
            printLog("退出读取线程");
        }
    }

    /**
     * 关闭全部连接,关闭读取线程,关闭连接线程,关闭心跳线程,关闭socket
     */
    public void closeConnect() {
        if (heartbeat != null)
            heartbeat.interrupt();
        if (connectThread != null)
            connectThread.interrupt();
        closeReadThread();
        for (int i = 0; i < connectListeners.size(); i++) {
            backNotice(connectListeners.get(i), false);
        }
        closeSocket();
    }

    /**
     * 退出
     */
    public void exit() {
        closeConnect();
        clearConnectListener();
        clearDataListener();
        instance = null;
    }

    /**
     * 关闭Socket
     */
    protected void closeSocket() {
        try {
            if (socket != null) {
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            printLog("closeSocket:" + e.getMessage());
        }
    }

    /**
     * 开启读取线程
     */
    protected void startReadThread() {
        closeReadThread();
        readThread = new ReadThread();
        readThread.setName("YSocket-读取线程");
        readThread.start();
    }

    /**
     * 关闭读取线程
     */
    protected void closeReadThread() {
        if (readThread != null)
            readThread.interrupt();
    }

    /**
     * 打印日志
     */
    protected void printLog(String str) {
        if (showLog) YLog.d(str);
    }

    /**
     * 获取当前连接状态
     */
    public boolean isConnect() {
        return connect;
    }

    /**
     * 发送消息byte[]
     *
     * @param bytes         消息byte[]
     * @param stateListener 成功与否监听
     */
    public void send(final byte[] bytes, final StateListener stateListener) {
        Thread thread = new Thread(() -> {
            // socket==null直接返回失败
            if (socket == null) {
                backNotice(stateListener, false);
                return;
            }
            // 判断消息为空直接丢弃
            if (bytes == null || bytes.length == 0) return;
            // 发送消息
            try {
                OutputStream os = socket.getOutputStream();// 获得输出流
                os.write(bytes);
                os.flush();
                if (showSendLog) printLog("发送:" + Arrays.toString(bytes));
                connect = true;
                backNotice(stateListener, true);
            } catch (Exception e) {
                connect = false;
                backNotice(stateListener, false);
                printLog("SendThread：" + e.getMessage());
            }
        });
        thread.setName("YSocket-发送线程");
        thread.start();
    }

    /**
     * 回调数据，考虑到服务器可能在短时间内多条消息推送，本地消息处理有一定时间，为了不漏读服务器每一条消息，因此这里单独开线程。又因为回调数据处理时可能引发异常，为了引起读取线程崩溃，因此进行异常捕获。
     */
    protected void backData(final List<DataListener> dataListeners, final byte[] bytes) {
        if (dataListeners != null) {
            try {
                Thread thread = new Thread(() -> {
                    try {
                        for (DataListener dataListener : dataListeners) {
                            if (dataListener != null) {
                                if (YClass.isAndroid()) {
                                    YThread.runOnUiThread(() -> {
                                        dataListener.data(bytes);
                                    });
                                } else {
                                    dataListener.data(bytes);
                                }
                            }
                        }
                    } catch (Exception e) {
                        printLog("错误：" + e.getMessage());
                        e.printStackTrace();
                    }
                });
                thread.setName("YSocket-回调数据");
                thread.start();
            } catch (Exception e) {
                printLog("错误：" + e.getMessage());
            }
        }
    }

    /**
     * 回调状态通知，
     * 回调数据，考虑到状态可能在短时间内多次变化回调，本地消息处理有一定时间，为了不卡住读取连接线程，因此这里单独开线程。又因为回调数据处理时可能引发异常，为了引起读取线程崩溃，因此进行异常捕获。
     */
    protected void backNotice(final StateListener stateListener, final boolean status) {
        if (stateListener != null) {
            Thread thread = new Thread(() -> {
                try {
                    if (YClass.isAndroid()) {
                        YThread.runOnUiThread(() -> {
                            stateListener.isSuccess(status);
                        });
                    } else {
                        stateListener.isSuccess(status);
                    }
                } catch (Exception e) {
                    printLog("错误：" + e.getMessage());
                    e.printStackTrace();
                }
            });
            thread.setName("YSocket-回调状态");
            thread.start();
        }
    }

    /**
     * 读取inputStream到byte数组中，在网络数据读取中inputStream.available()可能读取不到真是大小，因此采用如下循环方式读取inputStream数据长度。
     * inputStream.read(bytes);可能读不完inputStream中全部数据，所以采用循环方式读取数据。
     */
    public byte[] inputStreamToBytes(InputStream inputStream) throws Exception {
        if (inputStreamReadListener != null)
            return inputStreamReadListener.inputStreamToBytes(inputStream);
        long startTime = currentTimeMillis();
        int count = 0;
        while (count == 0 && currentTimeMillis() - startTime < timeOut)
            count = inputStream.available();//获取真正长度
        if (currentTimeMillis() - startTime >= timeOut)
            throw new TimeoutException("读取超时");
        byte[] bytes = new byte[count];
        // 一定要读取count个数据，如果inputStream.read(bytes);可能读不完
        int readCount = 0; // 已经成功读取的字节的个数
        while (readCount < count)
            readCount += inputStream.read(bytes, readCount, count - readCount);
        return bytes;
    }

    /**
     * 获取当前socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * 如果实现了此接口，创建socket将从此接口中获取
     *
     * @param createSocketInterceptor 创建socket拦截器
     */
    public void setCreateSocketInterceptor(CreateSocketInterceptor createSocketInterceptor) {
        this.createSocketInterceptor = createSocketInterceptor;
    }

    /**
     * 读取数据监听接口
     */
    public interface DataListener {
        void data(byte[] bytes);
    }

    /**
     * 状态监听接口
     */
    public interface StateListener {
        void isSuccess(boolean success);
    }

    /**
     * inputSteam读取解析监听
     */
    public interface InputStreamReadListener {
        byte[] inputStreamToBytes(InputStream inputStream) throws Exception;
    }

    /**
     * 创建socket
     */
    public interface CreateSocketInterceptor {
        Socket create() throws IOException;
    }
}
