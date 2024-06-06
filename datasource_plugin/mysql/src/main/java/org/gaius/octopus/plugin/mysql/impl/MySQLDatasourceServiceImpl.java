package org.gaius.octopus.plugin.mysql.impl;

import org.gaius.datasource.AbstractDatasourceService;
import org.gaius.datasource.AvaliableResp;

import java.util.Map;

/**
 * mysql数据源提供者
 *
 * @author gaius.zhao
 * @date 2024/5/23
 */
public class MySQLDatasourceServiceImpl extends AbstractDatasourceService {
    @Override
    public AvaliableResp avaliable(Map<String, Object> datasourceInfo) {
        return null;
    }

    @Override
    public Object invoke(Map<String, Object> datasourceInfo, Map<String, Object> interfaceInfo, Map<String, Object> params) {
        return null;
    }
}
