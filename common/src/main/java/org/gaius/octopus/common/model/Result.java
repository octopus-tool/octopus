package org.gaius.octopus.common.model;

import lombok.Data;
import org.gaius.octopus.common.exception.ResponseCode;

/**
 * @author zhaobo
 * @program octopus
 * @description 返回结果包装
 * @date 2024/6/8
 */
@Data
public class Result<T> {
    
    /**
     * 响应编码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * traceId
     */
    private String traceId;
    
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public Result(int code, String message, T data, String traceId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
    }
    
    /**
     * 响应成功
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseCode.SUCCESS, "success", data);
    }
    
    /**
     * 响应成功
     */
    public static <T> Result<T> success(T data, String traceId) {
        return new Result<>(ResponseCode.SUCCESS, "success", data, traceId);
    }
    
    /**
     * 响应失败
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
    
    
}
