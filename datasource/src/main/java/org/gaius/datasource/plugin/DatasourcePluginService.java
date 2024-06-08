package org.gaius.datasource.plugin;

import org.gaius.datasource.DatasourceFactory;
import org.gaius.datasource.DatasourceInstance;

import java.util.Collection;
import java.util.List;

/**
 * 插件服务
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
public interface DatasourcePluginService<T extends DatasourceInstance<R>, R> {
    
    /**
     * 插件注册
     *
     * @param name    插件名称
     * @param factory 插件工厂
     */
    void register(String name, DatasourceFactory<T, R> factory);
    
    /**
     * 获取数据源工厂
     *
     * @param name 插件名称
     * @return
     */
    DatasourceFactory<T, R> getFactory(String name);
    
    /**
     * 获取所有数据源仓库
     *
     * @return
     */
    Collection<DatasourceFactory<T, R>> getAllFactory();
}
