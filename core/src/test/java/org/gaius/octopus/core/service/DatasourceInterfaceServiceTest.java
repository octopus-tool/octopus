package org.gaius.octopus.core.service;

import jakarta.annotation.Resource;
import org.gaius.octopus.core.CoreApplicationTests;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.gaius.octopus.core.pojo.dto.DatasourceInterfaceDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class DatasourceInterfaceServiceTest extends CoreApplicationTests {
    
    @MockBean
    DatasourceService datasourceService;
    
    @Resource
    DatasourceInterfaceService datasourceInterfaceService;
    
    private static MySQLContainer mysql;
    
    @BeforeAll
    static void initContainer() {
        mysql = new MySQLContainer<>(DockerImageName.parse("mysql:5.7.34"));
        mysql.withPassword("123456");
        mysql.withUsername("root");
        mysql.withDatabaseName("test");
        mysql.withInitScript("init.sql");
        mysql.start();
    }
    
    @AfterAll
    static void tearDown() {
        mysql.stop();
    }
    
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void test1() throws Exception {
        Integer mysqlMappedPort = mysql.getMappedPort(MySQLContainer.MYSQL_PORT);
        
        Map<String, Object> datasourceInfo = Map.of("host", mysql.getHost(), "port", mysqlMappedPort, "user",
                mysql.getUsername(), "password", mysql.getPassword(), "database", mysql.getDatabaseName(),
                "driverClass", "com.mysql.cj.jdbc.Driver", "urlFormat",
                "jdbc:mysql://${host}:${port}/${database}?serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true",
                "pool", true);
        DatasourceDTO datasourceDTO = new DatasourceDTO();
        datasourceDTO.setId(1L);
        datasourceDTO.setContent(datasourceInfo);
        datasourceDTO.setDatasourceType("mysqlPlugin");
        DatasourceInterfaceDTO dto = new DatasourceInterfaceDTO();
        dto.setDatasourceId(1L);
        dto.setName("test");
        dto.setContent(Map.of("sql", "select * from user"));
        Mockito.when(datasourceService.selectById(1L)).thenReturn(datasourceDTO);
        Object object = datasourceInterfaceService.test(dto);
        Assertions.assertInstanceOf(ArrayList.class, object, "返回值类型错误");
    }
}