package cn.com.x1001.utils;

import java.util.HashSet;

public class StringUtil {
    public static boolean isContainString(String str, String[] array) {
        if (str == null || str.length() == 0) return false;
        if (array == null || array.length == 0) return false;
        for (String element : array) {
            if (element.equals(str)) return true;
        }
        return false;
    }
}
