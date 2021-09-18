package com.yujing.utils;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 计算器
 *
 * @author 余静 2018年5月15日19:00:17
 */
/*用法
public static void main(String[] args) {
    String Str = "-2-3";
    Replace r = new Replace(Str);
    r.put("a", 5);
    r.put("b", "2");
    r.put("x", 3);

    System.out.println("替换变量前：" + Str);
    System.out.println("替换变量后：" + r.toString());
    System.out.println("计算结果：" + YCalc.eval(r.toString()));
}
 */
@SuppressWarnings("WeakerAccess")
public class YCalc {
    private final static String ERR_NOT_END_VALID = "你表达的最后一个字符必须是SymbolEnd!";
    private final static String ERR_PARENTHESES_NOT_PAIR = "括号不配对!";
    private final static String ERR_CHAR_NOT_SUPPORT = "不支持的字符";
    private final static String ERR_OPERATION_NOT_SUPPORTED = "不支持的操作！";
    private final static String ERR_OPERATOR_NOT_VALID = "取余数不支持double数据！";
    private final static String ERR_UNKNOWN = "未知错误！";
    // ---------------------------------定义符号开始---------------------------
    private final static char SymbolAdd = '+';// 加号
    private final static char SymbolSub = '-';// 减号
    private final static char SymbolMultiply = '*';// 乘号
    private final static char SymbolDivided = '/';// 除号
    private final static char SymbolRemainder = '%';// 取余
    private final static char SymbolPoint = '.';// 小数点
    private final static char SymbolLeftParenthesis = '(';// 左括号
    private final static char SymbolRightParenthesis = ')';// 右括号
    private final static char SymbolEnd = '#';// 结束标志
    // ---------------------------------定义符号完毕---------------------------

