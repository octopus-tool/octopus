package org.gaius.octopus.core.middle;

import org.gaius.octopus.common.middle.CryptoService;
import org.springframework.stereotype.Service;

/**
 * @author zhaobo
 * @program octopus
 * @description 国密加解密
 * @date 2024/6/7
 */
@Service
public class SmCryptoService implements CryptoService {
    
    @Override
    public String encrypt(String content) {
        return content;
    }
    
    @Override
    public String decrypt(String content) {
        return content;
    }
}
