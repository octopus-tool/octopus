package org.gaius.octopus.core.execute.datasource;

import lombok.extern.slf4j.Slf4j;
import org.gaius.octopus.core.execute.AbstractExecuteEngine;
import org.gaius.octopus.core.execute.ExecuteContext;
import org.springframework.stereotype.Service;

/**
 * 数据源执行引擎
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
@Service
@Slf4j
public class DatasourceExecuteEngine extends AbstractExecuteEngine<String> {


    @Override
    public Object invoke(ExecuteContext<String> context) {

        return null;
    }
}
