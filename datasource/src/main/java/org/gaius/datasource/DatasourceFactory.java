package org.gaius.datasource;

import org.gaius.datasource.model.DatasourceProperties;

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
     * @param properties 参数
     * @return 插件实例
     */
    T create(DatasourceProperties properties);
    
    /**
     * 销毁插件实例
     */
    void destroy(DatasourceProperties properties);
    
    /**
     * 销毁所有插件实例
     */
    void destroyAll();
}
