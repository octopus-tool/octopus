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
    public abstract AvailableResp avaliable(Map<String, Object> datasourceInfo) throws Exception;

    /**
     * 数据源接口调用
     *
     * @param datasourceInfo 数据源连接信息
     * @param interfaceInfo  接口信息
     * @param params         参数
     * @return
     */
    public abstract Object invoke(Map<String, Object> datasourceInfo, Map<String, Object> interfaceInfo, Map<String, Object> params);

    /**
     * 格式化模版
     *
     * @param template 模版
     * @param params   变量
     * @return
     */
    public String formatByGroovy(String template, Map<String, Object> params) {
        return null;
    }
}
