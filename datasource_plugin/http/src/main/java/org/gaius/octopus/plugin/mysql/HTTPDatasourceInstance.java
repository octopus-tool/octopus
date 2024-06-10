package org.gaius.octopus.plugin.mysql;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaius.datasource.Available;
import org.gaius.datasource.DatasourceInstance;
import org.gaius.datasource.InvokeContext;
import org.gaius.datasource.ServiceContext;
import org.gaius.datasource.model.DatasourceProperties;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http数据源
 *
 * @author gaius.zhao
 * @date 2024/5/23
 */
@Slf4j
public class HTTPDatasourceInstance implements DatasourceInstance<Object> {
    
    /**
     * 数据源配置
     */
    private final DatasourceProperties properties;
    
    //  http认证
    private final OkHttpClient client;
    
    /**
     * 测试连接成功
     */
    private static final String SUCCESS_TEMPLATE = "%s %s 连接成功";
    
    public HTTPDatasourceInstance(DatasourceProperties properties) {
        this.properties = properties;
        this.client = init(properties);
        
    }
    
    /**
     * 初始化http客户端
     *
     * @param properties 数据源连接参数
     * @return
     */
    private OkHttpClient init(DatasourceProperties properties) {
        Map<String, Object> content = properties.getContent();
        long timeout = MapUtils.getLongValue(content, "timeout", 30L);
        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).build();
        // 获取认证协议；basic协议，自定义、none
        String credentials = MapUtils.getString(content, "credentialModel", "basic");
        if (StringUtils.equalsIgnoreCase(credentials, "basic")) {
            Map<String, String> credentialsObject = (Map<String, String>) MapUtils.getMap(content, "credentials");
            // 获取认证用户名
            String username = MapUtils.getString(credentialsObject, "username");
            // 获取认证密码
            String password = MapUtils.getString(credentialsObject, "password");
            // 基于basic认证生成认证头
            String auth = String.format("%s:%s", username, password);
            String basicCredentials = Base64.getEncoder().encodeToString(auth.getBytes());
            Interceptor credentialInterceptor = (chain) -> {
                Request request = chain.request();
                Headers headers = request.headers().newBuilder().add("Authorization", "Basic " + basicCredentials)
                        .build();
                return chain.proceed(request.newBuilder().headers(headers).build());
            };
            return httpClient.newBuilder().addInterceptor(credentialInterceptor).build();
        }
        return httpClient;
    }
    
    @Override
    public Available available(ServiceContext context) {
        // 测试连接
        Map<String, Object> content = properties.getContent();
        // 获取心跳地址
        String heartbeat = MapUtils.getString(content, "heartbeat");
        // 如果未设置心跳地址，则返回
        if (heartbeat == null) {
            log.info("未配置心跳地址，默认成功");
            return Available.builder().available(true).message("未设置心跳地址").build();
        }
        String endpoint = MapUtils.getString(content, "endpoint");
        String heartbeatUrl = String.format("%s%s", endpoint, heartbeat);
        // 获取心跳接口默认超时时间
        long timeout = MapUtils.getLong(content, "timeout", 5L);
        log.info("heartbeatUrl:{},timeout:{} 基于心跳地址校验服务是否正常", heartbeatUrl, timeout);
        List<Map<String, Object>> headers = (List<Map<String, Object>>) MapUtils.getObject(content, "headers");
        Headers reqHeaders;
        if (CollectionUtils.isNotEmpty(headers)) {
            Map<String, String> headerMap = Maps.newHashMapWithExpectedSize(headers.size());
            headers.forEach(header -> {
                String key = MapUtils.getString(header, "key");
                String value = MapUtils.getString(header, "value");
                if (StringUtils.isNoneEmpty(key, value)) {
                    headerMap.put(key.trim(), value.trim());
                }
            });
            reqHeaders = Headers.of(headerMap);
        } else {
            reqHeaders = new Headers.Builder().build();
        }
        Request request = new Request.Builder().url(heartbeatUrl).headers(reqHeaders).get().build();
        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return Available.builder().available(false).message(response.message()).build();
            }
            return Available.builder().available(true).message(String.format(SUCCESS_TEMPLATE, endpoint, heartbeat))
                    .build();
        } catch (IOException e) {
            log.error("请求失败", e);
        }
        return Available.builder().available(false).message("请求失败").build();
    }
    
    
    @Override
    public Object invoke(InvokeContext context) {
        return null;
    }
    
    @Override
    public void destroy() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        client.dispatcher().cancelAll();
    }
}
