package org.gaius.datasource.utils;

import groovy.text.GStringTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 模版渲染工具类
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@Slf4j
public class TemplateUtil {
    
    // 模版引擎
    private static final GStringTemplateEngine TEMPLATE_ENGINE;
    
    static {
        TEMPLATE_ENGINE = new GStringTemplateEngine();
    }
    
    
    /**
     * 格式化模版
     *
     * @param template 模版
     * @param params   变量
     * @return
     */
    public static String render(String template, Map<String, Object> params) {
        try {
            return TEMPLATE_ENGINE.createTemplate(template).make(params).toString();
        } catch (Exception e) {
            log.error("模版格式化异常，template:{}", template, e);
        }
        return template;
    }
}
