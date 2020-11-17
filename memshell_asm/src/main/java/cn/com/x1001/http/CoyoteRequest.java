package cn.com.x1001.http;


import cn.com.x1001.utils.Reflection;

public class CoyoteRequest extends AbstractRequest {
    public CoyoteRequest() {
        super();
    }

    public CoyoteRequest(Object request) {
        super(request);
    }

    public CoyoteRequest(Object request, String requestId) {
        super(request, requestId);
    }

    /**
     * 把 message bytes 字节类型信息数据转化成字符串类型数据
     *
     * @param messageBytes 字节类型的信息数据
     * @return 转化之后的字符串类型的信息数据
     */
    private String mb2string(Object messageBytes) {
        return Reflection.invokeStringMethod(messageBytes, "toString", EMPTY_CLASS);
    }

    /**
     * 获取本服务的端口号
     *
     * @return 端口号
     */
    private String getServerPort() {
        Object port = Reflection.invokeMethod(request, "getServerPort", EMPTY_CLASS);
        return port != null ? port.toString() : null;
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getRequestURI()
     */
    @Override
    public String getRequestURI() {
        return mb2string(Reflection.invokeMethod(request, "requestURI", EMPTY_CLASS));
    }

    @Override
    public String getParameter(String key) {
        Object parameter = Reflection.invokeMethod(request, "getParameter", STRING_CLASS, key);
        if (parameter instanceof String) return parameter.toString();
        return null;
    }

    @Override
    public String getHeader(String key) {
        return mb2string(Reflection.invokeMethod(request, "getHeader", STRING_CLASS, key));
    }

    @Override
    public String getParameters() {
        String parameters = mb2string(Reflection.invokeMethod(request, "getParameters", EMPTY_CLASS));
        if (parameters == null || parameters.length() == 0) {
            parameters = mb2string(Reflection.invokeMethod(request, "queryString", EMPTY_CLASS));
        }
        return parameters;
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer sb = new StringBuffer();

        String scheme = mb2string(Reflection.invokeMethod(request, "scheme", EMPTY_CLASS));
        if (scheme != null) {
            sb.append(scheme).append("://");
        }

        String host = getServerName();
        if (host != null) {
            sb.append(host);
        }

        String port = getServerPort();
        if (port != null) {
            sb.append(":").append(port);
        }

        String uri = getRequestURI();
        if (uri != null) {
            sb.append(uri);
        }

        return sb;
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getServerName()
     */
    @Override
    public String getServerName() {
        return mb2string(Reflection.invokeMethod(request, "serverName", EMPTY_CLASS));
    }

}
