package org.gaius.octopus.core.pojo.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源接口
 * @date 2024/6/9
 */
@Data
public class DatasourceInterfaceDTO {
    
    /**
     * 接口ID
     */
    private Long id;
    
    /**
     * 数据源ID
     */
    private Long datasourceId;
    
    /**
     * 接口名称
     */
    private String name;
    
    /**
     * 接口内容
     */
    private Map<String, Object> content;
    
    /**
     * 接口参数
     */
    private Map<String, Object> arg;
}
