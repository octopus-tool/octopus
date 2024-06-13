package org.gaius.datasource.plugin;

import lombok.Data;

/**
 * 插件上下文
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@Data
public class PluginContext {
    
    /**
     * 插件服务
     */
    DatasourcePluginService pluginService;
}
