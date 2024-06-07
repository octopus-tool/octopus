package org.gaius.datasource.model;

import lombok.Data;

import java.util.Map;

/**
 * 数据源配置
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@Data
public class DatasourceConfig {
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 数据源ID
     */
    private String datasourceId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源配置
     */
    private Map<String, Object> content;
}
