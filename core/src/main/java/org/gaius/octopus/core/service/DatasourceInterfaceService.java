package org.gaius.octopus.core.service;

import org.gaius.octopus.core.pojo.dto.DatasourceInterfaceDTO;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源接口
 * @date 2024/6/7
 */
public interface DatasourceInterfaceService {
    
    /**
     * 数据源测试
     *
     * @param dto 数据源接口对象
     * @return
     */
    Object test(DatasourceInterfaceDTO dto) throws Exception;
}
