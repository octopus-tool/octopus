package org.gaius.datasource;

/**
 * 数据源插件接口
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
public interface DatasourcePlugin {

    /**
     * 获取插件名称
     *
     * @return 插件名称
     */
    String getName();

    /**
     * 加载插件
     *
     * @param context 服务上下文
     */
    void load(PluginContext context);
}
