package org.gaius.octopus.plugin.mysql.utils;

import com.google.common.collect.Maps;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaius.datasource.utils.TemplateUtil;
import org.gaius.octopus.plugin.mysql.constant.HTTPConstant;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 提取工具类
 *
 * @author gaius.zhao
 * @date 2024/6/11
 */
public class HTTPUtil {
    
    /**
     * 请求参数拼接符
     */
    private final static String PARAM_JOINER = "%s=%s";
    
    /**
     * 获取请求地址
     *
     * @param content 数据源配置
     * @return
     */
    public static String getEndpoint(Map<String, Object> content) {
        return MapUtils.getString(content, HTTPConstant.ENDPOINT);
    }
    
    /**
     * 获取请求头
     *
     * @param content 数据源配置
     * @return
     */
    public static Headers getReqHeaders(Map<String, Object> content) {
        List<Map<String, Object>> headers = getHeaders(content);
        Headers reqHeaders;
        if (CollectionUtils.isNotEmpty(headers)) {
            Map<String, String> headerMap = Maps.newHashMapWithExpectedSize(headers.size());
            headers.forEach(header -> {
                String key = MapUtils.getString(header, HTTPConstant.KEY);
                String value = MapUtils.getString(header, HTTPConstant.VALUE);
                if (StringUtils.isNoneEmpty(key, value)) {
                    headerMap.put(key.trim(), value.trim());
                }
            });
            reqHeaders = Headers.of(headerMap);
        } else {
            reqHeaders = new Headers.Builder().build();
        }
        return reqHeaders;
    }
    
    /**
     * 获取请求头
     *
     * @param content 数据源配置
     * @return
     */
    public static List<Map<String, Object>> getHeaders(Map<String, Object> content) {
        return (List<Map<String, Object>>) MapUtils.getObject(content, HTTPConstant.HEADER);
    }
    
    /**
     * 获取认证信息
     *
     * @param content 数据源配置
     * @return
     */
    public static Map<String, Object> getCredentialsObject(Map<String, Object> content) {
        return (Map<String, Object>) MapUtils.getMap(content, HTTPConstant.CREDENTIALS);
    }
    
    /**
     * 获取参数对象列表
     *
     * @param content 配置对象集合
     * @return
     */
    public static List<Map<String, Object>> getParams(Map<String, Object> content) {
        return (List<Map<String, Object>>) MapUtils.getObject(content, HTTPConstant.PARAMS);
    }
    
    
    /**
     * 获取认证请求
     *
     * @param endpoint       认证地址
     * @param requestContent 请求对象
     * @param args           参数对象
     * @return
     */
    public static Request requestBuild(String endpoint, Map<String, Object> requestContent, Map<String, Object> args) {
        List<Map<String, Object>> headers = getHeaders(requestContent);
        Headers reqHeaders = formatReqHeader(headers, args);
        // 如果请求方式为get，则直接获取参数参数列表
        String method = MapUtils.getString(requestContent, HTTPConstant.METHOD);
        String path = MapUtils.getString(requestContent, HTTPConstant.PATH);
        String formatPath = TemplateUtil.render(path, args);
        String finalPath = endpoint + formatPath;
        if (!HttpMethod.permitsRequestBody(method)) {
            // 获取参数列表
            List<Map<String, Object>> params = getParams(requestContent);
            if (CollectionUtils.isNotEmpty(params)) {
                StringJoiner paramsJoiner = new StringJoiner("&");
                params.forEach(param -> {
                    String key = MapUtils.getString(param, HTTPConstant.KEY);
                    if (StringUtils.isEmpty(key)) {
                        return;
                    }
                    String value = MapUtils.getString(param, HTTPConstant.VALUE);
                    String finalKey = TemplateUtil.render(key.trim(), args);
                    String finalValue = TemplateUtil.render(value.trim(), args);
                    paramsJoiner.add(PARAM_JOINER.formatted(finalKey, finalValue));
                });
                finalPath = finalPath + "?" + paramsJoiner;
            }
            return new Request.Builder().headers(reqHeaders).url(finalPath).get().build();
        }
        String contentType = MapUtils.getString(requestContent, HTTPConstant.CONTENT_TYPE);
        MediaType mediaType = MediaType.parse(contentType);
        // 如果为json
        if (StringUtils.equalsIgnoreCase(contentType, "application/json")) {
            // 获取body参数
            String body = MapUtils.getString(requestContent, HTTPConstant.BODY);
            String formatBody = TemplateUtil.render(body, args);
            RequestBody requestBody = RequestBody.create(formatBody, mediaType);
            return new Request.Builder().headers(reqHeaders).url(finalPath).post(requestBody).build();
        }
        if (StringUtils.equalsIgnoreCase(contentType, "application/x-www-form-urlencoded")) {
            // 获取body参数
            List<Map<String, Object>> params = HTTPUtil.getParams(requestContent);
            if (CollectionUtils.isNotEmpty(params)) {
                FormBody.Builder builder = new FormBody.Builder();
                params.forEach(param -> {
                    String key = MapUtils.getString(param, HTTPConstant.KEY);
                    if (StringUtils.isEmpty(key)) {
                        return;
                    }
                    String value = MapUtils.getString(param, HTTPConstant.VALUE);
                    String finalKey = TemplateUtil.render(key.trim(), args);
                    String finalValue = TemplateUtil.render(value.trim(), args);
                    builder.add(finalKey, finalValue);
                });
                RequestBody requestBody = builder.build();
                return new Request.Builder().headers(reqHeaders).url(finalPath).post(requestBody).build();
            }
        }
        if (StringUtils.equalsIgnoreCase(contentType, "multipart/form-data")) {
            List<Map<String, Object>> params = HTTPUtil.getParams(requestContent);
            if (CollectionUtils.isNotEmpty(params)) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                params.forEach(param -> {
                    String key = MapUtils.getString(param, HTTPConstant.KEY);
                    if (StringUtils.isEmpty(key)) {
                        return;
                    }
                    String value = MapUtils.getString(param, HTTPConstant.VALUE);
                    String finalKey = TemplateUtil.render(key.trim(), args);
                    String finalValue = TemplateUtil.render(value.trim(), args);
                    builder.addFormDataPart(finalKey, finalValue);
                });
                RequestBody requestBody = builder.build();
                return new Request.Builder().headers(reqHeaders).url(finalPath).post(requestBody).build();
            }
        }
        return null;
    }
    
    /**
     * 格式化请求头
     *
     * @param headers 请求头
     * @param args    参数对象
     * @return
     */
    private static Headers formatReqHeader(List<Map<String, Object>> headers, Map<String, Object> args) {
        if (CollectionUtils.isNotEmpty(headers)) {
            Map<String, String> headerMap = Maps.newHashMapWithExpectedSize(headers.size());
            headers.forEach(header -> {
                String key = MapUtils.getString(header, HTTPConstant.KEY);
                String value = MapUtils.getString(header, HTTPConstant.VALUE);
                if (StringUtils.isEmpty(key)) {
                    return;
                }
                String finalKey = TemplateUtil.render(key.trim(), args);
                String finalValue = TemplateUtil.render(value.trim(), args);
                headerMap.put(finalKey, finalValue);
            });
            return Headers.of(headerMap);
        }
        return new Headers.Builder().build();
    }
}
