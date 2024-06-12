package org.gaius.octopus.plugin.mysql.request;

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
import java.util.StringJoiner;

/**
 * http get请求构建
 *
 * @author gaius.zhao
 * @date 2024/6/12
 */
public class HttpGetRequestBuilder extends AbstractHttpRequestBuilder {
    
    /**
     * 请求参数拼接符
     */
    private final static String PARAM_JOINER = "%s=%s";
    
    @Override
    protected Request build(Map<String, Object> content, String url, Headers headers, Map<String, Object> args) {
        // 获取参数列表
        String finalUrl = url;
        List<Map<String, Object>> params = HTTPUtil.getParams(content);
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
            finalUrl = finalUrl + "?" + paramsJoiner;
        }
        return new Request.Builder().headers(headers).url(finalUrl).get().build();
    }
}
