package org.gaius.octopus.core.execute;

import lombok.extern.slf4j.Slf4j;
import org.gaius.datasource.Available;

/**
 * 抽象执行器引擎
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
@Slf4j
public abstract class AbstractExecuteEngine<T> {
    
    /**
     * 执行对象验证
     *
     *
     * @return
     */
    public abstract Available validate(ExecuteContext<T> content) throws Exception;
    
    /**
     * 执行对象执行
     *
     * @param context 执行上下文
     * @return
     * @throws Exception
     */
    public abstract Object invoke(ExecuteContext<T> context) throws Exception;
    
    /**
     * 执行对象销毁
     */
    public abstract void destroy(ExecuteContext<T> context);
}
