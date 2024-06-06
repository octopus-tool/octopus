package org.gaius.octopus.core.execute;

import lombok.Data;

/**
 * 执行器上下文
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
@Data
public class ExecuteContext<T> {

    /**
     * 节点数据内容
     */
    private T content;

}
