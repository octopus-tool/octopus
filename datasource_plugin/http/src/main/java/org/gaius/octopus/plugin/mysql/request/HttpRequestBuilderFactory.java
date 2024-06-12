package org.gaius.octopus.plugin.mysql.request;

import okhttp3.Request;
import okhttp3.internal.http.HttpMethod;
import org.gaius.octopus.plugin.mysql.utils.HTTPUtil;

import java.util.Map;

/**
 * http请求对象构建工厂
 *
 * @author gaius.zhao
 * @date 2024/6/12
 */
public class HttpRequestBuilderFactory {
    
    /**
     * 获取http请求对象构建器
     *
     * @param endpoint http终端
     * @param content  接口内容
     * @param args     参数
     * @return
     */
    public static Request requestBuild(String endpoint, Map<String, Object> content, Map<String, Object> args) {
        String method = HTTPUtil.getMethod(content);
        if (HttpMethod.permitsRequestBody(method)) {
            String contentType = HTTPUtil.getContentType(content);
            switch (contentType) {
                case "application/json":
                    return new HttpJsonBodyRequestBuilder().build(endpoint, content, args);
                case "x-www-form-urlencoded":
                    return new HttpFormBodyRequestBuilder().build(endpoint, content, args);
                case "multipart/form-data":
                    return new HttpMultipartBodyRequestBuilder().build(endpoint, content, args);
            }
        }
        return new HttpGetRequestBuilder().build(endpoint, content, args);
    }
}
