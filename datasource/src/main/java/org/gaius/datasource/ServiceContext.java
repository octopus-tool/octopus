package org.gaius.datasource;

import lombok.Data;
import org.gaius.octopus.common.middle.CacheService;
import org.gaius.octopus.common.middle.CryptoService;

/**
 * 服务上下文
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
@Data
public class ServiceContext {
    /**
     * 缓存工具类
     */
    public CacheService<String> cacheService;
    /**
     * 加解密工具类
     */
    public CryptoService cryptoService;
}
