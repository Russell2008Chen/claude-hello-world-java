package com.cjw.utils;

/**
 * 字符串工具类，提供常用的字符串操作方法
 */
public class StringUtil {

    /**
     * 判断字符串是否为空或仅包含空白字符
     *
     * @param input 输入字符串
     * @return 如果字符串为null或仅包含空白字符则返回true，否则返回false
     */
    public static boolean isBlank(String input) {
        return input == null || input.isBlank();
    }

    /**
     * 判断字符串是否不为空且不仅包含空白字符
     *
     * @param input 输入字符串
     * @return 如果字符串不为null且包含非空白字符则返回true，否则返回false
     */
    public static boolean isNotBlank(String input) {
        return !isBlank(input);
    }

    /**
     * 获取字符串的长度，null字符串返回0
     *
     * @param input 输入字符串
     * @return 字符串长度，null返回0
     */
    public static int length(String input) {
        return input == null ? 0 : input.length();
    }

    /**
     * 将字符串转换为大写，null字符串返回null
     *
     * @param input 输入字符串
     * @return 大写字符串，null返回null
     */
    public static String toUpperCase(String input) {
        return input == null ? null : input.toUpperCase();
    }

    /**
     * 将字符串转换为小写，null字符串返回null
     *
     * @param input 输入字符串
     * @return 小写字符串，null返回null
     */
    public static String toLowerCase(String input) {
        return input == null ? null : input.toLowerCase();
    }

    /**
     * 合并两个字符串
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 合并后的字符串，如果两个字符串都为null则返回null
     */
    public static String merge(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return null;
        }
        return (str1 == null ? "" : str1) + (str2 == null ? "" : str2);
    }
}
