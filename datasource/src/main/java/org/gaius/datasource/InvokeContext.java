package org.gaius.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 调用上下文
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvokeContext {
    
    /**
     * 服务上下文
     */
    ServiceContext serviceContext;
    
    /**
     * 接口信息
     */
    Map<String, Object> interfaceInfo;
    
    /**
     * 参数信息
     */
    Map<String, Object> args;
}
