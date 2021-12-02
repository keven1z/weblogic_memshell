package cn.com.x1001.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author  keven1z
 * @Date  2021/6/11
 * @Description hook类的信息
*/
public class HookClass implements Cloneable{
    private String className;
    private HashMap<String,String> methodDesc = new HashMap<>();
    private HashSet<String> interfaces = new HashSet<String>();
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodDesc(HashMap<String, String> methodDesc) {
        this.methodDesc = methodDesc;
    }

    public HashMap<String, String> getMethodDesc() {
        return methodDesc;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HookClass hookClass = (HookClass) o;
        return className.equals(hookClass.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }

    public HashSet<String> getInterfaces() {
        return interfaces;
    }

    /**
     * @return 该hook类所有的方法
     */
    public Set<String> getMethods(){
        return this.methodDesc.keySet();
    }
    public void addMethodDesc(String method,String desc){
        this.methodDesc.put(method,desc);
    }

    @Override
    public Object clone(){
        HookClass hookClass = null;
        try {
            hookClass = (HookClass) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return hookClass;
    }
}
