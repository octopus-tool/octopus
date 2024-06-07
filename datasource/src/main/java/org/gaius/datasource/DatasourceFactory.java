package org.gaius.datasource;

import org.gaius.datasource.model.DatasourceConfig;

/**
 * 数据源工程类
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
public interface DatasourceFactory<T extends DatasourceInstance<R>, R> {
    /**
     * 创建插件实例
     *
     * @param config 参数
     * @return
     */
    T create(DatasourceConfig config);
}
