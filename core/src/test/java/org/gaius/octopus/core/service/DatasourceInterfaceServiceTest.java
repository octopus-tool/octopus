package org.gaius.octopus.core.service;

import jakarta.annotation.Resource;
import org.gaius.octopus.core.CoreApplicationTests;
import org.gaius.octopus.core.pojo.dto.DatasourceDTO;
import org.gaius.octopus.core.pojo.dto.DatasourceInterfaceDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class DatasourceInterfaceServiceTest extends CoreApplicationTests {
    
    @MockBean
    DatasourceService datasourceService;
    
    @Resource
    DatasourceInterfaceService datasourceInterfaceService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void test1() throws Exception {
        Map<String, Object> datasourceInfo = Map.of("host", "10.2.2.153", "port", 18103, "user", "Rootmaster", "password",
                "Rootmaster@777", "database", "cw_doau", "driverClass", "com.mysql.cj.jdbc.Driver", "urlFormat",
                "jdbc:mysql://${host}:${port}/${database}?serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true",
                "pool", true);
        DatasourceDTO datasourceDTO = new DatasourceDTO();
        datasourceDTO.setId(1L);
        datasourceDTO.setContent(datasourceInfo);
        datasourceDTO.setDatasourceType("mysqlPlugin");
        DatasourceInterfaceDTO dto = new DatasourceInterfaceDTO();
        dto.setDatasourceId(1L);
        dto.setName("test");
        dto.setContent(Map.of("sql", "select * from cw_doau.aut_script"));
        Mockito.when(datasourceService.selectById(1L)).thenReturn(datasourceDTO);
        Object object = datasourceInterfaceService.test(dto);
        Assertions.assertInstanceOf(object.getClass(), ArrayList.class, "返回值类型错误");
    }
}