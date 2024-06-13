package org.gaius.octopus.plugin.mysql.constant;

/**
 * http数据源常量
 *
 * @author gaius.zhao
 * @date 2024/6/11
 */
public class HTTPConstant {
    
    private HTTPConstant() {
    }
    
    /**
     * timeout字段
     */
    public static final String TIMEOUT = "timeout";
    
    /**
     * 终端
     */
    
    public static final String ENDPOINT = "endpoint";
    
    /**
     * 请求方法
     */
    public static final String METHOD = "method";
    
    /**
     * 请求路径
     */
    public static final String PATH = "path";
    
    /**
     * 请求内容类型
     */
    public static final String CONTENT_TYPE = "contentType";
    
    /**
     * 认证方式
     */
    public static final String CREDENTIAL_MODEL = "credentialModel";
    
    /**
     * 认证参数对象
     */
    public static final String CREDENTIALS = "credentials";
    
    /**
     * 用户名
     */
    public static final String USERNAME = "username";
    
    /**
     * 密码
     */
    public static final String PASSWORD = "password";
    
    /**
     * 心跳
     */
    public static final String HEARTBEAT = "heartbeat";
    
    /**
     * 请求头
     */
    public static final String HEADER = "headers";
    
    public static final String KEY = "key";
    
    public static final String VALUE = "value";
    
    /**
     * basic认证头
     */
    public static final String BASIC_AUTH_HEADER = "Authorization";
    
    /**
     * basic 认证格式
     */
    public static final String BASIC_AUTH_FORMAT = "Basic %s";
    
    /**
     * 默认接口请求超时时间，单位秒
     */
    public static final long DEFAULT_TIMEOUT = 30L;
    
    /**
     * 请求参数
     */
    public static final String PARAMS = "params";
    
    /**
     * 请求body
     */
    public static final String BODY = "body";
    
    public static final String CODE = "code";
    
    /**
     * 过期时间
     */
    
    public static final String EXPIRATION = "expiration";
    
    public static final String EXTRACT = "extract";
    
    
    /**
     * 凭证
     */
    public static final String CREDENTIAL = "credential";
}
