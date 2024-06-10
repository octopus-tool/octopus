package org.gaius.octopus.plugin.mysql;

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.gaius.datasource.Available;
import org.gaius.datasource.DatasourceInstance;
import org.gaius.datasource.InvokeContext;
import org.gaius.datasource.ServiceContext;
import org.gaius.datasource.exception.DatabaseException;
import org.gaius.datasource.model.DatasourceProperties;
import org.gaius.datasource.utils.TemplateUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * mysql数据源
 *
 * @author gaius.zhao
 * @date 2024/5/23
 */
@Slf4j
public class MySQLDatasourceInstance implements DatasourceInstance<Object> {
    
    /**
     * 数据源配置
     */
    private final DatasourceProperties properties;
    
    /**
     * 数据源
     */
    private final DataSource dataSource;
    
    /**
     * 测试连接成功
     */
    private static final String SUCCESS_TEMPLATE = "%s %s 连接成功";
    
    public MySQLDatasourceInstance(DatasourceProperties properties) {
        this.properties = properties;
        this.dataSource = init(properties);
    }
    
    /**
     * 初始化数据库连接
     *
     * @param config 配置
     * @return
     */
    private DataSource init(DatasourceProperties config) {
        Map<String, Object> content = config.getContent();
        Boolean pool = MapUtils.getBoolean(content, "pool", false);
        // 若当前数据源实例开启连接池，则使用HikariCP连接池
        if (pool) {
            log.info(
                    "The current data source instance is enabled to connect to the connection pool; tenantId:{} datasourceId:{} datasourceName:{}",
                    config.getTenantId(), config.getDatasourceId(), config.getDatasourceName());
            return createDataSource(content);
        }
        log.info(
                "Connection pool is not enabled for the current data source instance; tenantId:{} datasourceId:{} datasourceName:{}",
                config.getTenantId(), config.getDatasourceId(), config.getDatasourceName());
        return null;
    }
    
    
    /**
     * 获取数据源
     *
     * @param datasource 数据源配置
     * @return 数据源对象
     */
    private HikariDataSource createDataSource(Map<String, Object> datasource) {
        HikariConfig config = new HikariConfig();
        // 获取url格式
        String urlFormat = MapUtils.getString(datasource, "urlFormat");
        String url = TemplateUtil.render(urlFormat, datasource);
        // 获取数据库驱动
        String driverClass = MapUtils.getString(datasource, "driverClass");
        String user = MapUtils.getString(datasource, "user");
        String password = MapUtils.getString(datasource, "password");
        config.setJdbcUrl(url);
        config.setDriverClassName(driverClass);
        config.setUsername(user);
        config.setPassword(password);
        config.setConnectionTestQuery("select 1");
        Map<String, Object> advanced = (Map<String, Object>) MapUtils.getMap(datasource, "advanced",
                Collections.emptyMap());
        // 连接超时时间,默认10秒
        Long connectionTimeout = MapUtils.getLong(advanced, "connectionTimeout", 10000L);
        config.setConnectionTimeout(connectionTimeout);
        // 空闲连接超时时间,默认10分钟
        Long idleTimeout = MapUtils.getLong(advanced, "idleTimeout", 600000L);
        config.setIdleTimeout(idleTimeout);
        // 最大空闲时间,默认30分钟
        Long maxLifetime = MapUtils.getLong(advanced, "maxLifetime", 1800000L);
        config.setMaxLifetime(maxLifetime);
        // keepAliveTime,默认30秒
        Long keepAliveTime = MapUtils.getLong(advanced, "keepAliveTime", 30000L);
        config.setKeepaliveTime(keepAliveTime);
        // 最小连接数,默认1
        Integer minIdle = MapUtils.getInteger(advanced, "minIdle", 1);
        config.setMinimumIdle(minIdle);
        // 获取最大连接数,默认10
        Integer maxPoolSize = MapUtils.getInteger(advanced, "maxPoolSize", 10);
        config.setMaximumPoolSize(maxPoolSize);
        log.info("初始化mysql数据源, url:{}, user:{}, password:******, advanced:{}", url, user, advanced);
        return new HikariDataSource(config);
    }
    
    @Override
    public Available available(ServiceContext context) {
        try (Connection connection = createConnection(context)) {
            // 创建数据库连接
            if (connection != null) {
                DatabaseMetaData metaData = connection.getMetaData();
                String databaseType = metaData.getDatabaseProductName();
                String productVersion = metaData.getDatabaseProductVersion();
                log.info("The mysql database has been successfully connected, database version:{}", productVersion);
                return new Available(true, SUCCESS_TEMPLATE.formatted(databaseType, productVersion));
            }
            return new Available(false, "mysql数据库连接失败");
        } catch (ClassNotFoundException e) {
            log.error("mysql驱动加载失败", e);
            return new Available(false, "mysql驱动加载失败");
        } catch (SQLException sqlException) {
            log.error("mysql数据库连接失败", sqlException);
            return new Available(false, "mysql数据库连接失败；" + sqlException.getMessage());
        } catch (Exception unknown) {
            log.error("连接数据库时发生未知异常", unknown);
            return new Available(false, "连接数据库时发生未知异常");
        }
    }
    
