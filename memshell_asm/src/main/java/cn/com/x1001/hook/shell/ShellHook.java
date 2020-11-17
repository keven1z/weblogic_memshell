package cn.com.x1001.hook.shell;

import cn.com.x1001.CodeClassHook;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ShellHook extends CodeClassHook {


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions, MethodVisitor mv) {
        if ("service".equalsIgnoreCase(name)) {
            return new ShellAdviceAdapter(Opcodes.ASM5, mv, access, name, desc);
        }
        return mv;
    }

}
