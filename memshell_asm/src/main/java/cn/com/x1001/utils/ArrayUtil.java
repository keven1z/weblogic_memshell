package cn.com.x1001.utils;

public class ArrayUtil {
    public static boolean isContain(String target, String[] targetArray)
    {
        if (targetArray == null || targetArray.length == 0) return false;
        if (target != null)
        {
            String[] tmpArray;
            int i = (tmpArray = targetArray).length;
            for (int j = 0; j < i; j++)
            {
                String tmp = tmpArray[j];
                if (target.contains(tmp)) {
                    return true;
                }
            }
        }
        return false;
    }
}
