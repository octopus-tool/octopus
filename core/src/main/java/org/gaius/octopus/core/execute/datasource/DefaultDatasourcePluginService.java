package org.gaius.octopus.core.execute.datasource;

import lombok.extern.slf4j.Slf4j;
import org.gaius.datasource.DatasourceFactory;
import org.gaius.datasource.DatasourceInstance;
import org.gaius.datasource.plugin.DatasourcePluginService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaobo
 * @program octopus
 * @description 默认数据源插件服务实现类
 * @date 2024/6/7
 */
@Component
@Slf4j
public class DefaultDatasourcePluginService implements DatasourcePluginService<DatasourceInstance<Object>, Object> {
    
    private final Map<String, DatasourceFactory<DatasourceInstance<Object>, Object>> datasourceFactoryMap = new ConcurrentHashMap<>();
    
    @Override
    public void register(String name, DatasourceFactory<DatasourceInstance<Object>, Object> factory) {
        datasourceFactoryMap.put(name, factory);
    }
    
    /**
     * 获取数据源工厂
     *
     * @param name 数据源名称
     * @return
     */
    public DatasourceFactory<DatasourceInstance<Object>, Object> getFactory(String name) {
        return datasourceFactoryMap.get(name);
    }
    
    @Override
    public Collection<DatasourceFactory<DatasourceInstance<Object>, Object>> getAllFactory() {
        return datasourceFactoryMap.values();
    }
}
