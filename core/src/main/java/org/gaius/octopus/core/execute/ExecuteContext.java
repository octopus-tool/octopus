package org.gaius.octopus.core.execute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 执行器上下文
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteContext<T> {
    
    /**
     * 数据内容
     */
    private T content;
    
    /**
     * 参数
     */
    private Map<String, Object> args;
}
