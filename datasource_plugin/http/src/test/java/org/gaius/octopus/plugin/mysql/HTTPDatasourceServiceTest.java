package org.gaius.octopus.plugin.mysql;

import org.gaius.datasource.Available;
import org.gaius.datasource.ServiceContext;
import org.gaius.datasource.model.DatasourceProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class HTTPDatasourceServiceTest {
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void available_success_when_datasource_info_is_valid_then_return_available() {
        // 建构测试数据
        Map<String, Object> datasourceInfo = Map.of("endpoint", "https://www.baidu.com", "heartbeat", "/");
        DatasourceProperties datasourceProperties = new DatasourceProperties();
        datasourceProperties.setContent(datasourceInfo);
        datasourceProperties.setTenantId("110");
        datasourceProperties.setDatasourceId(1L);
        // 获取工具类
        HTTPDatasourceFactory factory = new HTTPDatasourceFactory();
        // 创建实例
        HTTPDatasourceInstance instance = factory.create(datasourceProperties);
        ServiceContext mock = mock(ServiceContext.class);
        Available available = instance.available(mock);
        Assertions.assertTrue(available.getAvailable(), "数据源连接失败");
    }
    
    // 当测试域名无法解析时，返回不可用
    @Test
    void available_fail_when_datasource_info_is_invalid_then_return_unavailable() {
        // 建构测试数据
        Map<String, Object> datasourceInfo = Map.of("endpoint", "https://www.google.com", "heartbeat", "/");
        DatasourceProperties datasourceProperties = new DatasourceProperties();
        datasourceProperties.setContent(datasourceInfo);
        datasourceProperties.setTenantId("110");
        datasourceProperties.setDatasourceId(1L);
        // 获取工具类
        HTTPDatasourceFactory factory = new HTTPDatasourceFactory();
        // 创建实例
        HTTPDatasourceInstance instance = factory.create(datasourceProperties);
        ServiceContext mock = mock(ServiceContext.class);
        Assertions.assertFalse(instance.available(mock).getAvailable(), "数据源连接失败");
        factory.destroy(datasourceProperties);
    }
    
    // 基于basic协议认证
    @Test
    void available_success_when_datasource_info_is_valid_and_basic_auth_then_return_available() {
        Map<String, String> credentialObject = Map.of("username", "123", "password", "123.");
        // 建构测试数据
        Map<String, Object> datasourceInfo = Map.of("endpoint",
                "http://jfrog.xxx.com/artifactory/Products-Repo/automation/automationCjeMaster", "heartbeat",
                "/automationCjeMaster-3.11.0-20240606182005-3d720a57.tar.gz", "credentialModel", "basic",
                "credentials", credentialObject);
        DatasourceProperties datasourceProperties = new DatasourceProperties();
        datasourceProperties.setContent(datasourceInfo);
        datasourceProperties.setTenantId("110");
        datasourceProperties.setDatasourceId(1L);
        // 获取工具类
        HTTPDatasourceFactory factory = new HTTPDatasourceFactory();
        // 创建实例
        HTTPDatasourceInstance instance = factory.create(datasourceProperties);
        ServiceContext mock = mock(ServiceContext.class);
        Assertions.assertTrue(instance.available(mock).getAvailable(), "数据源连接失败");
        factory.destroy(datasourceProperties);
    }
    
    @Test
    void invoke() {
    }
}