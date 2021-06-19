package cn.com.x1001.http;

import sun.reflect.Reflection;

import java.util.UUID;

/**
 * @see  <a href="https://github.com/baidu/openrasp/blob/master/agent/java/engine/src/main/java/com/baidu/openrasp/request/AbstractRequest.java">百度RASP</a>
 * 参考百度rasp @see
 */
public abstract class AbstractRequest {
    protected static final Class[] EMPTY_CLASS = new Class[]{};
    protected static final Class[] STRING_CLASS = new Class[]{String.class};
    protected Object request;
    protected String requestId;
    public AbstractRequest() {
        this(null);
    }
    public AbstractRequest(Object request) {
        this.request = request;
        this.requestId = UUID.randomUUID().toString().replace("-", "");
    }
    public AbstractRequest(Object request, String requestId) {
        this.request = request;
        this.requestId = requestId;
    }
    /**
     * 设置请求实体，该请求实体在不同的环境中可能是不同的类型
     *
     * @param request 请求实体
     */
    public void setRequest(Object request) {
        this.request = request;
    }
    /**
     * 获取请求实体
     *
     * @return 请求实体
     */
    public Object getRequest() {
        return this.request;
    }
    /**
     * 获取请求Id
     *
     * @return 请求Id
     */
    public String getRequestId() {
        return requestId;
    }
    /**
     * 获取请求的url
     *
     * @return 请求的url
     */
    public abstract StringBuffer getRequestURL();

    public String getRequestURLString() {
        Object ret = getRequestURL();
        return ret != null ? ret.toString() : null;
    }

    public String getServerName() {
        return null;
    }
    public String getRequestURI() {
        return null;
    }
    /**
     * 根据请求的参数名称，获取请求参数的值
     *
     * @param key 请求参数名称
     * @return 请求参数的值
     */
    public abstract String getParameter(String key);
    /**
     * 获取所有请求参数的值
     *
     * @return 请求参数的值
     */
    public abstract String getParameters();
    /**
     * 获取指定key的对应的header值
     */
    public abstract String getHeader(String key);
    /**
     * 获取method值
     */
    public abstract String getMethod();


}
