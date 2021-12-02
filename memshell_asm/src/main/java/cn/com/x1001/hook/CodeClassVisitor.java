package cn.com.x1001.hook;

import cn.com.x1001.bean.HookClass;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;

/**
 * @author  keven1z
 * @Date  2021/6/17
 * @Description 类访问对象
*/
public class CodeClassVisitor extends ClassVisitor {

    private HookClass hookClass;

    public CodeClassVisitor(ClassVisitor classVisitor, HookClass hookClass) {
        super(Opcodes.ASM5, classVisitor);
//        System.out.println("Hook class:"+hookClass.getClassName());
        this.hookClass = hookClass;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        Set<String> methods = hookClass.getMethods();
        MethodVisitor localMethodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (methods.contains(name))
            return new HookAdviceAdapter(Opcodes.ASM5, localMethodVisitor, access, name, desc);
        return localMethodVisitor;
    }

}
