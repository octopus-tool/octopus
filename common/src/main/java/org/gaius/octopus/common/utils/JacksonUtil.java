package org.gaius.octopus.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Jackson工具类
 *
 * @author zhaobo
 * @program octopus
 * @description
 * @date 2024/6/8
 */
public class JacksonUtil {
    
    public static ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.findAndRegisterModules();
    }
    
    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static <T> T readValue(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将对象转换为json字符串
     *
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
