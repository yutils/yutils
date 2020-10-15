package com.yujing.test;

public class Test {
    void test() {
        String a = "";
        a = a.replace("*", "_")
                .replace("#", "_")
                .replace(":", "_")
                .replace("?", "_")
                .replace("/", "_")
                .replace("\\", "_")
                .replace("|", "_")
                .replace("<", "_")
                .replace(">", "_")
                .replace(" ", "_");
    }
}
