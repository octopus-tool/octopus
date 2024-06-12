package org.gaius.octopus.plugin.mysql.request;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections4.MapUtils;
import org.gaius.datasource.utils.TemplateUtil;
import org.gaius.octopus.plugin.mysql.constant.HTTPConstant;
import org.gaius.octopus.plugin.mysql.utils.HTTPUtil;

import java.util.Map;

/**
 * json请求对象构建
 * <p>content-type为application/json</p>
 *
 * @author gaius.zhao
 * @date 2024/6/12
 */
public class HttpJsonBodyRequestBuilder extends AbstractHttpRequestBuilder {
    
    @Override
    public Request build(Map<String, Object> content, String url, Headers headers, Map<String, Object> args) {
        String contentType = HTTPUtil.getContentType(content);
        MediaType mediaType = MediaType.parse(contentType);
        // 获取body参数
        String body = MapUtils.getString(content, HTTPConstant.BODY);
        String formatBody = TemplateUtil.render(body, args);
        RequestBody requestBody = RequestBody.create(formatBody, mediaType);
        return new Request.Builder().headers(headers).url(url).post(requestBody).build();
    }
}
