package org.gaius.octopus.plugin.mysql;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 凭证模式
 *
 * @author gaius.zhao
 * @date 2024/6/11
 */
@Getter
public enum CredentialModel {
    /**
     * 无
     */
    NONE(1, "none"),
    /**
     * basic认证协议
     */
    BASIC(2, "basic"),
    /**
     * 自定义
     */
    CUSTOM(3, "custom"),
    ;
    
    CredentialModel(int value, String type) {
        this.value = value;
        this.type = type;
    }
    
    private final int value;
    
    private final String type;
    
    
    private static final Map<String, CredentialModel> MAPPINGS = new HashMap<>(16);
    
    static {
        for (CredentialModel targetEnum : values()) {
            MAPPINGS.put(targetEnum.type, targetEnum);
        }
    }
    
    public static CredentialModel resolve(String type) {
        return (type != null ? MAPPINGS.get(type) : null);
    }
    
}
