package org.gaius.octopus.core.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gaius.datasource.exception.DatasourceException;
import org.gaius.octopus.core.execute.AbstractExecuteEngine;
import org.gaius.octopus.core.execute.ExecuteContext;
import org.gaius.octopus.core.execute.datasource.DatasourceExecuteDTO;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.gaius.octopus.core.pojo.dto.DatasourceInterfaceDTO;
import org.gaius.octopus.core.service.DatasourceInterfaceService;
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
public class DatasourceInterfaceServiceImpl implements DatasourceInterfaceService {
    
    @Resource
    private DatasourceService datasourceService;
    
    @Resource
    private AbstractExecuteEngine<DatasourceExecuteDTO> datasourceExecuteEngine;
    
    @Override
    public Object test(DatasourceInterfaceDTO dto) throws Exception {
        // 基于数据源ID获取数据源实例
        DatasourceDTO datasourceDTO = datasourceService.selectById(dto.getDatasourceId());
        if (datasourceDTO == null) {
            throw new DatasourceException("not found");
        }
        DatasourceExecuteDTO executeDTO = DatasourceExecuteDTO.builder().datasource(datasourceDTO)
                .interfaceInfo(dto.getContent()).build();
        ExecuteContext<DatasourceExecuteDTO> context = ExecuteContext.<DatasourceExecuteDTO>builder()
                .content(executeDTO).args(dto.getArg()).build();
        return datasourceExecuteEngine.invoke(context);
    }
}
