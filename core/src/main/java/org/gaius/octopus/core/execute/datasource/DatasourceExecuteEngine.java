package org.gaius.octopus.core.execute.datasource;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gaius.datasource.Available;
import org.gaius.datasource.DatasourceFactory;
import org.gaius.datasource.DatasourceInstance;
import org.gaius.datasource.InvokeContext;
import org.gaius.datasource.ServiceContext;
import org.gaius.datasource.exception.PluginException;
import org.gaius.datasource.model.DatasourceProperties;
import org.gaius.datasource.plugin.DatasourcePluginService;
import org.gaius.octopus.common.middle.CacheService;
import org.gaius.octopus.common.middle.CryptoService;
import org.gaius.octopus.core.execute.AbstractExecuteEngine;
import org.gaius.octopus.core.execute.ExecuteContext;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.springframework.stereotype.Service;

/**
 * 数据源执行引擎
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
@Service
@Slf4j
public class DatasourceExecuteEngine extends AbstractExecuteEngine<DatasourceExecuteDTO> {
    
    @Resource
    private DatasourcePluginService<DatasourceInstance<Object>, Object> defaultDatasourcePluginService;
    
    @Resource
    private CacheService<String> defaultCacheService;
    
    @Resource
    private CryptoService smCryptoService;
    
    @Override
    public Available validate(ExecuteContext<DatasourceExecuteDTO> content) throws Exception {
        DatasourceExecuteDTO executeDTO = content.getContent();
        DatasourceDTO datasource = executeDTO.getDatasource();
        DatasourceInstance<Object> instance = getInstance(datasource);
        return instance.available(buildServiceContext());
    }
    
    /**
     * 构建服务上下文
     *
     * @return
     */
    private ServiceContext buildServiceContext() {
        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setCacheService(defaultCacheService);
        serviceContext.setCryptoService(smCryptoService);
        return serviceContext;
    }
    
    /**
     * 获取数据源实例
     *
     * @param dto 数据源对象
     * @return
     */
    public DatasourceInstance<Object> getInstance(DatasourceDTO dto) throws PluginException {
        String datasourceType = dto.getDatasourceType();
        DatasourceFactory<DatasourceInstance<Object>, Object> factory = defaultDatasourcePluginService.getFactory(
                datasourceType);
        if (factory != null) {
            DatasourceProperties properties = DatasourceProperties.builder().datasourceName(dto.getName())
                    .datasourceId(dto.getId()).content(dto.getContent()).build();
            return factory.create(properties);
        }
        log.error("Plugin:{} not found", datasourceType);
        throw new PluginException("插件：【" + datasourceType + "】未找到");
    }
    
    @Override
    public Object invoke(ExecuteContext<DatasourceExecuteDTO> context) throws Exception {
        DatasourceExecuteDTO executeDTO = context.getContent();
        DatasourceInstance<Object> instance = getInstance(executeDTO.getDatasource());
        InvokeContext invokeContext = InvokeContext.builder().serviceContext(buildServiceContext())
                .interfaceInfo(executeDTO.getInterfaceInfo()).args(context.getArgs()).build();
        return instance.invoke(invokeContext);
    }
    
    @Override
    public void destroy(ExecuteContext<DatasourceExecuteDTO> context) {
        DatasourceExecuteDTO content = context.getContent();
        DatasourceDTO datasource = content.getDatasource();
        String datasourceType = datasource.getDatasourceType();
        DatasourceFactory<DatasourceInstance<Object>, Object> factory = defaultDatasourcePluginService.getFactory(
                datasourceType);
        if (factory != null) {
            DatasourceProperties properties = DatasourceProperties.builder().datasourceName(datasource.getName())
                    .datasourceId(datasource.getId()).content(datasource.getContent()).build();
            factory.destroy(properties);
        }
    }
}
