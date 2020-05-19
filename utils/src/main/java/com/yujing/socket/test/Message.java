package com.yujing.socket.test;

import android.util.Log;

import com.yujing.utils.YBytes;
import com.yujing.utils.YConvertBytes;

import java.nio.charset.StandardCharsets;

/**
 * 通讯消息类，消息由请求协议头(第0位)+命令类型(第7位)+数据长度(第8-12位)+数据+校验位(最后一位)构成
 *
 * @author YuJing 2018年11月26日14:23:15
 */
@SuppressWarnings("unused")
public class Message {
    private YBytes yBytes;

    /**
     * 构造函数创建一个Bytes对象，长度为12
     */
    public Message() {
        yBytes = new YBytes(12);
        yBytes.changeByte((byte) 0xA5, 0);
    }

    /**
     * 构造函数创建一个Bytes对象，长度为12，并且设置命令类型（控制位）
     */
    public Message(int cmd) {
        yBytes = new YBytes(12);
        yBytes.changeByte((byte) 0xA5, 0);
        yBytes.changeByte((byte) cmd, 7);
    }

    /**
     * 构造函数创建一个Bytes对象,直接赋值默认byte数组
     *
     * @param b 默认值
     */
    public Message(byte[] b) {
        yBytes = new YBytes(b);
    }

    /**
     * 设置命令类型（控制位）
     *
     * @param cmd 值
     * @return Message
     */
    public Message setCommand(int cmd) {
        yBytes.changeByte((byte) cmd, 7);
        return this;
    }

    /**
     * 获取控制位
     *
     * @return 控制位
     */
    public int getCommand() {
        return yBytes.getBytes()[7];
    }

    /**
     * 获取返回数据控制位
     *
     * @return 返回数据的控制位
     */
    public int getResultCommand() {
        return yBytes.getBytes()[5];
    }

    /**
     * 设置数据长度
     *
     * @param length 长度
     * @return Message
     */
    private Message setLength(int length) {
        byte[] lengthBytes = YConvertBytes.intToBytes(length);
        yBytes.changeByte(lengthBytes, 8);
        return this;
    }

    /**
     * 获取正文byte长度
     *
     * @return 正文byte长度
     */
    private int getDataLength() {
        return YConvertBytes.bytesToInt(yBytes.getBytes(), 8);
    }

    /**
     * 获取返回正文byte长度
     *
     * @return 返回的正文byte长度
     */
    private int getResultDataLength() {
        return YConvertBytes.bytesToInt(yBytes.getBytes(), 6);
    }

    /**
     * 设置数据
     *
     * @param data 值
     * @return Message
     */
    public Message setData(String data) {
        byte[] stringBytes;
        stringBytes = data.getBytes(StandardCharsets.UTF_8);
        setLength(stringBytes.length);
        yBytes.addByte(stringBytes);
        Log.i("send", data);
        return this;
    }

    /**
     * 发送的data，解析data部分字符串
     *
     * @return 发送的data
     */
    public String getData() {
        int index = 12;
        byte[] stringBytes = yBytes.getBytes();
        byte[] temp = new byte[stringBytes.length - index];
        System.arraycopy(stringBytes, index, temp, 0, temp.length);
        String data;
        data = new String(temp, StandardCharsets.UTF_8);
        return data;
    }

    /**
     * 接收的data，解析data部分字符串
     *
     * @return 接收的data
     */
    public String getResultData() {
        int index = 10;
        byte[] stringBytes = yBytes.getBytes();
        byte[] temp = new byte[stringBytes.length - index];
        System.arraycopy(stringBytes, index, temp, 0, temp.length);
        String data;
        data = new String(temp, StandardCharsets.UTF_8);
        return data;
    }

    /**
     * 获取bytes数组
     *
     * @return 发送的Bytes
     */
    public byte[] getBytes() {
        // 计算校验位
        int chk = 0;
        for (byte b : yBytes.getBytes())
            chk += b;
        byte[] temp = new byte[yBytes.getBytes().length + 1];
        System.arraycopy(yBytes.getBytes(), 0, temp, 0, yBytes.getBytes().length);
        temp[temp.length - 1] = (byte) chk;
        return temp;
    }

    /**
     * 获取返回bytes数组
     *
     * @return 接收的Bytes
     */
    public byte[] getResultBytes() {
        return yBytes.getBytes();
    }
}