    /**
     * 创建数据库连接
     *
     * @param serviceContext 服务上下文
     * @return connection 数据库连接
     * @throws ClassNotFoundException 驱动加载失败
     * @throws SQLException           数据库连接失败
     */
    private Connection createConnection(ServiceContext serviceContext) throws ClassNotFoundException, SQLException {
        Map<String, Object> datasourceInfo = properties.getContent();
        // 获取url格式
        String urlFormat = MapUtils.getString(datasourceInfo, "urlFormat");
        // 获取数据库驱动
        String driverClass = MapUtils.getString(datasourceInfo, "driverClass");
        String user = MapUtils.getString(datasourceInfo, "user");
        String password = MapUtils.getString(datasourceInfo, "password");
        String encrypt = serviceContext.getCryptoService().encrypt(password);
        String url = TemplateUtil.render(urlFormat, datasourceInfo);
        log.info("校验mysql连接参数是否正确, driverClass:{},url:{}, user:{}, password:******", driverClass, url, user);
        Class.forName(driverClass);
        return DriverManager.getConnection(url, user, encrypt);
    }
    
    @Override
    public Object invoke(InvokeContext context) throws DatabaseException {
        StopWatch started = StopWatch.createStarted();
        log.info("开始执行mysql语句");
        Map<String, Object> interfaceInfo = context.getInterfaceInfo();
        Map<String, Object> args = context.getArgs();
        try (Connection connection = getConnection(context.getServiceContext())) {
            // 获取SQL执行超时时间,默认1分钟
            int timeout = MapUtils.getIntValue(interfaceInfo, "timeout", 60);
            // 执行SQL
            String sql = MapUtils.getString(interfaceInfo, "sql");
            log.info("原始sql语句:{}", sql);
            // sql格式化
            sql = TemplateUtil.render(sql, args);
            log.info("格式化后的sql语句:{}", sql);
            // 准备执行SQL
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(timeout);
            // 执行SQL,若非查询语句,获取更新记录数
            log.info("开始执行sql语句, timeout:{}", timeout);
            StopWatch startedExecute = StopWatch.createStarted();
            boolean isSelected = statement.execute();
            log.info("sql语句执行完成，耗时:{}ms", startedExecute.getTime());
            if (isSelected) {
                ResultSet resultSet = statement.getResultSet();
                // 获取结果集
                List<Map<String, Object>> result = new ArrayList<>();
                while (resultSet.next()) {
                    // 获取列数
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    Map<String, Object> row = Maps.newHashMapWithExpectedSize(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = Objects.toString(metaData.getColumnLabel(i), metaData.getColumnName(i));
                        // 基于数据库字段类型转换为对应的Java类型
                        Object columnValue = convertColumnValue(resultSet, metaData.getColumnType(i), i);
                        row.put(columnName, columnValue);
                    }
                    result.add(row);
                }
                return result;
            }
            return statement.getUpdateCount();
        } catch (SQLException e) {
            log.error("sql任务异常", e);
            throw new DatabaseException(e);
        } catch (ClassNotFoundException e) {
            log.error("mysql驱动加载失败", e);
            throw new DatabaseException(e);
        } finally {
            started.stop();
            log.info("sql任务执行结束,总耗时:{}ms", started.getTime(TimeUnit.MILLISECONDS));
        }
    }
    
    /**
     * 转换数据库字段值
     * <p>主要处理日期类型字段
     *
     * @param resultSet   数据库结果集
     * @param columnType  数据库字段类型
     * @param columnIndex 数据库字段索引
     * @return 对应的Java类型
     */
    private Object convertColumnValue(ResultSet resultSet, int columnType, int columnIndex) throws SQLException {
        if (columnType == Types.DATE || columnType == Types.TIME) {
            java.sql.Date sqlDate = resultSet.getDate(columnIndex);
            if (sqlDate != null) {
                return new Date(sqlDate.getTime());
            }
            return null;
        }
        if (columnType == Types.TIMESTAMP) {
            Timestamp sqlTimestamp = resultSet.getTimestamp(columnIndex);
            if (sqlTimestamp != null) {
                return new Date(sqlTimestamp.getTime());
            }
            return null;
        }
        return resultSet.getObject(columnIndex);
    }
    
    /**
     * 获取数据库连接
     *
     * <p>若当前数据源实例使用了连接池，则从连接池中获取连接；否则直接创建数据库连接</p>
     *
     * @param serviceContext 服务上下文
     * @return connection 数据库连接
     */
    private Connection getConnection(ServiceContext serviceContext) throws SQLException, ClassNotFoundException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        return createConnection(serviceContext);
    }
    
    @Override
    public void destroy() {
        // 如当前数据源实例使用了连接池，需要关闭连接池
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
