package org.gaius.datasource.exception;

/**
 * @author zhaobo
 * @program octopus
 * @description 插件异常类
 * @date 2024/6/7
 */
public class PluginException extends Exception {
    
    public PluginException(Throwable cause) {
        super(cause);
    }
    
    public PluginException(String message) {
        super(message);
    }
    
    public PluginException(String message, Throwable origin) {
        super(message, origin);
    }
    
}
