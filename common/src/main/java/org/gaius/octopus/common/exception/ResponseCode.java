package org.gaius.octopus.common.exception;

/**
 * <p>
 *
 * @author zhaobo
 * @program octopus
 * @description 响应码
 * @date 2024/6/8
 */
public interface ResponseCode {
    
    int SUCCESS = 200;
    
    int SYSTEM_ERROR = 500;
    
    /**
     * 数据源； 模块code为01
     * <p>四位数, 前两位为固定51,第三四位为模块代码,第四五位为具体错误码
     * <p>510100 - 510199 为数据源模块
     */
    interface Datasource {
        
        /**
         * 数据源连接失败
         */
        int DATASOURCE_CONNECT_FAIL = 510100;
        /**
         * 数据源驱动加载失败
         */
        int DATASOURCE_DRIVER_LOAD_FAIL = 510101;
        
        /**
         * 数据源连接发生未知异常
         */
        int DATASOURCE_UNKNOWN_EXCEPTION = 510199;
    }
    
}
