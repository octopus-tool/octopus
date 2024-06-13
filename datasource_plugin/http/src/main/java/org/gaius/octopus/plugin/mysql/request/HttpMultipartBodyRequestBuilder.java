package org.gaius.octopus.plugin.mysql.request;

import okhttp3.Headers;
import okhttp3.MultipartBody;
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
 * form表单请求对象构建
 * <p>content-type为multipart/form-data</p>
 *
 * @author gaius.zhao
 * @date 2024/6/12
 */
public class HttpMultipartBodyRequestBuilder extends AbstractHttpRequestBuilder {
    
    @Override
    public Request build(Map<String, Object> content, String url, Headers headers, Map<String, Object> args) {
        List<Map<String, Object>> params = HTTPUtil.getParams(content);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (CollectionUtils.isNotEmpty(params)) {
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
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().headers(headers).url(url).post(requestBody).build();
    }
}
