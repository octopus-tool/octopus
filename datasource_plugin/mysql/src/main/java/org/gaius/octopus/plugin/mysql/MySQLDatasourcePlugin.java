package org.gaius.octopus.plugin.mysql;

import com.google.auto.service.AutoService;
import org.gaius.datasource.plugin.DatasourcePlugin;
import org.gaius.datasource.plugin.PluginContext;

/**
 * 数据源插件
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@AutoService(DatasourcePlugin.class)
public class MySQLDatasourcePlugin implements DatasourcePlugin {
    
    @Override
    public String getName() {
        return "mysqlPlugin";
    }
    
    @Override
    public void load(PluginContext context) {
        // 注册当前插件
        context.getPluginService().register(getName(), new MySQLDatasourceFactory());
    }
}
