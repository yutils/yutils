package com.yujing.utils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Properties工具类
 * 1.key value 都可以是中文
 * 2.注释也可以是中文
 * 3.每一组 key value 都可以有注释
 *
 * @author 余静 2022年8月23日11:33:00
 */
/*用法
java：
//根注释，顶部注释
YPropertiesUtils.rootExplain = "这是一个配置文件";
//提前设置key要匹配的注释内容
YPropertiesUtils.explainMap.put("key1", "第一个普通的key");
YPropertiesUtils.explainMap.put("中文key2", "中文key");
YPropertiesUtils.explainMap.put("ip", "一个ip地址");
YPropertiesUtils.explainMap.put("json", "这是一个对象");
HashMap<String, String> map = new HashMap<>();
map.put("key1", "yujing");
map.put("中文key2", "张三丰");
map.put("ip", "192.168.1.1");
// 创建或修改
String path = "C:/Users/yujing/Desktop/test.properties";
YPropertiesUtils.set(path, map);
YPropertiesUtils.set(path, "中文key2", "张无忌");
YPropertiesUtils.set(path, "json", (new Gson()).toJson(map));
// 取值
System.out.println(YPropertiesUtils.get(path, "中文key2"));
// 取值全部
System.out.println((new Gson()).toJson(YPropertiesUtils.getAll(path)));
//删除
//YPropertiesUtils.remove(path, "name1","password1");
//删除全部
//YPropertiesUtils.removeAll(path);




kotlin：
val path = YPath.get() + "/config.txt"
YPropertiesUtils.rootExplain = "这是一个配置文件"
//提前设置key要匹配的注释内容
YPropertiesUtils.explainMap["key1"] = "第一个普通的key"
YPropertiesUtils.explainMap["中文key2"] = "中文key"
YPropertiesUtils.explainMap["ip"] = "一个ip地址"
YPropertiesUtils.explainMap["json"] = "这是一个对象"
val map: HashMap<String, String> = HashMap()
map["key1"] = "yujing"
map["中文key2"] = "张三丰"
map["ip"] = "192.168.1.1"
// 创建或修改
YPropertiesUtils.set(path, map)
YPropertiesUtils.set(path, "中文key2", "张无忌")
YPropertiesUtils.set(path, "json", Gson().toJson(map))
// 取值
println(YPropertiesUtils.get(path, "中文key2"))
// 取值全部
println(Gson().toJson(YPropertiesUtils.getAll(path)))




效果：(test.properties)
```
#这是一个配置文件
#Tue Aug 23 14:42:23 CST 2022

#第一个普通的key
key1=yujing

#中文key
中文key2=张无忌

#一个ip地址
ip=192.168.1.1

#这是一个对象
json={"key1"\:"yujing","ip"\:"192.168.1.1","中文key2"\:"张三丰"}
```
 */
public class YPropertiesUtils extends Properties {
    /**
     * 根注释，顶部注释
     */
    public static String rootExplain = "config";

    /**
     * 注释，对应的Key，explain
     * 当对应的key的value不为null，就是生成注释
     */
    public static HashMap<String, String> explainMap = new HashMap<>();

    /**
     * 设置多对 KeyValue
     *
     * @param filePath 文件路径
     * @param map      map
     * @return
     */
    public static boolean set(String filePath, HashMap<String, String> map) {
        if (map == null) return false;
        createFile(filePath);
        YPropertiesUtils ppt = loadFile(filePath);
        if (ppt == null) return false;
        ppt.putAll(map);
        return commit(filePath, ppt, rootExplain);
    }

    /**
     * 设置一对 KeyValue
     *
     * @param filePath 文件路径
     * @param key      key
     * @param value    value
     * @return
     */
    public static boolean set(String filePath, String key, String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put(key, value);
        return set(filePath, map);
    }

    /**
     * 获取一个 key值
     *
     * @param filePath 文件路径
     * @param key      key
     * @return
     */
    public static String get(String filePath, String key) {
        Properties ppt = loadFile(filePath);
        return ppt == null ? null : ppt.getProperty(key);
    }

