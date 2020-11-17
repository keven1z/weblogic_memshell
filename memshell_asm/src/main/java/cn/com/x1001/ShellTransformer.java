package cn.com.x1001;


import cn.com.x1001.hook.shell.ShellHook;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class ShellTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        }
//        System.out.println(className);
        if (Agent.KEY_CLASS.equals(className)) {
            try {
                return doTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            } catch (Exception e) {
                return classfileBuffer;
            }
        }
        return classfileBuffer;

    }

    private byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return new ShellHook().transformClass(classfileBuffer);
    }


}
