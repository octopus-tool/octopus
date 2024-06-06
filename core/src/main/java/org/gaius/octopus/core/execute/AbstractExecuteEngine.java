package org.gaius.octopus.core.execute;

import lombok.extern.slf4j.Slf4j;

/**
 * 抽象执行器引擎
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
@Slf4j
public abstract class AbstractExecuteEngine<T> {

    public abstract Object invoke(ExecuteContext<T> context);
}
