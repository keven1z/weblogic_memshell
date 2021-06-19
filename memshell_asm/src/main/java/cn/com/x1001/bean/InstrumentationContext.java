package cn.com.x1001.bean;


import cn.com.x1001.utils.StringUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author keven1z
 * @Date 2021/6/17
 * @Description 存储hook 的上下文信息
 */
public class InstrumentationContext {
    /*
     * 存储待hook的接口或类
     */
    private static Set<ClassInfo> classHashSet = new HashSet<ClassInfo>();
    /*
     * 存储已hook的类
     */
    private static Set<ClassInfo> hasHookedClassSet = new HashSet<ClassInfo>();

    public Set<ClassInfo> getClassHashSet() {
        return classHashSet;
    }

    public void addToHookSet(ClassInfo classInfo) {
        hasHookedClassSet.add(classInfo);
    }

    /**
     * 是否为hook点，以下三种情况判断为hook点
     * 1. 待hook的class为预先定义的hook点的classname
     * 2. 待hook的class在预先定义的hook点的父类或者接口中
     * 3. 待hook的接口类为预先定义的hook点的className
     */
    public boolean isHookPoint(String className, HashSet<String> ancestors) {
        for (ClassInfo classInfo : classHashSet) {
            if (classInfo.getClassName().equals(className) || StringUtil.isContainString(className, classInfo.getAncestors()) || StringUtil.isContainString(classInfo.getClassName(), ancestors)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前hook点的ClassInfo对象
     */
    public ClassInfo getThisHookPoint(String className) {
        for (ClassInfo classInfo : classHashSet) {
            if (classInfo.getClassName().equals(className)) {
                return classInfo;
            }
        }
        return null;
    }

    public boolean isExistClass(String className) {
        for (ClassInfo classInfo : classHashSet) {
            if (classInfo.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExistClass(Class[] interfaces) {
        if (interfaces == null || interfaces.length == 0) return false;
        for (Class clazz : interfaces) {
            if (isExistClass(clazz.getName().replace(".", "/"))) return true;
        }
        return false;
    }

    public ClassInfo getAncestorHookPoint(HashSet<String> ancestors) {
        for (String ancestor : ancestors) {
            return getThisHookPoint(ancestor);
        }
        return null;
    }
}
