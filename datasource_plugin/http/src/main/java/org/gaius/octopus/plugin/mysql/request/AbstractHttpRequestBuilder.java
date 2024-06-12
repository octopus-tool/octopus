package org.gaius.octopus.plugin.mysql.request;

import com.google.common.collect.Maps;
import okhttp3.Headers;
import okhttp3.Request;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaius.datasource.utils.TemplateUtil;
import org.gaius.octopus.plugin.mysql.constant.HTTPConstant;
import org.gaius.octopus.plugin.mysql.utils.HTTPUtil;

import java.util.List;
import java.util.Map;

/**
 * http请求对象
 *
 * @author gaius.zhao
 * @date 2024/6/12
 */
public abstract class AbstractHttpRequestBuilder {
    
    /**
     * 构建http请求
     *
     * @param endpoint 接口总端
     * @param content  请求接口配置
     * @param args     请求参数
     * @return
     */
    public Request build(String endpoint, Map<String, Object> content, Map<String, Object> args) {
        List<Map<String, Object>> headers = HTTPUtil.getHeaders(content);
        Headers reqHeaders = formatReqHeader(headers, args);
        String path = MapUtils.getString(content, HTTPConstant.PATH);
        String formatPath = TemplateUtil.render(path, args);
        String url = endpoint + formatPath;
        return build(content, url, reqHeaders, args);
    }
    
    
    /**
     * 格式化请求头
     *
     * @param headers 请求头
     * @param args    参数对象
     * @return
     */
    private Headers formatReqHeader(List<Map<String, Object>> headers, Map<String, Object> args) {
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
    
    
    /**
     * 构建http请求
     *
     * @param content 请求对象
     * @param url     地址
     * @param headers 请求头
     * @param args    参数
     * @return
     */
    protected abstract Request build(Map<String, Object> content, String url, Headers headers,
            Map<String, Object> args);
    
}
