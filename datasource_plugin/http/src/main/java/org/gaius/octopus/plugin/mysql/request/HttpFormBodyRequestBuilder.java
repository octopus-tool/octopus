package org.gaius.octopus.plugin.mysql.request;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaius.datasource.utils.TemplateUtil;
import org.gaius.octopus.plugin.mysql.constant.HTTPConstant;
import org.gaius.octopus.plugin.mysql.utils.HTTPUtil;

import java.util.List;
import java.util.Map;

/**
 * 表单请求对象构建
 * <p>content-type为application/x-www-form-urlencoded</p>
 *
 * @author gaius.zhao
 * @date 2024/6/12
 */
public class HttpFormBodyRequestBuilder extends AbstractHttpRequestBuilder {
    
    @Override
    public Request build(Map<String, Object> content, String url, Headers headers, Map<String, Object> args) {
        // 获取body参数
        List<Map<String, Object>> params = HTTPUtil.getParams(content);
        FormBody.Builder builder = new FormBody.Builder();
        if (CollectionUtils.isNotEmpty(params)) {
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
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().headers(headers).url(url).post(requestBody).build();
    }
}
