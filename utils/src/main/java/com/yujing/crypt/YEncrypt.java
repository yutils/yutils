package com.yujing.crypt;

import java.nio.charset.StandardCharsets;

/** 
* 加密解密算法
* @author YuJing 2017年3月29日 下午6:16:57
*  
*/
@SuppressWarnings("unused")
public class YEncrypt {
	
	public YEncrypt() {
	}

	public String encode(String str) {
		return Base64.encode(encode(str.getBytes()));
	}

	public String decode(String psw) {
		return new String(decode(Base64.decode(psw)));
	}

	public String encode(String str, String passWord) {
		return Base64.encode(encode(str.getBytes(), passWord));
	}

	public String decode(String psw, String passWord) {
		return new String(decode(Base64.decode(psw), passWord));
	}

	private byte[] intToByte(int int65535) {// 只能是0到65535之间的数不能为负数
		return new byte[] { (byte) (int65535 >> 8), (byte) (int65535 % 256) };
	}

	private int byteToInt(byte[] byte65535) {
		return ((byte65535[0] & 0xFF) << 8) + (byte65535[1] & 0xFF);// byte65535[0]&0xFF把byte转0-255
	}

	// 加密
	public byte[] encode(byte[] byteArry) {
		int key1 = (int) (Math.random() * 65535);
		int key2 = (int) (Math.random() * 65535);
		byte[] bytes = new byte[byteArry.length + 6];
		bytes[2] = intToByte(key1)[0];
		bytes[3] = intToByte(key1)[1];
		bytes[4] = intToByte(key2)[0];
		bytes[5] = intToByte(key2)[1];
		byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
		byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
		long xy = key1 + key2;// 校验位
		for (int i = 0; i < byteArry.length; i++) {
			xy += byteArry[i];
			bytes[i + 6] = (byte) ((int) byteArry[i] + (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
		}
		xy = Math.abs(xy);
		bytes[0] = intToByte((int) xy % 65535)[0];
		bytes[1] = intToByte((int) xy % 65535)[1];
		return bytes;
	}

	// 加密
	public byte[] encode(byte[] byteArry, String passWord) {
		byte[] p = passWord.getBytes();
		int key1 = (int) (Math.random() * 65535);
		int key2 = (int) (Math.random() * 65535);
		byte[] bytes = new byte[byteArry.length + 6];
		bytes[2] = intToByte(key1)[0];
		bytes[3] = intToByte(key1)[1];
		bytes[4] = intToByte(key2)[0];
		bytes[5] = intToByte(key2)[1];
		byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
		byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
		long xy = key1 + key2;// 校验位
		for (int i = 0; i < byteArry.length; i++) {
			xy += byteArry[i];
			bytes[i + 6] = (byte) ((int) byteArry[i] + p[i % p.length] + (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
		}
		xy = Math.abs(xy);
		bytes[0] = intToByte((int) xy % 65535)[0];
		bytes[1] = intToByte((int) xy % 65535)[1];
		return bytes;
	}

	// 解密
	public byte[] decode(byte[] byteArry) {
		int xy = byteToInt(new byte[] { byteArry[0], byteArry[1] });// 校验位
		int key1 = byteToInt(new byte[] { byteArry[2], byteArry[3] });
		int key2 = byteToInt(new byte[] { byteArry[4], byteArry[5] });
		byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
		byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
		byte[] bytes = new byte[byteArry.length - 6];
		long xy1 = key1 + key2;// 校验位
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ((int) byteArry[i + 6] - (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
			xy1 += bytes[i];
		}
		xy1 = Math.abs(xy1);
		if ((xy1 % 65535) == xy) {
			return bytes;
		} else {
			return new byte[] { -23, -108, -103, -24, -81, -81, -17, -68, -127, -27, -83, -105, -25, -84, -90, -28, -72, -78, -24, -94, -85, -28, -65, -82, -26, -108, -71, -24, -65, -121, -17, -68, -127 };
		}
	}

	// 解密
	public byte[] decode(byte[] byteArry, String passWord) {
		byte[] p = passWord.getBytes();
		int xy = byteToInt(new byte[] { byteArry[0], byteArry[1] });// 校验位
		int key1 = byteToInt(new byte[] { byteArry[2], byteArry[3] });
		int key2 = byteToInt(new byte[] { byteArry[4], byteArry[5] });
		byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
		byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
		byte[] bytes = new byte[byteArry.length - 6];
		long xy1 = key1 + key2;// 校验位
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ((int) byteArry[i + 6] - p[i % p.length] - (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
			xy1 += bytes[i];
		}
		xy1 = Math.abs(xy1);
		if ((xy1 % 65535) == xy) {
			return bytes;
		} else {//密码错误或字符串被修改过！
			return new byte[] { -27,-81,-122,-25,-96,-127,-23,-108,-103,-24,-81,-81,-26,-120,-106,-27,-83,-105,-25,-84,-90,-28,-72,-78,-24,-94,-85,-28,-65,-82,-26,-108,-71,-24,-65,-121,-17,-68,-127};
		}
	}

	// 100个随机数字
	private byte[] m = new byte[] { 0, 8, 5, 1, 6, 1, 4, 4, 1, 0, 1, 9, 9, 0, 0, 5, 2, 5, 0, 7, 3, 3, 7, 3, 2, 1, 7, 0, 0, 4, 0, 1, 0, 2, 0, 8, 0, 1, 6, 2, 0, 4, 2, 0, 2, 0, 2, 5, 0, 6, 9, 0, 8, 3, 0, 3, 4, 8, 4, 3, 0, 9, 5, 0, 4, 3, 4, 0, 0, 0, 8, 0, 0, 5, 2, 5, 0, 5, 5, 0, 0, 9, 6, 2, 0, 4, 6, 0, 6, 0, 8, 7, 0, 1, 1, 9, 0, 7, 0, 7 };

	public byte[] encodeFast(byte[] byteArry) {
		for (int i = 0; i < byteArry.length; i++) {
			byteArry[i] += m[i % (m.length)];
		}
		return byteArry;
	}

	public byte[] decodeFast(byte[] byteArry) {
		for (int i = 0; i < byteArry.length; i++) {
			byteArry[i] -= m[i % (m.length)];
		}
		return byteArry;
	}

	static public class Base64 {
		public static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
		public static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };

		public static String encode(byte[] data) {
			StringBuilder sb = new StringBuilder();
			int len = data.length;
			int i = 0;
			int b1, b2, b3;
			while (i < len) {
				b1 = data[i++] & 0xff;
				if (i == len) {
					sb.append(base64EncodeChars[b1 >>> 2]);
					sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
					sb.append("==");
					break;
				}
				b2 = data[i++] & 0xff;
				if (i == len) {
					sb.append(base64EncodeChars[b1 >>> 2]);
					sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
					sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
					sb.append("=");
					break;
				}
				b3 = data[i++] & 0xff;
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
				sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
				sb.append(base64EncodeChars[b3 & 0x3f]);
			}

			return sb.toString();
		}

		public static byte[] decode(String str) {
			StringBuilder sb = new StringBuilder();
			byte[] data;
			data = str.getBytes(StandardCharsets.US_ASCII);
			int len = data.length;
			int i = 0;
			int b1, b2, b3, b4;
			while (i < len) {
				/* b1 */
				do {
					b1 = base64DecodeChars[data[i++]];
				} while (i < len && b1 == -1);
				if (b1 == -1)
					break;
				/* b2 */
				do {
					b2 = base64DecodeChars[data[i++]];
				} while (i < len && b2 == -1);
				if (b2 == -1)
					break;
				sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
				/* b3 */
				do {
					b3 = data[i++];
					if (b3 == 61)
						return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
					b3 = base64DecodeChars[b3];
				} while (i < len && b3 == -1);
				if (b3 == -1)
					break;
				sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
				/* b4 */
				do {
					b4 = data[i++];
					if (b4 == 61)
						return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
					b4 = base64DecodeChars[b4];
				} while (i < len && b4 == -1);
				if (b4 == -1)
					break;
				sb.append((char) (((b3 & 0x03) << 6) | b4));
			}
			return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
		}
	}
}