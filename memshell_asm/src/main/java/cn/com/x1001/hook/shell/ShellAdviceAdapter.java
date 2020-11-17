package cn.com.x1001.hook.shell;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public class ShellAdviceAdapter extends AdviceAdapter {
    private String className;

    /**
     * Creates a new {@link AdviceAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link Type Type}).
     */
    protected ShellAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc);
    }


    @Override
    protected void onMethodEnter() {
        Type type = Type.getType(ShellChecker.class);
        Method method = new Method("check", "([Ljava/lang/Object;Ljava/lang/Object;)V");
        //push所有传入参数
        loadArgArray();
        loadThis();
        invokeStatic(type,method);
    }
}
