package org.gaius.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 数据源配置
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasourceProperties {
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 数据源ID
     */
    private Long datasourceId;
    
    /**
     * 数据源名称
     */
    private String datasourceName;
    
    /**
     * 数据源配置
     */
    private Map<String, Object> content;
}
