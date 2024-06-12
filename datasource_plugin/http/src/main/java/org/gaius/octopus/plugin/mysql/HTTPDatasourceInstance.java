package org.gaius.octopus.plugin.mysql;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaius.datasource.Available;
import org.gaius.datasource.DatasourceInstance;
import org.gaius.datasource.InvokeContext;
import org.gaius.datasource.ServiceContext;
import org.gaius.datasource.exception.DatasourceException;
import org.gaius.datasource.model.DatasourceProperties;
import org.gaius.octopus.common.middle.CacheService;
import org.gaius.octopus.common.utils.JacksonUtil;
import org.gaius.octopus.plugin.mysql.constant.HTTPConstant;
import org.gaius.octopus.plugin.mysql.request.HttpRequestBuilderFactory;
import org.gaius.octopus.plugin.mysql.utils.HTTPUtil;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http数据源
 * <p>以下为basic认证数据格式</p>
 * <blockquote>
 * <pre>
 * {
 *   "endpoint": "https://org.gaius.com",
 *   "header": [
 *     { "key": "Content-type", "value": "application/json", "desc": "编码" }
 *   ],
 *   "heartbeat": "/heartbeat",
 *   "credentialModel": "basic",
 *   "credentials": {
 *     "username": "xxx",
 *     "password": "xxx"
 *   }
 * }
 * </pre>
 * </blockquote>
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
    
    /**
     * http客户端
     */
    private final OkHttpClient client;
    
    /**
     * 自定义认证对象
     */
    private final CustomAuthentication customAuthentication;
    
    /**
     * 测试连接成功
     */
    private static final String SUCCESS_TEMPLATE = "%s %s 连接成功";
    
    public HTTPDatasourceInstance(DatasourceProperties properties) {
        this.properties = properties;
        this.client = init(properties);
        this.customAuthentication = new CustomAuthentication(this.client, properties);
    }
    
    
    /**
     * 初始化http客户端
     *
     * @param properties 数据源连接参数
     * @return
     */
    private OkHttpClient init(DatasourceProperties properties) {
        log.info("初始化http数据源");
        Map<String, Object> content = properties.getContent();
        long timeout = MapUtils.getLongValue(content, HTTPConstant.TIMEOUT, HTTPConstant.DEFAULT_TIMEOUT);
        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).build();
        // 获取认证协议；basic协议，自定义、none
        String credentials = MapUtils.getString(content, HTTPConstant.CREDENTIAL_MODEL, CredentialModel.NONE.getType());
        CredentialModel credentialModel = CredentialModel.resolve(credentials);
        if (credentialModel == CredentialModel.BASIC) {
            return generateBasicAuthClient(httpClient, content);
        }
        return httpClient;
    }
    
    /**
     * 生效basic认证协议客户端
     *
     * @param httpClient http客户端
     * @param content    数据源配置
     * @return
     */
    private static OkHttpClient generateBasicAuthClient(OkHttpClient httpClient, Map<String, Object> content) {
        Map<String, Object> credentialsObject = HTTPUtil.getCredentialsObject(content);
        // 获取认证用户名
        String username = MapUtils.getString(credentialsObject, HTTPConstant.USERNAME);
        // 获取认证密码
        String password = MapUtils.getString(credentialsObject, HTTPConstant.PASSWORD);
        if (StringUtils.isAnyEmpty(username, password)) {
            log.warn("basic认证用户名或密码为空，请检查配置");
            return httpClient;
        }
        // 基于basic认证生成认证头
        String auth = String.format("%s:%s", username, password);
        String basicCredentials = Base64.getEncoder().encodeToString(auth.getBytes());
        Interceptor credentialInterceptor = (chain) -> {
            Request request = chain.request();
            String authValue = HTTPConstant.BASIC_AUTH_FORMAT.formatted(basicCredentials);
            Headers headers = request.headers().newBuilder().add(HTTPConstant.BASIC_AUTH_HEADER, authValue).build();
            return chain.proceed(request.newBuilder().headers(headers).build());
        };
        return httpClient.newBuilder().addInterceptor(credentialInterceptor).build();
    }
    
    @Override
    public Available available(ServiceContext context) {
        // 测试连接
        Map<String, Object> content = properties.getContent();
        // 获取心跳地址
        String heartbeat = MapUtils.getString(content, HTTPConstant.HEARTBEAT);
        // 如果未设置心跳地址，则返回
        if (heartbeat == null) {
            log.info("未配置心跳地址，默认成功");
            return Available.builder().available(true).message("未设置心跳地址").build();
        }
        String endpoint = MapUtils.getString(content, HTTPConstant.ENDPOINT);
        String heartbeatUrl = String.format("%s%s", endpoint, heartbeat);
        log.info("heartbeatUrl:{} 基于心跳地址校验服务是否正常", heartbeatUrl);
        Headers reqHeaders = HTTPUtil.getReqHeaders(content);
        Request request = new Request.Builder().url(heartbeatUrl).headers(reqHeaders).get().build();
        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return Available.builder().available(false).message(response.message()).build();
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return Available.builder().available(true).message(String.format(SUCCESS_TEMPLATE, endpoint, heartbeat))
                        .build();
            }
            return Available.builder().available(true).message(responseBody.string()).build();
        } catch (IOException e) {
            log.error("请求失败", e);
            return Available.builder().available(false).message(e.getMessage()).build();
        }
    }
    
    
    @Override
    public Object invoke(InvokeContext context) throws DatasourceException {
        // 若自定义认证对象不为空，则获取认证结果
        if (customAuthentication != null) {
            Map<String, Object> authObjectMap = customAuthentication.getResult(context);
            Map<String, Object> args = context.getArgs();
            if (MapUtils.isNotEmpty(authObjectMap)) {
                args.put(HTTPConstant.CREDENTIAL, authObjectMap);
            }
        }
        Map<String, Object> interfaceInfo = context.getInterfaceInfo();
        String endpoint = MapUtils.getString(properties.getContent(), HTTPConstant.ENDPOINT);
        List<Map<String, Object>> interfaceHeaders = HTTPUtil.getHeaders(interfaceInfo);
        List<Map<String, Object>> datasourceHeaders = HTTPUtil.getHeaders(properties.getContent());
        // 与数据源请求头进行合并
        mergeHeaders(interfaceHeaders, datasourceHeaders);
        Request request = HttpRequestBuilderFactory.requestBuild(endpoint, interfaceInfo, context.getArgs());
        if (request == null) {
            throw new DatasourceException("请求失败");
        }
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new DatasourceException(
                        String.format("请求失败,code:%s,message:%s", response.code(), response.message()));
            }
            // 提取响应结果
            String extract = MapUtils.getString(interfaceInfo, HTTPConstant.EXTRACT, "$");
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            return JacksonUtil.jsonpathToClass(responseBody.string(), extract);
        } catch (Exception e) {
            throw new DatasourceException(e);
        }
    }
    
    /**
     * 合并接口与数据源请求头,若数据源与接口中存在相同的key，则接口请求头优先级更高
     *
     * @param interfaceHeaders  接口请求头
     * @param datasourceHeaders 数据源请求头
     * @return
     */
    private void mergeHeaders(List<Map<String, Object>> interfaceHeaders, List<Map<String, Object>> datasourceHeaders) {
        if (CollectionUtils.isEmpty(interfaceHeaders)) {
            return;
        }
        if (CollectionUtils.isEmpty(datasourceHeaders)) {
            return;
        }
        // 将interfaceHeaders转换为key，value格式
        Map<String, Object> interfaceHeaderMap = new HashMap<>();
        interfaceHeaders.forEach(header -> {
            String key = MapUtils.getString(header, HTTPConstant.KEY);
            if (StringUtils.isEmpty(key)) {
                return;
            }
            String value = MapUtils.getString(header, HTTPConstant.VALUE);
            interfaceHeaderMap.put(key, value);
        });
        datasourceHeaders.forEach(header -> {
            String key = MapUtils.getString(header, HTTPConstant.KEY);
            if (StringUtils.isEmpty(key)) {
                return;
            }
            if (interfaceHeaderMap.containsKey(key)) {
                // 若接口请求头中存在key，则接口请求头优先级更高
                return;
            }
            interfaceHeaders.add(header);
        });
    }
    
    @Override
    public void destroy() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        client.dispatcher().cancelAll();
    }
    
    /**
     * 自定义认证对象
     */
    public static class CustomAuthentication {
        
        /**
         * 缓存key
         */
        private static final String AUTH_CACHE_KEY = "ds:%s:%d";
        
        /**
         * http客户端
         */
        private final OkHttpClient client;
        
        /**
         * 数据源配置
         */
        private final DatasourceProperties properties;
        
        public CustomAuthentication(OkHttpClient client, DatasourceProperties properties) {
            this.client = client;
            this.properties = properties;
        }
        
        /**
         * 获取自定义认证对象认证结果
         * <p>
         * 若自定义认证模式中，过期时间设置为0，则每次请求都重新获取认证结果。若为-1，则认证结果进行缓存并且不过期
         * </p>
         *
         * <p>以下为自定义json格式</p>
         * <blockquote>
         * <pre>
         * {
         *   "endpoint": "https://org.gaius.com",
         *   "headers": [
         *     { "key": "Content-type", "value": "application/json", "desc": "编码" }
         *   ],
         *   "heartbeat": "/heartbeat",
         *   "credentialModel": "custom",
         *   "credentials": {
         *     "path": "/login",
         *     "method": "post",
         *     "contentType":"application/json",
         *     "body": {
         *         "username": "xxx",
         *         "password": "xxx"
         *     }
         *   }
         * }
         * </pre>
         * </blockquote>
         *
         * <p>以下为form表单认证格式</p>
         * <blockquote>
         * <pre>
         * {
         *   "endpoint": "https://org.gaius.com",
         *   "headers": [
         *     { "key": "Content-type", "value": "application/json", "desc": "编码" }
         *   ],
         *   "heartbeat": "/heartbeat",
         *   "credentialModel": "custom",
         *   "credentials": {
         *     "path": "/login",
         *     "method": "post",
         *     "contentType": "application/x-www-form-urlencoded",
         *     "params": [
         *       { "key": "username", "value": "xxx", "desc": "用户名" },
         *       { "key": "password", "value": "xxx", "desc": "密码" }
         *     ]
         *   }
         * }
         * </pre>
         * </blockquote>
         *
         * @param invokeContext 调用上下文
         * @return 认证结果
         * @throws DatasourceException 认证异常
         */
        private Map<String, Object> getResult(InvokeContext invokeContext) throws DatasourceException {
            Map<String, Object> content = properties.getContent();
            String credentialModelValue = MapUtils.getString(content, HTTPConstant.CREDENTIAL_MODEL,
                    CredentialModel.NONE.getType());
            CredentialModel credentialModel = CredentialModel.resolve(credentialModelValue);
            if (credentialModel != CredentialModel.CUSTOM) {
                return Collections.emptyMap();
            }
            // 获取当前数据过期时间
            long expiration = MapUtils.getLongValue(content, HTTPConstant.EXPIRATION, 0);
            String redisKey = AUTH_CACHE_KEY.formatted(properties.getTenantId(), properties.getDatasourceId());
            if (expiration != 0) {
                Map<String, Object> authCacheMap = getFromCache(invokeContext, redisKey);
                if (MapUtils.isNotEmpty(authCacheMap)) {
                    return authCacheMap;
                }
            }
            Map<String, Object> credentialsObject = HTTPUtil.getCredentialsObject(content);
            String endpoint = HTTPUtil.getEndpoint(content);
            Request authRequest = HttpRequestBuilderFactory.requestBuild(endpoint, credentialsObject,
                    invokeContext.getArgs());
            if (authRequest == null) {
                throw new DatasourceException("认证请求参数错误");
            }
            try (Response response = client.newCall(authRequest).execute()) {
                return processAuthResponse(invokeContext, response, credentialsObject, expiration, redisKey);
            } catch (IOException e) {
                throw new DatasourceException(e);
            } catch (Exception e) {
                if (e instanceof DatasourceException) {
                    throw e;
                }
                log.error("认证请求失败", e);
                throw new DatasourceException(e);
            }
        }
        
        
        /**
         * 从缓存对象中获取认证对象
         *
         * @param invokeContext 调用上下文
         * @param redisKey      缓存key
         * @return
         */
        private Map<String, Object> getFromCache(InvokeContext invokeContext, String redisKey) {
            CacheService<String> cacheService = invokeContext.getServiceContext().getCacheService();
            return cacheService.hmget(redisKey);
        }
        
        
        /**
         * 处理认证响应
         *
         * @param invokeContext 调用上下文
         * @param response      响应
         * @param content       认证对象
         * @param expiration    过期时间
         * @param redisKey      缓存key
         * @return
         */
        private Map<String, Object> processAuthResponse(InvokeContext invokeContext, Response response,
                Map<String, Object> content, long expiration, String redisKey) throws DatasourceException, IOException {
            if (response.isSuccessful()) {
                Map<String, Object> responseObject = new HashMap<>();
                responseObject.put(HTTPConstant.HEADER, response.headers().toMultimap());
                responseObject.put(HTTPConstant.CODE, response.code());
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String responseBodyStr = responseBody.string();
                    if (StringUtils.isNotEmpty(responseBodyStr)) {
                        Object responseValue = JacksonUtil.parseToTargetObject(responseBodyStr);
                        responseObject.put(HTTPConstant.BODY, responseValue);
                    }
                }
                // 获取认证参数提取配置
                List<Map<String, Object>> extractList = (List<Map<String, Object>>) MapUtils.getObject(content,
                        HTTPConstant.EXTRACT);
                Map<String, Object> authCacheMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(extractList)) {
                    extractList.forEach(extract -> {
                        String key = MapUtils.getString(extract, HTTPConstant.KEY);
                        String value = MapUtils.getString(extract, HTTPConstant.VALUE);
                        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                            // 使用jsonpath提取数据
                            Object extractValue = JacksonUtil.jsonpathToClass(responseObject, value);
                            authCacheMap.put(key, extractValue);
                        }
                    });
                }
                // 过期时间为-1时，表示永不过期
                if (expiration == -1) {
                    invokeContext.getServiceContext().getCacheService().hmset(redisKey, authCacheMap);
                    return authCacheMap;
                }
                // 过期时间为0时，表示不缓存
                if (expiration == 0) {
                    // 删除缓存
                    invokeContext.getServiceContext().getCacheService().del(redisKey);
                    return authCacheMap;
                }
                // 过期时间大于0，设置过期时间
                invokeContext.getServiceContext().getCacheService()
                        .hmset(redisKey, authCacheMap, expiration, TimeUnit.SECONDS);
                return authCacheMap;
            }
            String errorMsg = String.format("认证请求失败, code: %s, message: %s, body:%s", response.code(),
                    response.message(), response.body());
            throw new DatasourceException(errorMsg);
        }
    }
}
