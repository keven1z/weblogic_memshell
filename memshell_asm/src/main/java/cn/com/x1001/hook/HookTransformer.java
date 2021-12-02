package cn.com.x1001.hook;


import cn.com.x1001.Agent;
import cn.com.x1001.bean.HookClass;
import cn.com.x1001.bean.InstrumentationContext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class HookTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (null == className) {
            return classfileBuffer;
        }

        ClassReader classReader = new ClassReader(classfileBuffer);
        if (isInterface(classReader.getAccess())) return classfileBuffer;

        InstrumentationContext context = Agent.context;
        String[] interfaces = classReader.getInterfaces();
        HookClass hookClass = context.getHookPoint(className, interfaces);
        if (hookClass == null) return classfileBuffer;

        HookClass realHookClass = (HookClass) hookClass.clone();
        realHookClass.setClassName(className);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        CodeClassVisitor codeClassVisitor = new CodeClassVisitor(classWriter, realHookClass);
        classReader.accept(codeClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }


    /**
     * @param access
     * @return true 表明该类为接口类
     */
    private boolean isInterface(int access) {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

}
