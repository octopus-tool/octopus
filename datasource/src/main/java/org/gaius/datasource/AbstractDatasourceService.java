package org.gaius.datasource;

import java.util.Map;

/**
 * 数据源抽象类
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
public abstract class AbstractDatasourceService {

    /**
     * 数据源是否可用
     *
     * @param datasourceInfo 数据源连接信息
     * @return 是否可用
     */
    public abstract AvaliableResp avaliable(Map<String, Object> datasourceInfo);

    /**
     * 数据源接口调用
     *
     * @param datasourceInfo 数据源连接信息
     * @param interfaceInfo  接口信息
     * @param params         参数
     * @return
     */
    public abstract Object invoke(Map<String, Object> datasourceInfo, Map<String, Object> interfaceInfo, Map<String, Object> params);
}
