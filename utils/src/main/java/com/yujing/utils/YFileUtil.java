package com.yujing.utils;

import android.content.Context;

import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件工具类
 *
 * @author 余静 2018年5月15日19:00:17
 */
@SuppressWarnings({"WeakerAccess", "unused", "ResultOfMethodCallIgnored"})
public class YFileUtil {
    /**
     * string转file
     *
     * @param file 文件
     * @param str  要转换的string
     */
    public static void stringToFile(File file, String str) {
        byteToFile(file, str.getBytes());
    }

    /**
     * string转file
     *
     * @param file    文件
     * @param str     要转换的String
     * @param charset 字符集
     */
    public static void stringToFile(File file, String str, Charset charset) {
        byteToFile(file, str.getBytes(charset));
    }

    /**
     * file转string
     *
     * @param file 文件
     * @return 转出后的String
     */
    public static String fileToString(File file) {
        byte[] data = fileToByte(file);
        if (data == null)
            return null;
        return new String(data);
    }

    /**
     * bytes转file
     *
     * @param bytes byte[]
     * @param file  文件
     * @return 是否成功
     */
    public static boolean byteToFile(File file, byte[] bytes) {
        if (!Objects.requireNonNull(file.getParentFile()).exists()) // 如果位置不存在
            file.getParentFile().mkdirs();
        if (file.exists())
            file.delete();
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            out.write(bytes);
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

    /**
     * file转bytes
     *
     * @param file 文件
     * @return 结果byte[]
     */
    public static byte[] fileToByte(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try (FileInputStream stream = new FileInputStream(file); ByteArrayOutputStream out = new ByteArrayOutputStream((int) file.length())) {
            byte[] b = new byte[1024 * 4];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            return out.toByteArray();
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 添加字符串到文件末尾
     *
     * @param file 文件
     * @param str  字符串
     * @return 是否成功
     */
    public static boolean addStringToFile(File file, String str) {
        return addByteToFile(file, str.getBytes());
    }

    /**
     * 添加bytes到文件末尾
     *
     * @param file  文件
     * @param bytes byte[]
     * @return 是否成功
     */
    public static boolean addByteToFile(File file, byte[] bytes) {
        try {
            if (!Objects.requireNonNull(file.getParentFile()).exists()) // 如果位置不存在
                file.getParentFile().mkdirs();
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.write(bytes);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 删除文件或文件夹
     *
     * @param filePath 文件夹路径
     * @return 若删除成功，则返回True；反之，则返回False
     */
    public static boolean delFile(String filePath) {
        return delFile(new File(filePath));
    }

    /**
     * 删除文件或文件夹
     *
     * @param file 文件或文件夹
     * @return 若删除成功，则返回True；反之，则返回False
     */
    public static boolean delFile(File file) {
        if (file.isFile()) {
            return file.delete();
        } else if (file.isDirectory()) {
            if (Objects.requireNonNull(file.listFiles()).length == 0) {
                return file.delete();
            } else {
                int zFiles = Objects.requireNonNull(file.listFiles()).length;
                File[] delFile2 = file.listFiles();
                for (int i = 0; i < zFiles; i++) {
                    if (Objects.requireNonNull(delFile2)[i].isDirectory()) {
                        delFile(delFile2[i].getAbsolutePath());
                    }
                    delFile2[i].delete();
                }
                return file.delete();
            }
        } else {
            return false;
        }
    }

    /**
     * 复制文件/文件夹 若要进行文件夹复制，请勿将目标文件夹置于源文件夹中
     *
     * @param source   源文件（夹）
     * @param target   目标文件（夹）
     * @param isFolder 若进行文件夹复制，则为True；反之为False
     * @throws IOException IOException
     */
    public static void copy(String source, String target, boolean isFolder) throws IOException {
        if (isFolder) {
            new File(target).mkdirs();
            File a = new File(source);
            String[] file = a.list();
            File temp;
            for (String aFile : Objects.requireNonNull(file)) {
                if (source.endsWith(File.separator)) {
                    temp = new File(source + aFile);
                } else {
                    temp = new File(source + File.separator + aFile);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(target + File.separator + temp.getName());
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    copy(source + File.separator + aFile, target + File.separator + aFile, true);
                }
            }
        } else {
            File oldFile = new File(source);
            if (oldFile.exists()) {
                InputStream inputStream = new FileInputStream(source);
                File file = new File(target);
                Objects.requireNonNull(file.getParentFile()).mkdirs();
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int byteRead;
                while ((byteRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, byteRead);
                }
                inputStream.close();
                outputStream.close();
            }
        }
    }

    /**
     * 获取文件夹下全部文件
     *
     * @param dir 读取的文件夹
     * @return 文件列表
     */
    public static List<File> getFileAll(File dir) {
        final List<File> fileAll = new ArrayList<>();
        getFileAll(dir, file -> {
            if (!file.isDirectory())
                fileAll.add(file);
            return false;
        });
        return fileAll;
    }

    /**
     * 递归获取dir文件夹下全部文件
     *
     * @param dir        读取的文件夹
     * @param fileFilter 回调读取到的每一个文件或者文件夹
     */
    public static void getFileAll(File dir, final FileFilter fileFilter) {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory())
                    file.listFiles(this);
                fileFilter.accept(file);
                return false;
            }
        };
        dir.listFiles(filter);
    }

    /**
     * 读取RAW文件夹下文件
     *
     * @param context  上下文对象
     * @param resource RawResource
     * @return InputStream
     */
    public static InputStream readRaw(Context context, @RawRes int resource) {
        return context.getResources().openRawResource(resource);
    }

    public static InputStream readRaw(@RawRes int resource) {
        return readRaw(YApp.get(), resource);
    }

    /**
     * 读取Assets下面的文件
     *
     * @param context  上下文对象
     * @param fileName 文件完整路径名
     * @return InputStream
     */
    public static InputStream readAssets(Context context, String fileName) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(fileName);
        } catch (IOException e) {
            YLog.e("readAssets", "异常", e);
        }
        return is;
    }

    public static InputStream readAssets(String fileName) {
        return readAssets(YApp.get(), fileName);
    }

    /**
     * 复制Assets文件夹到指定文件夹，如果assetDir为"",那么复制整个assets文件夹
     * 递归复制，如果文件名称不包含"."视为文件夹
     *
     * @param context  context
     * @param assetDir assetDir
     * @param dir      指定文件夹
     */
    private void CopyAssets(Context context, String assetDir, String dir) {
        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }
        for (int i = 0; i < files.length; i++) {
            try {
                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets(context, fileName, dir + fileName + "/");
                    } else {
                        CopyAssets(context, assetDir + "/" + fileName, dir + "/" + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
                    outFile.delete();
                InputStream in;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void CopyAssets(String assetDir, String dir) {
        CopyAssets(YApp.get(), assetDir, dir);
    }
}
