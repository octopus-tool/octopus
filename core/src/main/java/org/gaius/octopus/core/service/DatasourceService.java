package org.gaius.octopus.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.gaius.datasource.Available;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.gaius.octopus.core.pojo.entity.Datasource;

/**
 * @author zhaobo
 * @program octopus
 * @description 数据源接口
 * @date 2024/6/7
 */
public interface DatasourceService extends IService<Datasource> {
    
    /**
     * 数据源测试
     *
     * @param dto 数据源对象
     * @return
     */
    Available test(DatasourceDTO dto) throws Exception;
    
    /**
     * 根据id查询数据源
     *
     * @param datasourceId 数据源id
     * @return 数据源对象
     */
    DatasourceDTO selectById(Long datasourceId);
}
