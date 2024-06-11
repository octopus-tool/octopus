package org.gaius.octopus.core.controller;

import org.gaius.octopus.common.utils.JacksonUtil;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DatasourceController.class)
@Tag("use-container")
@ComponentScan(basePackages = "org.gaius.octopus.**")
@MapperScan(basePackages = "org.gaius.octopus.core.mapper")
@AutoConfigureMybatis
class DatasourceControllerTest {
    
    private static MySQLContainer mysql;
    
    @BeforeAll
    static void initContainer() {
        mysql = new MySQLContainer<>(DockerImageName.parse("mysql:5.7.34"));
        mysql.withPassword("123456");
        mysql.withUsername("root");
        mysql.withDatabaseName("test");
        mysql.start();
    }
    
    @AfterAll
    static void tearDown() {
        mysql.stop();
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void available() throws Exception {
        Integer mysqlMappedPort = mysql.getMappedPort(MySQLContainer.MYSQL_PORT);
        
        Map<String, Object> datasourceInfo = Map.of("host", mysql.getHost(), "port", mysqlMappedPort, "user",
                mysql.getUsername(), "password", mysql.getPassword(), "database", mysql.getDatabaseName(),
                "driverClass", "com.mysql.cj.jdbc.Driver", "urlFormat",
                "jdbc:mysql://${host}:${port}/${database}?serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true");
        DatasourceDTO requestBody = new DatasourceDTO();
        requestBody.setId(1L);
        requestBody.setContent(datasourceInfo);
        requestBody.setDatasourceType("mysqlPlugin");
        // 将Java对象转换为JSON字符串
        String requestBodyJson = JacksonUtil.writeObjectToString(requestBody);
        mockMvc.perform(
                        post("/api/v1/datasource/test").contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isOk()).andExpect(content().json("{\"code\":200,\"message\":\"success\",\"data\":{\"available\":true,\"message\":\"MySQL 5.7.34 连接成功\"},\"traceId\":null}"));
    }
}