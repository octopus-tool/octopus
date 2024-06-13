package org.gaius.octopus.common.middle;

/**
 * 加解密工具类
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
public interface CryptoService {
    /**
     * 加密
     *
     * @param content 待加密内容
     * @return 加密后内容
     */
    String encrypt(String content);

    /**
     * 解密
     *
     * @param content 待解密内容
     * @return 解密后内容
     */
    String decrypt(String content);
}
