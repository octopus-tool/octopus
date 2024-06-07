package org.gaius.octopus.plugin.mysql;

import lombok.extern.slf4j.Slf4j;
import org.gaius.datasource.DatasourceFactory;
import org.gaius.datasource.model.DatasourceConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mysql数据源工厂类
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@Slf4j
public class MySQLDatasourceFactory implements DatasourceFactory<MySQLDatasourceInstance, Object> {
    // 每一个数据源ID对应一个数据源实例
    private static final Map<String, MySQLDatasourceInstance> DATASOURCE_INSTANCE_MAP = new ConcurrentHashMap<>();

    // 对象锁
    private static final Object LOCK = new Object();

    @Override
    public MySQLDatasourceInstance create(DatasourceConfig config) {
        // 基于数据源配置，判断数据源是否需要进行连接池创建
        String tenantId = config.getTenantId();
        String datasourceId = config.getDatasourceId();
        String datasourceName = config.getDatasourceName();
        log.info("获取数据源实例 tenantId:{},datasourceId:{},datasourceName:{}", tenantId, datasourceId, datasourceName);
        // 采用饿汉式创建数据源实例，添加锁避免重复创建
        synchronized (LOCK) {
            if (DATASOURCE_INSTANCE_MAP.containsKey(datasourceId)) {
                return DATASOURCE_INSTANCE_MAP.get(datasourceId);
            } else {
                MySQLDatasourceInstance datasourceInstance = new MySQLDatasourceInstance(config);
                DATASOURCE_INSTANCE_MAP.put(datasourceId, datasourceInstance);
                return datasourceInstance;
            }
        }
    }
}
