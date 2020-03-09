
package com.yujing.crypt.test;

import com.yujing.crypt.YEncrypt;
import com.yujing.crypt.YGzip;

import java.io.File;
import java.util.Scanner;


@SuppressWarnings("resource")
public class Main {
	public static void main(String[] args) {
		while (true) {
			System.out.println("------------欢迎使用余静文件加密压缩系统--------------");
			System.out.println("------请输入以下指令：-------------------------------");
			System.out.println("------文件快速加密：11--------文件快速解密：12--------");
			System.out.println("------文件复杂加密：21--------文件复杂解密：22--------");
			System.out.println("------文件加压：31------------文件解压：32------------");
			System.out.println("------字符串加密：41----------字符串解密：42----------");
			System.out.println("-----------------输入其他则退出----------------------");
			String initString=new Scanner(System.in).next();
			switch (initString) {
				case "11":
					if11();
					break;
				case "12":
					if12();
					break;
				case "21":
					if21();
					break;
				case "22":
					if22();
					break;
				case "31":
					if31();
					break;
				case "32":
					if32();
					break;
				case "41":
					if41();
					break;
				case "42":
					if42();
					break;
				default:
					System.exit(0);
			}
			System.out.println("\n\n");
		}
	}
	private static void if11(){
		try {
			System.out.println("请输入文件路径（或者拖放文件到此处）：");
			String path=new Scanner(System.in).next();
			FileUtil.encodeFast(new File(path), new File(path+".ycryFast"));
		} catch (Exception e) {
			System.out.println("文件路径错误！");
		}
	}
	private static void if12(){
		try {
			System.out.println("请输入文件路径（或者拖放文件到此处）：");
			String path=new Scanner(System.in).next();
			String path2=path.lastIndexOf(".ycryFast")==-1?(path+".copy"):path.substring(0, path.lastIndexOf(".ycryFast"));
			FileUtil.decodeFast(new File(path), new File(path2));
		} catch (Exception e) {
			System.out.println("文件路径错误！");
		}
		
	}
	private static void if21(){
		try {
			System.out.println("请输入文件路径（或者拖放文件到此处）：");
			String path=new Scanner(System.in).next();
			FileUtil.encode(new File(path), new File(path+".ycry"));
		} catch (Exception e) {
			System.out.println("文件路径错误！");
		}
	}
	private static void if22(){
		try {
			System.out.println("请输入文件路径（或者拖放文件到此处）：");
			String path=new Scanner(System.in).next();
			String path2=path.lastIndexOf(".ycry")==-1?(path+".copy"):path.substring(0, path.lastIndexOf(".ycry"));
			FileUtil.decode(new File(path), new File(path2));
		} catch (Exception e) {
			System.out.println("文件路径错误！");
		}
	}
	private static void if31(){
		System.out.println("请输入文件路径（或者拖放文件到此处）：");
		String path=new Scanner(System.in).next();
		try {
		YGzip.compress(new File(path),false);
		} catch (Exception e) {
			System.out.println("文件路径错误！");
		}
	}
	private static void if32(){
		System.out.println("请输入文件路径（或者拖放文件到此处）：");
		String path=new Scanner(System.in).next();
		try {
		YGzip.decompress(new File(path),false);
		} catch (Exception e) {
			System.out.println("文件路径错误！");
		}
	}
	private static void if41(){
		System.out.println("请输入要加密的字符串：");
		String str=new Scanner(System.in).next();
		YEncrypt encrypt=new YEncrypt();
		String str2=encrypt.encode(str);
		System.out.println("加密后结果："+str2);
	}
	private static void if42(){
		try {
			System.out.println("请输入要解密的字符串：");
			String str=new Scanner(System.in).next();
			YEncrypt encrypt=new YEncrypt();
			String str2=encrypt.decode(str);
			System.out.println("解密后结果："+str2);
		} catch (Exception e) {
			System.out.println("输入的不是加密后的字符串！");
		}
	}
}
