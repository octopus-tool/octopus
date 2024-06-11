package org.gaius.octopus.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Jackson工具类
 *
 * @author zhaobo
 * @program octopus
 * @description
 * @date 2024/6/8
 */
@Slf4j
public class JacksonUtil {
    
    /**
     * date format of yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 常规mapper
     */
    private static final JsonMapper MAPPER;
    
    static {
        JsonFactory jsonFactory = new JsonFactoryBuilder().disable(JsonFactory.Feature.INTERN_FIELD_NAMES)
                .enable(JsonFactory.Feature.CANONICALIZE_FIELD_NAMES).disable(JsonFactory.Feature.CHARSET_DETECTION)
                .build();
        MAPPER = JsonMapper.builder(jsonFactory).enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS).enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).disable(SerializationFeature.INDENT_OUTPUT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).serializationInclusion(JsonInclude.Include.ALWAYS)
                .findAndAddModules().build();
        MAPPER.setDateFormat(new java.text.SimpleDateFormat(YYYY_MM_DD_HH_MM_SS));
    }
    
    
    private JacksonUtil() {
    }
    
    /**
     * jsonNode对象转为json字符串
     *
     * @param node jsonNode对象
     * @return
     */
    public static String toPrettyString(JsonNode node) {
        if (Objects.isNull(node)) {
            return null;
        }
        if (node instanceof ObjectNode || node instanceof ArrayNode) {
            return node.toString();
        } else {
            return node.asText();
        }
    }
    
    /**
     * JSON 字符串转 JSON 对象/数组
     *
     * @param text JSON 字符串
     * @return JSON 对象/数组
     */
    public static JsonNode parseStringToJson(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return MAPPER.readTree(text);
        } catch (IOException e) {
            log.error("JsonNodeUtils parseStringToJson fail,text={}, error is:", text, e);
            throw new IllegalArgumentException("json解析错误", e);
        }
    }
    
    /**
     * JSON 对象转 map
     *
     * @param node JSON 对象
     * @return map
     */
    public static Map<Object, Object> node2Map(JsonNode node) {
        if (node == null || node instanceof ArrayNode) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.convertValue(node, new TypeReference<Map<Object, Object>>() {
            });
        } catch (Exception e) {
            log.error("JsonNodeUtils node2Map fail,{} error is:", e.getMessage(), e);
        }
        return Collections.emptyMap();
    }
    
    /**
     * JSONNode转ListMap
     *
     * @param node
     * @return
     */
    public static List<Map<String, Object>> node2ListMap(JsonNode node) {
        if (node == null || node instanceof ObjectNode) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.convertValue(node, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            log.error("JsonNodeUtils node2Map fail,{} error is:", e.getMessage(), e);
        }
        return Collections.emptyList();
    }
    
    /**
     * JSONNode转List
     *
     * @param node JsonNode
     * @return
     */
    public static List<Object> node2List(JsonNode node) {
        if (node == null || node instanceof ObjectNode) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.convertValue(node, new TypeReference<List<Object>>() {
            });
        } catch (Exception e) {
            log.error("JsonNodeUtils node2List fail,{} error is:", e.getMessage(), e);
        }
        return Collections.emptyList();
    }
    
    /**
     * JSON 字符串转 Java 对象
     *
     * @param text JSON 字符串
     * @param <T>  类型
     * @return 转换结果
     */
    public static <T> T parseToTargetObject(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            return MAPPER.readValue(text, new TypeReference<T>() {
            });
        } catch (IOException e) {
            log.error("JsonNodeUtils parseToTargetObject fail,text={}, error is:", text, e);
        }
        return null;
    }
    
    public static <T> T parseToTargetObject(String text, Class<T> valueType) {
        try {
            return MAPPER.readValue(text, valueType);
        } catch (IOException e) {
            log.error("JsonNodeUtils parseToTargetObject fail,text={}, error is:", text, e);
        }
        return null;
    }
    
    /**
     * JSON 字符串字节数组 Java 对象
     *
     * @param bytes JSON 字符串字节数组
     * @param <T>   类型
     * @return 转换结果
     */
    public static <T> T parseToTargetObject(byte[] bytes) {
        return parseToTargetObject(new String(bytes, StandardCharsets.UTF_8));
    }
    
    /**
     * JSON 字符串字节数组 Java 对象
     *
     * @param bytes JSON 字符串字节数组
     * @param <T>   类型
     * @return 转换结果
     */
    public static <T> T parseToTargetObject(byte[] bytes, Class<T> valueType) {
        return parseToTargetObject(new String(bytes, StandardCharsets.UTF_8), valueType);
    }
    
    /**
     * json 字符串转 List
     *
     * @param text      json
     * @param valueType List 对象
     * @param <T>       List 对象类型
     * @return List<T>
     */
    public static <T> List<T> parseToTargetList(String text, Class<T> valueType) {
        try {
            JavaType jt = MAPPER.getTypeFactory().constructParametricType(ArrayList.class, valueType);
            return MAPPER.readValue(text, jt);
        } catch (IOException e) {
            log.error("JsonNodeUtils parseToTargetList fail,text={}, error is:", text, e);
        }
        return Collections.emptyList();
    }
    
    public static <T> T convertObject(Object o, Class<T> clazz) {
        if (o == null) {
            return null;
        }
        try {
            return MAPPER.convertValue(o, clazz);
        } catch (IllegalArgumentException e) {
            log.error("JsonNodeUtils convertObject fail,text={}, error is:", o, e);
        }
        return null;
    }
    
    
    public static List<String> getObjectNodeSet(ObjectNode data) {
        Iterator<String> keyIterator = data.fieldNames();
        List<String> keyList = Lists.newArrayList();
        while (keyIterator.hasNext()) {
            keyList.add(keyIterator.next());
        }
        return keyList;
    }
    
    /**
     * 将对象转换为字符串（有缩进）
     *
     * @param value 对象
     * @return
     */
    public static String writeObjectToString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("JsonNodeUtils writeObjectToString fail, error is:", e);
        }
        return null;
    }
    
    public static JsonNode writeObjectToNode(Object o) {
        try {
            return MAPPER.convertValue(o, JsonNode.class);
        } catch (IllegalArgumentException e) {
            log.error("JsonNodeUtils writeObjectToNode fail, error is:", e);
        }
        return null;
    }
    
    public static ArrayNode writeObjectToArrayNode(Object o) {
        try {
            return MAPPER.convertValue(o, ArrayNode.class);
        } catch (IllegalArgumentException e) {
            log.error("JsonNodeUtils writeObjectToNode fail, error is:", e);
        }
        return null;
    }
    
    public static <T> List<T> parseArray(Object o, Class<T> clazz) {
        if (o == null) {
            return Collections.emptyList();
        }
        try {
            JavaType jt = MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clazz);
            return MAPPER.convertValue(o, jt);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    /**
     * jsonpath 读取指定配置的数据
     *
     * @param data 原数据
     * @param path 路径
     * @return
     */
    public static JsonNode jsonpath(JsonNode data, String path) {
        return jsonpath(toPrettyString(data), path);
    }
    
    
    /**
     * jsonpath 读取指定配置的数据
     *
     * @param data 原数据
     * @param path 路径
     * @return
     */
    public static JsonNode jsonpath(Object data, String path) {
        if (data instanceof String) {
            return jsonpath((String) data, path);
        }
        return jsonpath(writeObjectToString(data), path);
    }
    
    /**
     * jsonpath 读取指定配置的数据
     *
     * @param data 原数据
     * @param path 路径
     * @return
     */
    public static JsonNode jsonpath(String data, String path) {
        Configuration conf = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider()).build();
        try {
            return JsonPath.using(conf).parse(data).read(path);
        } catch (Exception e) {
            log.error("JsonNodeUtils jsonpath fail, data={}, config={}, error is:", data, path, e);
            return MissingNode.getInstance();
        }
    }
    
    /**
     * jsonpath 读取指定配置的数据
     *
     * @param data 原数据
     * @param path 路径
     * @return 返回Java对象数据
     */
    public static Object jsonpathToClass(Object data, String path) {
        if (data == null) {
            return null;
        }
        JsonNode jsonNode;
        if (data instanceof String) {
            jsonNode = jsonpath((String) data, path);
        } else {
            jsonNode = jsonpath(writeObjectToString(data), path);
        }
        if (jsonNode.isArray()) {
            return node2List(jsonNode);
        }
        if (jsonNode.isObject()) {
            return node2Map(jsonNode);
        }
        JsonNodeType nodeType = jsonNode.getNodeType();
        if (nodeType == JsonNodeType.STRING) {
            return jsonNode.textValue();
        }
        if (nodeType == JsonNodeType.BOOLEAN) {
            return jsonNode.booleanValue();
        }
        if (nodeType == JsonNodeType.NUMBER) {
            return jsonNode.numberValue();
        }
        if (nodeType == JsonNodeType.NULL) {
            return null;
        }
        return jsonNode;
    }
}
