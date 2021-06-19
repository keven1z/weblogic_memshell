package cn.com.x1001.hook;


import cn.com.x1001.Agent;
import cn.com.x1001.bean.ClassInfo;
import cn.com.x1001.bean.InstrumentationContext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;


public class HookTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (null == className) {
            return classfileBuffer;
        }
        InstrumentationContext context = Agent.context;
        ClassReader classReader = new ClassReader(classfileBuffer);
        String[] interfaces = classReader.getInterfaces();
        String superName = classReader.getSuperName();
        HashSet<String> hashSet = buildAncestors(className, superName, interfaces);
        if (!context.isHookPoint(className,hashSet)) return classfileBuffer;

        ClassInfo thisHookPoint = context.getThisHookPoint(className);
        /*
            如果本身不是hook点，则生成ClassInfo加入已hook set中
         */
        if (thisHookPoint == null) {
            thisHookPoint = new ClassInfo();
            thisHookPoint.setClassName(className);
            //填充父类或接口的方法描述
            ClassInfo ancestorHookPoint = context.getAncestorHookPoint(hashSet);
            if (ancestorHookPoint != null) thisHookPoint.setMethodDesc(ancestorHookPoint.getMethodDesc());
        }

        thisHookPoint.setAncestors(hashSet);
        thisHookPoint.setInterfaces(new HashSet<String>(Arrays.asList(interfaces)));
        if (superName != null && !superName.equalsIgnoreCase("java/lang/Object")) {
            thisHookPoint.setSuperClassName(superName);
        }
        thisHookPoint.setAccess(classReader.getAccess());
        /*
         * 如果是接口，不进入修改代码
         */
        if (isInterface(classReader.getAccess())) return classfileBuffer;

        /*
         * 将进入hook点的代码添加到hook点中
         */
        Agent.context.addToHookSet(thisHookPoint);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        CodeClassVisitor codeClassVisitor = new CodeClassVisitor(classWriter, thisHookPoint);
        classReader.accept(codeClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    /**
     * @param className 当前hook类
     * @param superName 当前hook类的父类
     * @param interfaces 当前hook类的接口类
     */
    private HashSet<String> buildAncestors(String className, String superName, String[] interfaces) {

        HashSet<String> ancestors = new HashSet<>();
        if (interfaces.length > 0) {
            ancestors.addAll(Arrays.asList(interfaces));
        }
        if (superName != null && !superName.equalsIgnoreCase("java/lang/Object")) {
            ancestors.add(superName);
        }
        return ancestors;
    }

    /**
     * @param access
     * @return true 表明该类为接口类
     */
    private boolean isInterface(int access) {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

}
