package cn.com.x1001.utils;

import java.util.HashSet;

public class StringUtil {
    public static boolean isContainString(String str, HashSet<String> hashSet) {
        if (str == null || str.length() == 0) return false;
        if (hashSet == null || hashSet.size() == 0) return false;
        for (String element : hashSet) {
            if (element.equals(str)) return true;
        }
        return false;
    }

    public static boolean isContainString(String str, String[] arr) {
        if (str == null || str.length() == 0) return false;
        if (arr == null || arr.length == 0) return false;
        for (String element : arr) {
            if (element.equals(str)) return true;
        }
        return false;
    }
}
