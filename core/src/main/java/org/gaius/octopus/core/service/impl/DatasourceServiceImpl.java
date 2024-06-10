package org.gaius.octopus.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gaius.datasource.Available;
import org.gaius.octopus.common.utils.JacksonUtil;
import org.gaius.octopus.core.execute.AbstractExecuteEngine;
import org.gaius.octopus.core.execute.ExecuteContext;
import org.gaius.octopus.core.execute.datasource.DatasourceExecuteDTO;
import org.gaius.octopus.core.mapper.DatasourceMapper;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.gaius.octopus.core.pojo.entity.Datasource;
import org.gaius.octopus.core.service.DatasourceService;
import org.springframework.stereotype.Service;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源接口实现类
 * @date 2024/6/7
 */
@Service
@Slf4j
public class DatasourceServiceImpl extends ServiceImpl<DatasourceMapper, Datasource> implements DatasourceService {
    
    @Resource
    private AbstractExecuteEngine<DatasourceExecuteDTO> datasourceExecuteEngine;
    
    @Override
    public Available test(DatasourceDTO dto) throws Exception {
        ExecuteContext<DatasourceExecuteDTO> executeContext = new ExecuteContext<>();
        executeContext.setContent(new DatasourceExecuteDTO(dto, null));
        return datasourceExecuteEngine.validate(executeContext);
    }
    
    @Override
    public DatasourceDTO selectById(Integer datasourceId) {
        Datasource datasource = this.getById(datasourceId);
        if (datasource == null) {
            return null;
        }
        DatasourceDTO dto = new DatasourceDTO();
        dto.setId(datasource.getId());
        dto.setName(datasource.getName());
        dto.setDatasourceType(datasource.getPluginName());
        dto.setContent(JacksonUtil.readValue(datasource.getContent()));
        return dto;
    }
}
