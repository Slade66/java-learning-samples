package com.slade66.string;

import org.junit.jupiter.api.Test;

/**
 * String 类 intern() 方法的练习题。
 */
public class InternTests {

    @Test
    void test1() {
        System.out.println("--- Test 1 ---");
        String s1 = "Apple";
        String s2 = new String("Apple");
        System.out.println("s1 == s2: " + (s1 == s2));
    }

    @Test
    void test2() {
        System.out.println("--- Test 2 ---");
        String s1 = "Banana";
        String s2 = new String("Banana");
        String s3 = s2.intern();
        System.out.println("s1 == s3: " + (s1 == s3));
    }

    @Test
    void test3() {
        System.out.println("--- Test 3 ---");
        String s1 = "Cherry";
        String s2 = new String("Cherry");
        String s3 = s2.intern();
        System.out.println("s2 == s3: " + (s2 == s3));
        System.out.println("s1 == s3: " + (s1 == s3));
    }

    @Test
    void test4() {
        System.out.println("--- Test 4 ---");
        String s1 = new String("DatePalm");
        String s2 = s1.intern();
        String s3 = "DatePalm";
        System.out.println("s1 == s2: " + (s1 == s2));
        System.out.println("s2 == s3: " + (s2 == s3));
        System.out.println("s1 == s3: " + (s1 == s3));
    }

    @Test
    void test5() {
        System.out.println("--- Test 5 ---");
        String s1 = "Grape" + "Fruit";
        String s2 = "GrapeFruit";
        System.out.println("s1 == s2: " + (s1 == s2));
    }

    @Test
    void test6() {
        System.out.println("--- Test 6 ---");
        String s1 = "Honey";
        String s2 = "Dew";
        String s3 = s1 + s2;
        String s4 = "HoneyDew";
        System.out.println("s3 == s4: " + (s3 == s4));
    }

    @Test
    void test7() {
        System.out.println("--- Test 7 ---");
        String s1 = "Ice";
        String s2 = "Cream";
        String s3 = s1 + s2;
        String s4 = "IceCream";
        String s5 = s3.intern();
        System.out.println("s4 == s5: " + (s4 == s5));
        System.out.println("s3 == s5: " + (s3 == s5));
    }

    @Test
    void test8() {
        System.out.println("--- Test 8 ---");
        final String s1 = "Jack";
        final String s2 = "Fruit";
        String s3 = s1 + s2;
        String s4 = "JackFruit";
        System.out.println("s3 == s4: " + (s3 == s4));
    }

    @Test
    void test9() {
        System.out.println("--- Test 9 ---");
        String s1 = "ProgrammingJava";
        String s2 = s1.substring(11);
        String s3 = "Java";
        String s4 = new String("Java");
        String s5 = s2.intern();
        System.out.println("s2 == s3: " + (s2 == s3));
        System.out.println("s2 == s4: " + (s2 == s4));
        System.out.println("s5 == s3: " + (s5 == s3));
        System.out.println("s2 == s5: " + (s2 == s5));
        System.out.println("s4 == s5: " + (s4 == s5));
    }

    @Test
    void test10() {
        System.out.println("--- Test 10 ---");
        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append("World");
        String s1 = sb.toString();
        String s2 = "HelloWorld";
        String s3 = s1.intern();
        System.out.println("s1 == s2: " + (s1 == s2));
        System.out.println("s3 == s2: " + (s3 == s2));
        System.out.println("s1 == s3: " + (s1 == s3));
    }

}
