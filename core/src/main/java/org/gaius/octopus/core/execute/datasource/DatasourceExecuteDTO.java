package org.gaius.octopus.core.execute.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;

import java.util.Map;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源执行对象
 * @date 2024/6/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasourceExecuteDTO {
    
    private DatasourceDTO datasource;
    
    private Map<String, Object> interfaceInfo;
    
}
