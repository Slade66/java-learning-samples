package com.slade66;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LambdaAndMethodReferenceTest {

    /**
     * 题目1：集合排序与过滤
     * 要求：
     * 1. 给定一个字符串列表，使用 Lambda 表达式对集合进行排序，要求按字符串长度从小到大排序。
     * 2. 排序后，利用集合自身的方法（如 removeIf 或者使用传统循环）过滤掉长度小于 5 的字符串。
     * 3. 使用方法引用（例如 System.out::println）将剩余的字符串打印到控制台。
     */
    @Test
    public void exam1() {
        List<String> data = new ArrayList<>(Arrays.asList(
                "apple", "kiwi", "banana", "orange", "mango", "fig", "grape"
        ));
        data.sort((str1, str2) -> str1.length() - str2.length());
        data.removeIf(str -> str.length() < 5);
        data.forEach(System.out::println);
    }

    /**
     * 题目2：自定义函数式接口与 Lambda
     * 要求：
     * 1. 定义一个函数式接口 StringProcessor，包含一个处理字符串的方法（例如将字符串转换为大写）。
     * 2. 编写一个方法，接收 StringProcessor 实例和一个字符串数组或列表，对每个字符串调用该接口的方法，并返回处理后的结果。
     * 3. 分别使用 Lambda 表达式和方法引用实现该接口，然后调用你编写的方法，比较效果是否一致。
     */
    @Test
    public void exam2() {
        String[] data = {"hello", "world", "java", "lambda", "exercise"};
        useStringProcessor(String::toUpperCase, data).forEach(System.out::println);
    }

    @FunctionalInterface
    interface StringProcessor {
        String process(String str);
    }

    List<String> useStringProcessor(StringProcessor sp, String[] strs) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < strs.length; i++) {
            result.add(sp.process(strs[i]));
        }
        return result;
    }

    /**
     * 题目3：组合多个函数的处理
     * 要求：
     * 1. 定义两个函数式接口：一个用于字符串修剪（去除首尾空格），另一个用于字符串格式化（例如在字符串两侧添加固定字符）。
     * 2. 分别使用 Lambda 表达式实现这两个接口。
     * 3. 手动组合两个函数的调用：给定一个带有前后空格的字符串，先调用修剪函数，再调用格式化函数，最后打印出处理结果。
     */
    @Test
    public void exam3() {
        String data = "   Hello Java   ";
        TrimString trimString = String::trim;
        FormatString formatString = (str, leftChar, rightChar) -> leftChar + str + rightChar;
        System.out.println(formatString.format(trimString.trim(data), 'a', 'b'));
    }

    interface TrimString {
        String trim(String str);
    }

    interface FormatString {
        String format(String str, char leftChar, char rightChar);
    }

}
