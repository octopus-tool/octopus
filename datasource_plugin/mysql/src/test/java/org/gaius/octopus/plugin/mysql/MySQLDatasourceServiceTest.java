package org.gaius.octopus.plugin.mysql;

import org.gaius.datasource.AvailableResp;
import org.gaius.datasource.ServiceContext;
import org.gaius.datasource.model.DatasourceConfig;
import org.gaius.octopus.common.middle.CryptoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySQLDatasourceServiceTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void avaliable() {
        // 建构测试数据
        Map<String, Object> datasourceInfo = Map.of(
                "host", "10.2.2.153",
                "port", 18103,
                "user", "Rootmaster",
                "password", "Rootmaster@777",
                "database", "cw_doau",
                "driverClass", "com.mysql.cj.jdbc.Driver",
                "urlFormat", "jdbc:mysql://${host}:${port}/${database}?serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true"
        );
        DatasourceConfig datasourceConfig = new DatasourceConfig();
        datasourceConfig.setContent(datasourceInfo);
        datasourceConfig.setTenantId("110");
        datasourceConfig.setDatasourceId("123");
        // 获取工具类
        MySQLDatasourceFactory factory = new MySQLDatasourceFactory();
        // 创建实例
        MySQLDatasourceInstance instance = factory.create(datasourceConfig);
        ServiceContext mock = mock(ServiceContext.class);
        when(mock.getCryptoService()).thenReturn(new DefaultCryptoService());
        AvailableResp avaliable = instance.avaliable(mock);
        Assertions.assertTrue(avaliable.getAvailable(), "数据库连接失败");
    }

    public static class DefaultCryptoService implements CryptoService {

        @Override
        public String encrypt(String content) {
            return content;
        }

        @Override
        public String decrypt(String content) {
            return content;
        }
    }

    @Test
    void invoke() {
    }
}