    /**
     * 获取全部 key 和 value
     *
     * @param filePath 文件路径
     * @return
     */
    public static Map<String, String> getAll(String filePath) {
        Properties ppt = loadFile(filePath);
        Map<String, String> map = new HashMap<>();
        if (ppt == null) return map;
        String key;
        String value;
        Enumeration<?> en = ppt.propertyNames();
        if (en == null) return map;
        while (en.hasMoreElements()) {
            key = (String) en.nextElement();
            value = ppt.getProperty(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 删除对应key
     *
     * @param filePath 文件路径
     * @param key      key1，key2
     * @return
     */
    public static boolean remove(String filePath, String... key) {
        if (key == null) return false;
        YPropertiesUtils ppt = loadFile(filePath);
        if (ppt == null) return false;
        for (String item : key)
            ppt.remove(item);
        return commit(filePath, ppt, rootExplain);
    }

    /**
     * 删除对应key
     *
     * @param filePath 文件路径
     * @param key      key
     * @return
     */
    public static boolean remove(String filePath, String key) {
        return remove(filePath, new String[]{key});
    }

    /**
     * 删除全部key
     *
     * @param filePath 文件路径
     * @return
     */
    public static boolean removeAll(String filePath) {
        YPropertiesUtils ppt = loadFile(filePath);
        if (ppt == null) return false;
        ppt.clear();
        return commit(filePath, ppt, rootExplain);
    }

    /**
     * 创建文件，如果文件夹不存在，先创建文件夹
     *
     * @param filePath
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        java.io.File fileParent = file.getParentFile();
        if (!Objects.requireNonNull(fileParent).exists())
            fileParent.mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("(Properties)IOException:" + e.getMessage());
            }
        }
    }

    /**
     * 加载 Properties
     *
     * @param filePath 文件路径
     * @return
     */
    public static YPropertiesUtils loadFile(String filePath) {
        java.io.InputStream is = YPropertiesUtils.class.getResourceAsStream(filePath);
        YPropertiesUtils ppt = new YPropertiesUtils();
        try {
            if (is == null) {
                ppt.load(new InputStreamReader(new java.io.FileInputStream(filePath), StandardCharsets.UTF_8));
                return ppt;
            }
            ppt.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            return ppt;
        } catch (FileNotFoundException e) {
            System.err.println("(Properties)FileNotFoundException: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("(Properties)IOException: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("(Properties)Exception:" + e.getMessage());
            return null;
        }
    }

    /**
     * 提交
     *
     * @param filePath 文件路径
     * @param ppt      Properties
     * @param notes    注释
     * @return
     */
    @SuppressWarnings("ReturnInsideFinallyBlock")
    public static boolean commit(String filePath, YPropertiesUtils ppt, String notes) {
        OutputStreamWriter stream = null;
        try {
            stream = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            try {
                String path = Objects.requireNonNull(YPropertiesUtils.class.getResource(filePath)).getPath();
                stream = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);
            } catch (FileNotFoundException e1) {
                System.err.println("(Properties)FileNotFoundException:" + e1.getMessage());
                return false;
            } catch (Exception e2) {
                System.err.println("(Properties)Exception:" + e2.getMessage());
                return false;
            }
        } finally {
            if (stream == null) return false;
        }
        try {
            ppt.store(stream, notes);
            //ppt.storeToXML(new FileOutputStream(propertyFilePath), NOTES,StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("(Properties)IOException:" + e.getMessage());
            return false;
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    //-----------------------------------重写开始  直到末尾-----------------------------------
    //日期格式
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * 重载
     */
    @Override
    public void store(Writer writer, String comments) throws IOException {
        store0((writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer), comments, false);
    }

    /**
     * 重载因为父类是private，所以这儿只能修改调用该方法的类
     */
    private void store0(BufferedWriter bw, String comments, boolean escUnicode)
            throws IOException {
        if (comments != null) {
            writeComments(bw, comments);
        }
        bw.write("#最后一次修改时间：" + formatter.format(new Date()));
        bw.newLine();
        synchronized (this) {
            for (Map.Entry<Object, Object> e : entrySet()) {
                String key = (String) e.getKey();
                String val = (String) e.getValue();
                key = saveConvert(key, true, escUnicode);
                val = saveConvert(val, false, escUnicode);
                //--------------------余静添加的行 开始--------------------
                String explain = explainMap.get(key);
                if (explain != null) {
                    bw.newLine();
                    bw.write("#" + explain);
                    bw.newLine();
                }
                //--------------------余静添加的行 结束--------------------
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
    }

    /**
     * 重载因为父类是private，所以这儿只能修改调用该方法的类
     */
    private String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder outBuffer = new StringBuilder(bufLen);
        //HexFormat hex = HexFormat.of().withUpperCase();//余静 注释
        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
                        outBuffer.append("\\u");
                        //outBuffer.append(hex.toHexDigits(aChar));//余静注释
                        outBuffer.append(toHex(aChar));//余静添加
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    /**
     * 重载因为父类是private，所以这儿只能修改调用该方法的类
     */
    private static void writeComments(BufferedWriter bw, String comments)
            throws IOException {
        //HexFormat hex = HexFormat.of().withUpperCase();
        bw.write("#");
        int len = comments.length();
        int current = 0;
        int last = 0;
        while (current < len) {
            char c = comments.charAt(current);
            if (c > '\u00ff' || c == '\n' || c == '\r') {
                if (last != current)
                    bw.write(comments.substring(last, current));
                if (c > '\u00ff') {
                    //bw.write("\\u");//余静注释掉的行
                    //bw.write(hex.toHexDigits(c));//余静注释掉的行
                    bw.write(c);//余静添加的行
                } else {
                    bw.newLine();
                    if (c == '\r' && current != len - 1 && comments.charAt(current + 1) == '\n') {
                        current++;
                    }
                    if (current == len - 1 || (comments.charAt(current + 1) != '#' && comments.charAt(current + 1) != '!'))
                        bw.write("#");
                }
                last = current + 1;
            }
            current++;
        }
        if (last != current)
            bw.write(comments.substring(last, current));
        bw.newLine();
    }

    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    private static final char[] hexDigit = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
}
