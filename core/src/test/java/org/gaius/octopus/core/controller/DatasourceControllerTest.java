package org.gaius.octopus.core.controller;

import org.gaius.octopus.common.utils.JacksonUtil;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DatasourceController.class)
@ComponentScan(basePackages = "org.gaius.octopus")
class DatasourceControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void available() throws Exception {
        Map<String, Object> datasourceInfo = Map.of("host", "10.2.2.153", "port", 18103, "user", "Rootmaster",
                "password", "Rootmaster@777", "database", "cw_doau", "driverClass", "com.mysql.cj.jdbc.Driver",
                "urlFormat",
                "jdbc:mysql://${host}:${port}/${database}?serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true");
        DatasourceDTO requestBody = new DatasourceDTO();
        requestBody.setContent(datasourceInfo);
        requestBody.setDatasourceType("mysqlPlugin");
        // 将Java对象转换为JSON字符串
        String requestBodyJson = JacksonUtil.writeObjectToString(requestBody);
        mockMvc.perform(
                        post("/api/v1/datasource/test").contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isOk()).andExpect(content().json("{\"available\":true,\"message\":\"success\"}"));
    }
}