package com.yujing.socket.test;

import com.yujing.socket.YSocket;
import com.yujing.utils.YConvertBytes;

import java.util.Arrays;

public class ClientTest {

	public static void main(String[] args) throws InterruptedException {
		YSocket ySocket = new YSocket("127.0.0.1", 5555);
		ySocket.addConnectListener(isSuccess -> System.out.println("连接状态：" + isSuccess));
		ySocket.addDataListener(bytes -> {
			System.out.println("收到返回信息：" + Arrays.toString(bytes));
			byte[] re = new byte[bytes.length - 10];
			System.arraycopy(bytes, 10, re, 0, re.length);
			String s = new String(re);
			System.out.println("收到返回信息：" + s);
		});
		//设置读取方法
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


		Thread.sleep(1000);
		String str = "{ \"Command\":2, \"DeviceNo\":\"Ab32156\", \"DeviceType\":\"磅码设备\", \"DeviceVersion\":\"1.0.1\"}";
		Message message = new Message(2);
		message.setData(str);
		for (int i = 0; i < 3; i++) {
			ySocket.send(message.getyBytes(), isSuccess -> System.out.println("发送：" + isSuccess));
			Thread.sleep(1000 * 5);
		}
		ySocket.closeConnect();
	}
}
