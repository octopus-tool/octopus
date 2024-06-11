package org.gaius.datasource.exception;

import java.io.Serial;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源异常
 * @date 2024/6/6
 */
public class DatasourceException extends Exception {
    
    @Serial
    private static final long serialVersionUID = -3553483826168773633L;
    
    public DatasourceException(Throwable cause) {
        super(cause);
    }
    
    public DatasourceException(String message) {
        super(message);
    }
    
    public DatasourceException(String message, Throwable origin) {
        super(message, origin);
    }
}
