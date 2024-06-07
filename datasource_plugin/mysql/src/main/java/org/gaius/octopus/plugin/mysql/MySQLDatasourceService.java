package org.gaius.octopus.plugin.mysql;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.gaius.datasource.AbstractDatasourceService;
import org.gaius.datasource.AvailableResp;
import org.gaius.datasource.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * mysql数据源
 *
 * @author gaius.zhao
 * @date 2024/5/23
 */
@AutoService(AbstractDatasourceService.class)
@Slf4j
public class MySQLDatasourceService extends AbstractDatasourceService {
    @Override
    public AvailableResp avaliable(Map<String, Object> datasourceInfo) {
        try {
            // 获取url格式
            String urlFormat = MapUtils.getString(datasourceInfo, "urlFormat");
            // 获取数据库驱动
            String driverClass = MapUtils.getString(datasourceInfo, "driverClass");
            String user = MapUtils.getString(datasourceInfo, "user");
            String password = MapUtils.getString(datasourceInfo, "password");
            // todo 数据库密码需解密
            // 使用groovy格式化urlFormat
            String url = formatByGroovy(urlFormat, datasourceInfo);
            log.info("校验mysql连接参数是否正确, driverClass:{},url:{}, user:{}, password:******", driverClass, url, user);
            // 创建数据库连接
            Class.forName(driverClass);
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                if (connection != null) {
                    DatabaseMetaData metaData = connection.getMetaData();
                    String databaseType = metaData.getDatabaseProductName();
                    String productVersion = metaData.getDatabaseProductVersion();
                    log.info("mysql数据库连接成功, 数据库类型:{}, 数据库版本:{}", databaseType, databaseType);
                    return new AvailableResp(true, databaseType + productVersion + "连接成功");
                }
                return new AvailableResp(false, "mysql数据库连接失败");
            }
        } catch (ClassNotFoundException e) {
            log.error("mysql驱动加载失败", e);
            return new AvailableResp(false, "mysql驱动加载失败");
        } catch (SQLException sqlException) {
            log.error("mysql数据库连接失败", sqlException);
            return new AvailableResp(false, "mysql数据库连接失败；" + sqlException.getMessage());
        } catch (Exception unknown) {
            log.error("连接数据库时发生未知异常", unknown);
            return new AvailableResp(false, "连接数据库时发生未知异常");
        }
    }

    @Override
    public Object invoke(Map<String, Object> datasourceInfo, Map<String, Object> interfaceInfo, Map<String, Object> params) {
        return null;
    }
}