    // 传入字符串表达式如："1-(2*2)+6"
    public static String eval(String expression) {
        expression = expression.replace(" ", "");// 去掉全部空格
        expression = expression.replace("(-", "(0-");// 去掉全部空格
        if (expression.charAt(0) == SymbolSub) {
            expression = "0" + expression;
        }
        expression += String.valueOf(SymbolEnd);// #为结束标记
        ArrayList<String> list;
        try {
            list = toSuffixSequence(expression);
            return calculate(list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 计算类，在栈中取出
    private static String calculate(ArrayList<String> list) throws Exception {
        Stack<String> stack = new Stack<>();// 栈
        for (String s : list) {// 遍历list
            if (isOperator(s)) {// 如果是运算符+-*/%
                String d1 = stack.pop();// 将栈中当前元素出栈
                String d2 = stack.pop();// 将栈中当前元素出栈
                String res = doCalc(d2, d1, s);
                stack.push(res);
            } else
                stack.push(s);
        }
        if (stack.size() == 1)
            return stack.pop();
        else
            throw new Exception(ERR_UNKNOWN);
    }

    private static String doCalc(String d1, String d2, String oper) throws Exception {
        if (oper != null && oper.length() > 1)
            throw new Exception(ERR_OPERATION_NOT_SUPPORTED + ":'" + oper + "'");
        boolean isDouble = isDoubleNeeded(d1, d2, oper);
        if (oper == null) {
            throw new Exception(ERR_UNKNOWN);
        }
        switch (oper.charAt(0)) {
            case SymbolAdd:
                if (isDouble)
                    return Double.toString(Double.parseDouble(d1) + Double.parseDouble(d2));
                else
                    return Integer.toString(Integer.parseInt(d1) + Integer.parseInt(d2));
            case SymbolSub:
                if (isDouble)
                    return Double.toString(Double.parseDouble(d1) - Double.parseDouble(d2));
                else
                    return Integer.toString(Integer.parseInt(d1) - Integer.parseInt(d2));
            case SymbolMultiply:
                if (isDouble)
                    return Double.toString(Double.parseDouble(d1) * Double.parseDouble(d2));
                else
                    return Integer.toString(Integer.parseInt(d1) * Integer.parseInt(d2));
            case SymbolDivided:
                if (isDouble)
                    return Double.toString(Double.parseDouble(d1) / Double.parseDouble(d2));
                else
                    return Integer.toString(Integer.parseInt(d1) / Integer.parseInt(d2));
            case SymbolRemainder:
                if (isDouble)
                    throw new Exception(ERR_OPERATOR_NOT_VALID);
                else
                    return Integer.toString(Integer.parseInt(d1) % Integer.parseInt(d2));
            default:
                throw new Exception(ERR_OPERATION_NOT_SUPPORTED + ":'" + oper + "'");
        }
    }

    private static boolean isDoubleNeeded(String d1, String d2, String oper) {
        if (d1.contains(String.valueOf(SymbolPoint)) || d2.contains(String.valueOf(SymbolPoint)))
            return true;
        if (oper != null && oper.equals(String.valueOf(SymbolDivided))) {
            int left = Integer.parseInt(d1) % Integer.parseInt(d2);
            return left != 0;
        }
        return false;
    }

    private static boolean isOperator(String str) {
        return str != null && (str.equals(String.valueOf(SymbolAdd)) || str.equals(String.valueOf(SymbolSub))
                || str.equals(String.valueOf(SymbolMultiply)) || str.equals(String.valueOf(SymbolDivided))
                || str.equals(String.valueOf(SymbolRemainder)));
    }

    // 产生准备计算的list放入栈
    private static ArrayList<String> toSuffixSequence(String expression) throws Exception {
        if (!expression.endsWith(String.valueOf(SymbolEnd)))
            throw new Exception(ERR_NOT_END_VALID);
        ArrayList<String> list = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        stack.push(String.valueOf(SymbolEnd));// 作为标记，压入栈
        char last, ch;
        StringBuffer sb;
        for (int i = 0; i < expression.length(); i++) {
            ch = expression.charAt(i);
            switch (ch) {
                case SymbolAdd:
                case SymbolSub:
                case SymbolMultiply:
                case SymbolDivided:
                case SymbolRemainder:
                    last = stack.peek().charAt(0);// peek()查看栈顶对象而不移除它
                    if (last != SymbolLeftParenthesis && priority(last) >= priority(ch))
                        list.add(stack.pop());
                    stack.push(String.valueOf(ch));
                    break;
                case SymbolLeftParenthesis:
                    stack.push(String.valueOf(SymbolLeftParenthesis));
                    break;
                case SymbolRightParenthesis:
                    while (!stack.isEmpty() && stack.peek().charAt(0) != SymbolLeftParenthesis)
                        list.add(stack.pop());
                    if (stack.isEmpty() || stack.size() == 1)
                        throw new Exception("右括号错误，" + ERR_PARENTHESES_NOT_PAIR);
                    stack.pop();
                    break;
                case SymbolEnd:
                    while (stack.size() > 1 && stack.peek().charAt(0) != SymbolLeftParenthesis)
                        list.add(stack.pop());
                    if (stack.size() > 1)
                        throw new Exception("左括号错误， " + ERR_PARENTHESES_NOT_PAIR);
                    break;
                default:
                    if (Character.isDigit(ch) || SymbolPoint == ch) {
                        sb = new StringBuffer();
                        sb.append(ch);
                        while (Character.isDigit(expression.charAt(i + 1)) || expression.charAt(i + 1) == SymbolPoint)
                            sb.append(expression.charAt(++i));
                        list.add(sb.toString());
                        break;
                    } else
                        throw new Exception(ERR_CHAR_NOT_SUPPORT + ":'" + ch + "'");
            }
        }
        return list;
    }

    private static int priority(char ch) {
        switch (ch) {
            case SymbolAdd:
            case SymbolSub:
                return 1;
            case SymbolMultiply:
            case SymbolDivided:
            case SymbolRemainder:
                return 2;
            case SymbolEnd:
                return 0;
            default:
                return 0;
        }
    }

    /**
     * 替换类
     *
     * @author 余静
     */
    @SuppressWarnings("WeakerAccess")
    public static class Replace {
        String data;

        public Replace(String data) {
            this.data = data;
        }

        public void put(String key, Object value) {
            data = data.replace(key, value.toString());
        }

        @NonNull
        public String toString() {
            return data;
        }
    }
}