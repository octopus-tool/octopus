package org.gaius.octopus.plugin.mysql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.gaius.datasource.Available;
import org.gaius.datasource.InvokeContext;
import org.gaius.datasource.ServiceContext;
import org.gaius.datasource.exception.DatasourceException;
import org.gaius.datasource.model.DatasourceProperties;
import org.gaius.octopus.common.middle.CacheService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockServerExtension.class)
class HTTPDatasourceServiceTest {
    
    private static ClientAndServer clientAndServer;
    
    @BeforeAll
    static void setUp() {
        clientAndServer = new ClientAndServer(1080);
        clientAndServer.when(HttpRequest.request().withMethod("GET").withPath("/health"))
                .respond(HttpResponse.response().withStatusCode(200));
        
        // mockserver basic认证协议 admin:123456
        HttpRequest basicRequest = HttpRequest.request().withHeader("Authorization", "Basic YWRtaW46MTIzNDU2")
                .withMethod("GET").withPath("/login");
        clientAndServer.when(basicRequest).respond(HttpResponse.response().withStatusCode(200)
                .withBody("{\"code\":200, \"data\":123, \"message\":\"success\"}"));
        
        // mockserver basic认证协议 admin:123456
        HttpRequest customRequest = HttpRequest.request().withHeader("token", "123").withMethod("GET")
                .withPath("/custom-auth");
        clientAndServer.when(customRequest).respond(HttpResponse.response().withStatusCode(200)
                .withBody("{\"code\":200, \"data\":456, \"message\":\"success\"}"));
    }
    
    @AfterAll
    static void tearDown() {
        clientAndServer.stop();
    }
    
    @Test
    void available_success_when_datasource_info_is_valid_then_return_available() {
        // 建构测试数据
        Map<String, Object> datasourceInfo = Map.of("endpoint", "http://localhost:1080", "heartbeat", "/health");
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
        Map<String, String> credentialObject = Map.of("username", "admin", "password", "123456");
        // 建构测试数据
        Map<String, Object> datasourceInfo = Map.of("endpoint", "http://localhost:1080", "heartbeat", "/login",
                "credentialModel", "basic", "credentials", credentialObject);
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
    
    // 自定义认证
    @Test
    void invoke_success_when_datasource_info_is_valid_and_custom_auth_then_return_available()
            throws DatasourceException {
        List<Map<String, Object>> headers = Lists.newArrayList(
                Map.of("key", "Authorization", "value", "Basic YWRtaW46MTIzNDU2", "desc", "token"));
        List<Map<String, Object>> extractList = List.of(
                Map.of("key", "token", "value", "$.body.data", "desc", "token"));
        Map<String, Object> credentialObject = Map.of("path", "/login", "method", "GET", "headers", headers, "extract",
                extractList);
        // 建构测试数据
        
        Map<String, Object> datasourceInfo = Map.of("endpoint", "http://localhost:1080", "path", "custom-auth",
                "method", "GET", "headers", headers, "heartbeat", " /login", "credentialModel", "custom", "credentials",
                credentialObject);
        
        List<Map<String, Object>> interfaceHeaders = Lists.newArrayList(
                Map.of("key", "token", "value", "${credential.token}", "desc", "token"));
        Map<String, Object> interfaceInfo = Map.of("path", "/custom-auth", "headers", interfaceHeaders, "method", "GET");
        /**
         * {
         *     "endpoint": "https://org.gaius.com",
         *     "headers": [
         *       { "key": "Content-type", "value": "application/json", "desc": "编码" }
         *     ],
         *     "heartbeat": "/heartbeat",
         *     "credentialModel": "custom",
         *     "credentials": {
         *       "path": "/login",
         *       "method": "post",
         *       "contentType":"application/json",
         *       "body": {
         *           "username": "xxx",
         *           "password": "xxx"
         *       }
         *     }
         *   }
         */
        DatasourceProperties datasourceProperties = new DatasourceProperties();
        datasourceProperties.setContent(datasourceInfo);
        datasourceProperties.setTenantId("110");
        datasourceProperties.setDatasourceId(1L);
        // 获取工具类
        HTTPDatasourceFactory factory = new HTTPDatasourceFactory();
        // 创建实例
        HTTPDatasourceInstance instance = factory.create(datasourceProperties);
        CacheService<String> cacheService = mock(CacheService.class);
        when(cacheService.del(anyString())).thenReturn(true);
        
        ServiceContext mock = mock(ServiceContext.class);
        when(mock.getCacheService()).thenReturn(cacheService);
        
        InvokeContext invokeContext = new InvokeContext();
        invokeContext.setServiceContext(mock);
        invokeContext.setInterfaceInfo(interfaceInfo);
        invokeContext.setArgs(Maps.newHashMap());
        Object result = instance.invoke(invokeContext);
        Assertions.assertInstanceOf(Map.class, result, "数据源连接失败");
        factory.destroy(datasourceProperties);
    }
    
}