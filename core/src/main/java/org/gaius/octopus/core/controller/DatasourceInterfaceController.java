package org.gaius.octopus.core.controller;

import jakarta.annotation.Resource;
import org.gaius.octopus.common.model.Result;
import org.gaius.octopus.core.pojo.dto.DatasourceInterfaceDTO;
import org.gaius.octopus.core.service.DatasourceInterfaceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据源接口控制器
 *
 * @author zhaobo
 * @program octopus
 * @description 数据源接口控制器
 * @date 2024/6/7
 */
@RestController
@RequestMapping("/api/v1/datasource/interface")
public class DatasourceInterfaceController {
    
    @Resource
    private DatasourceInterfaceService datasourceInterfaceService;
    
    /**
     * 数据源参数校验
     *
     * @param dto 数据源参数
     * @return
     */
    @PostMapping("test")
    public Result<Object> available(@RequestBody DatasourceInterfaceDTO dto) throws Exception {
        return Result.success(datasourceInterfaceService.test(dto));
    }
    
}
