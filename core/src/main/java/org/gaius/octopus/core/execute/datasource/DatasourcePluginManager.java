package org.gaius.octopus.core.execute.datasource;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gaius.datasource.DatasourceFactory;
import org.gaius.datasource.DatasourceInstance;
import org.gaius.datasource.plugin.DatasourcePlugin;
import org.gaius.datasource.plugin.DatasourcePluginService;
import org.gaius.datasource.plugin.PluginContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.ServiceLoader;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源插件管理器
 * @date 2024/6/7
 */
@Component
@Slf4j
public class DatasourcePluginManager implements InitializingBean, DisposableBean {
    
    @Resource
    private DatasourcePluginService<DatasourceInstance<Object>, Object> defaultDatasourcePluginService;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        PluginContext pluginContext = new PluginContext();
        pluginContext.setPluginService(defaultDatasourcePluginService);
        // 使用spi加载数据源仓库
        ServiceLoader<DatasourcePlugin> datasourcePlugins = ServiceLoader.load(DatasourcePlugin.class);
        for (DatasourcePlugin datasourcePlugin : datasourcePlugins) {
            datasourcePlugin.load(pluginContext);
        }
    }
    
    @Override
    public void destroy() throws Exception {
        log.info("销毁数据源插件");
        Collection<DatasourceFactory<DatasourceInstance<Object>, Object>> list = defaultDatasourcePluginService.getAllFactory();
        list.forEach(DatasourceFactory::destroyAll);
        log.info("销毁数据源插件完成");
    }
}
