package com.yujing.crypt.test;

import com.yujing.crypt.YEncrypt;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文件
 */
public class FileUtil {
	/**
	 * 从指定路径读取文件到字节数组中,对于一些非文本格式的内容可以选用这个方法 457364578634785634534
	 * 
	 * @param path
	 *            文件路径,包含文件名
	 * @return byte[] 文件字节数组
	 * 
	 */
	public static byte[] getFile(String path) {
		byte[] data = new byte[0];
		try {
			FileInputStream stream = new FileInputStream(path);
			int size = stream.available();
			data = new byte[size];
			stream.read(data);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println("No Find File");
		} catch (IOException e) {
			System.out.println("IO Error");
		}
		return data;
	}

	/**
	 * 把字节内容写入到对应的文件，对于一些非文本的文件可以采用这个方法。
	 * 
	 * @param data
	 *            将要写入到文件中的字节数据
	 * @param path
	 *            文件路径,包含文件名
	 * @return boolean isOK 当写入完毕时返回true;
	 */
	public static boolean toFile(byte[] data, String path) {
		File file2 = new File(path);
		File file = new File(file2.getParent());
		if (!file.exists()) {// 如果位置不存在
			file.mkdirs();
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(path);
			out.write(data);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("No Find File");
			return false;
		} catch (IOException e) {
			System.out.println("IO Error");
			return false;
		}
		return true;
	}

	public static void copyFile(File file1, File file2) {
		File file = new File(file2.getParent());
		if (!file.exists()) {// 如果位置不存在
			file.mkdirs();
		}
		Show show = null;
		try {
			FileInputStream fis = new FileInputStream(file1);
			// 如果下面的语句使用BufferedOutputStream来修饰则带来更好的性能现。
			FileOutputStream fos = new FileOutputStream(file2);
			show = new Show(fis.available());
			int length;
			byte[] b = new byte[1048576];
			while ((length = fis.read(b)) != -1) {
				if (show.isShow())
					show.set(fis.available());
				fos.write(b, 0, length);
			}
			show.finish();
			System.out.print("copyFile OK!!!");
			fos.close();
			fis.close();
		} catch (FileNotFoundException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("No Find File");
		} catch (IOException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("IO Error");
		}
	}

	public static void encodeFast(File file1, File file2) {
		if (file1.equals(file2)) {
			System.out.print("Error:file1=file2");
			return;
		}
		File file = new File(file2.getParent());
		if (!file.exists()) {// 如果位置不存在
			file.mkdirs();
		}
		Show show = null;
		try {
			FileInputStream fis = new FileInputStream(file1);
			FileOutputStream fos = new FileOutputStream(file2);
			BufferedOutputStream bos = new BufferedOutputStream(fos);// 如果使用BufferedOutputStream来修饰则带来更好的性能现。
			show = new Show(fis.available());
			int length = 1048576;// 每次读取长度
			byte[] data = new byte[length];
			int readLength;// 实际读取长度
			YEncrypt yFastEncrypt = new YEncrypt();
			while ((readLength = fis.read(data)) != -1) {
				if (show.isShow())
					show.set(fis.available());
				byte[] temp = yFastEncrypt.encodeFast(data);
				bos.write(temp, 0, readLength);
			}
			show.finish();
			System.out.println("encode OK!!!");
			bos.close();
			fos.close();
			fis.close();
		} catch (FileNotFoundException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("No Find File");
		} catch (IOException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("IO Error");
		}
	}

	public static void decodeFast(File file1, File file2) {
		if (file1.equals(file2)) {
			System.out.print("Error:file1=file2");
			return;
		}
		File file = new File(file2.getParent());
		if (!file.exists()) {// 如果位置不存在
			file.mkdirs();
		}
		Show show = null;
		try {
			FileInputStream fis = new FileInputStream(file1);
			FileOutputStream fos = new FileOutputStream(file2);
			BufferedOutputStream bos = new BufferedOutputStream(fos);// 如果使用BufferedOutputStream来修饰则带来更好的性能现。
			show = new Show(fis.available());
			int length = 1048576;// 每次读取长度
			byte[] data = new byte[length];
			int readLength;// 实际读取长度
			YEncrypt yFastEncrypt = new YEncrypt();
			while ((readLength = fis.read(data)) != -1) {
				if (show.isShow())
					show.set(fis.available());
				byte[] temp = yFastEncrypt.decodeFast(data);
				bos.write(temp, 0, readLength);
			}
			show.finish();
			System.out.println("encode OK!!!");
			bos.close();
			fos.close();
			fis.close();
		} catch (FileNotFoundException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("No Find File");
		} catch (IOException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("IO Error");
		}
	}

	public static void encode(File file1, File file2) {
		if (file1.equals(file2)) {
			System.out.print("Error:file1=file2");
			return;
		}
		File file = new File(file2.getParent());
		if (!file.exists()) {// 如果位置不存在
			file.mkdirs();
		}
		Show show = null;
		try {
			FileInputStream fis = new FileInputStream(file1);
			FileOutputStream fos = new FileOutputStream(file2);
			BufferedOutputStream bos = new BufferedOutputStream(fos);// 如果使用BufferedOutputStream来修饰则带来更好的性能现。
			int length = 1048576;// 每次读取长度
			byte[] data = new byte[length];
			int readLength;// 实际读取长度
			YEncrypt encrypt = new YEncrypt();
			show = new Show(fis.available());
			while ((readLength = fis.read(data)) != -1) {
				if (show.isShow())
					show.set(fis.available());
				if (readLength != data.length) {// 最后一次读取
					byte[] data2 = new byte[readLength];
					System.arraycopy(data, 0, data2, 0, data2.length);
					byte[] temp = YEncrypt.Base64.encode(encrypt.encode(data2)).getBytes();
					bos.write(temp, 0, temp.length);
				} else {
					byte[] temp = YEncrypt.Base64.encode(encrypt.encode(data)).getBytes();
					bos.write(temp, 0, temp.length);
					bos.write((new byte[] { 13 }), 0, 1);// 加换行符
				}
			}
			show.finish();
			System.out.print("encode OK!!!");
			bos.close();
			fos.close();
			fis.close();
		} catch (FileNotFoundException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("No Find File");
		} catch (IOException e) {
			if (show != null) {
				show.stopShow();
			}
			System.out.println("IO Error");
		}
	}

	public static void decode(File file1, File file2) {
		if (file1.equals(file2)) {
			System.out.print("Error:file1=file2");
			return;
		}
		File file = new File(file2.getParent());
		if (!file.exists()) {// 如果位置不存在
			file.mkdirs();
		}
		Show show = null;
		try {
			FileInputStream fis = new FileInputStream(file1);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			// 如果下面的语句使用BufferedOutputStream来修饰则带来更好的性能现。
			FileOutputStream fos = new FileOutputStream(file2);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			show = new Show(fis.available());
			String line;// 按行读取数据
			YEncrypt encrypt = new YEncrypt();
			while ((line = br.readLine()) != null) {
				if (show.isShow())
					show.set(fis.available());
				byte[] temp = encrypt.decode(YEncrypt.Base64.decode(line));
				bos.write(temp, 0, temp.length);
			}
			show.finish();
			System.out.print("decode OK!!!");
			bos.close();
			fos.close();
			br.close();
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("No Find File");
			if (show != null) {
				show.stopShow();
			}
		} catch (IOException e) {
			System.out.println("IO Error");
			if (show != null) {
				show.stopShow();
			}
		} catch (Exception e) {
			System.out.println("Error,或许文件并未加密");
			if (show != null) {
				show.stopShow();
			}
		}
	}
}
