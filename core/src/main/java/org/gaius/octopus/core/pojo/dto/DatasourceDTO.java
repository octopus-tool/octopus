package org.gaius.octopus.core.pojo.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源对象
 * @date 2024/6/7
 */
@Data
public class DatasourceDTO {
    
    /**
     * 数据源ID
     */
    private Long id;
    
    /**
     * 数据源名称
     */
    private String name;
    
    /**
     * 数据源类型
     */
    private String datasourceType;
    
    /**
     * 数据源配置
     */
    private Map<String, Object> content;
}
