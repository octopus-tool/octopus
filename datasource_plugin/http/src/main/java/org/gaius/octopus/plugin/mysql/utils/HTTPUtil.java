package org.gaius.octopus.plugin.mysql.utils;

import com.google.common.collect.Maps;
import okhttp3.Headers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaius.octopus.plugin.mysql.constant.HTTPConstant;

import java.util.List;
import java.util.Map;

/**
 * 提取工具类
 *
 * @author gaius.zhao
 * @date 2024/6/11
 */
public class HTTPUtil {
    
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
     * 获取请求方法
     *
     * @param content 请求内容
     * @return
     */
    public static String getMethod(Map<String, Object> content) {
        return MapUtils.getString(content, HTTPConstant.METHOD);
    }
    
    /**
     * 获取请求内容类型
     *
     * @param content 请求内容
     * @return
     */
    public static String getContentType(Map<String, Object> content) {
        return MapUtils.getString(content, HTTPConstant.CONTENT_TYPE);
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
}
