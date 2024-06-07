package org.gaius.datasource;

/**
 * 插件服务
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
public interface DatasourcePluginService {

    /**
     * 插件注册
     */
    void register(DatasourceFactory factory);
}
