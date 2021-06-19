package cn.com.x1001.hook;

import cn.com.x1001.bean.ClassInfo;
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

    private ClassInfo classInfo;

    public CodeClassVisitor(ClassVisitor classVisitor, ClassInfo classInfo) {
        super(Opcodes.ASM5, classVisitor);
        this.classInfo = classInfo;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        Set<String> methods = classInfo.getMethods();
        MethodVisitor localMethodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (methods.contains(name))
            return new HookAdviceAdapter(Opcodes.ASM5, localMethodVisitor, access, name, desc);
        return localMethodVisitor;
    }

}
