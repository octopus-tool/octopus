package org.gaius.octopus.core.service;

import org.gaius.datasource.Available;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源接口
 * @date 2024/6/7
 */
public interface DatasourceService {
    
    /**
     * 数据源测试
     *
     * @param dto 数据源对象
     * @return
     */
    Available test(DatasourceDTO dto) throws Exception;
}
