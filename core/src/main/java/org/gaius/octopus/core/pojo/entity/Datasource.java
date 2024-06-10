package org.gaius.octopus.core.pojo.entity;

import lombok.Data;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源
 * @date 2024/6/10
 */
@Data
public class Datasource {
    
    private Long id;
    
    private String name;
    
    private String pluginName;
    
    private String content;
}
