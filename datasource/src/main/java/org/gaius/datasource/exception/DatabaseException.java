package org.gaius.datasource.exception;

import java.io.Serial;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据库异常
 * @date 2024/6/6
 */
public class DatabaseException extends Exception {
    
    @Serial
    private static final long serialVersionUID = -3553483826168773633L;
    
    public DatabaseException(Throwable cause) {
        super(cause);
    }
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable origin) {
        super(message, origin);
    }
}
