package org.gaius.octopus.core.controller;

import jakarta.annotation.Resource;
import org.gaius.octopus.core.execute.AbstractExecuteEngine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaius.zhao
 * @date 2024/5/24
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private AbstractExecuteEngine datasourceExecuteEngine;

    @RequestMapping("/test")
    public String test() {
        datasourceExecuteEngine.invoke(null);
        return "test";
    }
}
