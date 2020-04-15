
package com.yujing.crypt;

import com.yujing.crypt.test.Show;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP工具
 * @author yujing  2019年8月27日16:57:26
 */
@SuppressWarnings("unused")
public class YGzip {

    public static final int BUFFER = 16384;
    public static final String EXT = ".gz";

    /**
     * 数据压缩
     *
     * @param data 数据
     * @throws Exception 异常
     * @return 压缩后的数据
     */
    public static byte[] compress(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 压缩
        compress(bais, baos);
        byte[] output = baos.toByteArray();
        baos.flush();
        baos.close();
        bais.close();
        return output;
    }

    /**
     * 文件压缩
     *
     * @param file   文件
     * @param delete 是否删除文件
     * @throws Exception 异常
     */
    public static void compress(File file, boolean delete) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(file.getPath() + EXT);
        compress(fis, fos);
        fis.close();
        fos.flush();
        fos.close();
        if (delete) {
            file.delete();
        }
    }

    /**
     * 数据压缩
     *
     * @param is 输入流
     * @param os 输出流
     */
    public static void compress(InputStream is, OutputStream os) {
        Show show = null;
        try {
            GZIPOutputStream gos = new GZIPOutputStream(os);
            show = new Show(is.available());
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                if (show.isShow()) show.set(is.available());
                gos.write(data, 0, count);
            }
            show.finish();
            gos.finish();
            gos.flush();
            gos.close();
        } catch (IOException e) {
            System.out.println("Error");
            if (show != null) {
                show.stopShow();
            }
        }
    }

    /**
     * 数据解压缩
     *
     * @param data 数据
     * @return 结果
     * @throws Exception 异常
     */
    public static byte[] decompress(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 解压缩
        decompress(bais, baos);
        data = baos.toByteArray();
        baos.flush();
        baos.close();
        bais.close();
        return data;
    }

    /**
     * 文件解压缩
     *
     * @param file   文件
     * @param delete 是否删除原始文件
     * @throws Exception 异常
     */
    public static void decompress(File file, boolean delete) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        String path = file.getPath().lastIndexOf(".gz") == -1 ? (file.getPath() + ".copy") : file.getPath().substring(0, file.getPath().lastIndexOf(".gz"));
        FileOutputStream fos = new FileOutputStream(new File(path));
        decompress(fis, fos);
        fis.close();
        fos.flush();
        fos.close();
        if (delete) {
            file.delete();
        }
    }

    /**
     * 数据解压缩
     *
     * @param is 输入流
     * @param os 输出流
     */
    public static void decompress(final InputStream is, OutputStream os) {
        Show show = null;
        try {
            GZIPInputStream gis = new GZIPInputStream(is);
            show = new Show(is.available());
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                if (show.isShow()) show.set(is.available());
                os.write(data, 0, count);
            }
            show.finish();
            gis.close();
        } catch (IOException e) {
            System.out.println("Error,或许文件并未加压");
            if (show != null) {
                show.stopShow();
            }
        }
    }
}
