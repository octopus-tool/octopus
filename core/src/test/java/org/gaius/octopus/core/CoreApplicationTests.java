package org.gaius.octopus.core;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = "org.gaius.octopus")
@MapperScan("org.gaius.octopus.core.mapper")
public class CoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
