package cn.com.x1001.http;

import cn.com.x1001.utils.Reflection;

import java.io.PrintWriter;

public class HttpResponse {
    private Object response;
    protected static final Class[] EMPTY_CLASS = new Class[]{};
    protected static final Class[] STRING_CLASS = new Class[]{String.class};
    protected static final Class[] INT_CLASS = new Class[]{Integer.class};
    public HttpResponse(Object response) {
        this.response = response;
    }

    public Object getRequest() {
        try {
            return Reflection.getField(response,"request");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //
        }
        return null;
//        return Reflection.invokeMethod(response, "getRequest", EMPTY_CLASS);
    }

    public void write(String value) {

        Object writer = Reflection.invokeMethod(response, "getWriterNoCheck", EMPTY_CLASS);
        if (writer == null) {
            writer = Reflection.invokeMethod(response, "getOutputStream", EMPTY_CLASS);
        }
        Reflection.invokeMethod(response, "disableKeepAliveOnSendError", EMPTY_CLASS);
        Reflection.invokeMethod(response, "setContentType", STRING_CLASS,"text/html");
        Reflection.invokeMethod(response, "setContentLength", INT_CLASS,value.length());
        Reflection.invokeMethod(response, "setCharacterEncoding", STRING_CLASS,"UTF-8");
        Reflection.invokeMethod(writer, "print", new Class[]{String.class}, value);
        Reflection.invokeMethod(writer, "flush", new Class[]{String.class});
        Reflection.invokeMethod(response, "send", EMPTY_CLASS);

    }
}
