package org.gaius.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 是否可用响应
 *
 * @author gaius.zhao
 * @date 2024/5/23
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Available {
    
    /**
     * 是否可用；true：可用；false：不可用
     */
    private Boolean available;
    
    /**
     * 信息；可用时看返回数据源信息；不可用时看返回错误信息
     */
    private String message;
}
