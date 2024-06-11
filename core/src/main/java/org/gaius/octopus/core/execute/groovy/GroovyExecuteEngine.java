package org.gaius.octopus.core.execute.groovy;

import com.google.common.collect.Maps;
import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.gaius.datasource.Available;
import org.gaius.datasource.exception.DatasourceException;
import org.gaius.octopus.core.execute.AbstractExecuteEngine;
import org.gaius.octopus.core.execute.ExecuteContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * groovy执行引擎
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
@Service
@Slf4j
public class GroovyExecuteEngine extends AbstractExecuteEngine<String> {
    
    private static CompilerConfiguration compilerConfiguration;
    
    static {
        initCompilerConfiguration();
    }
    
    @Override
    public Available validate(ExecuteContext<String> content) throws DatasourceException {
        
        return null;
    }
    
    @Override
    public Object invoke(ExecuteContext<String> context) {
        String scriptText = context.getContent();
        if (StringUtils.isEmpty(scriptText)) {
            throw new IllegalArgumentException("脚本内容为空");
        }
        try {
            // 计算脚本md5值作为key
            String scriptKey = DigestUtils.md5Hex(scriptText);
            GroovyShell groovyShell = new GroovyShell(compilerConfiguration);
            GroovyCodeSource gcs = new GroovyCodeSource(scriptText, scriptKey, "/groovy/script");
            Script script = (Script) groovyShell.getClassLoader().parseClass(gcs).newInstance();
            Binding binding = wrapperGlobalVariables(context);
            script.setBinding(binding);
            return script.run();
        } catch (Exception e) {
            if (e instanceof CompilationFailedException) {
                log.error("脚本编译失败", e);
            }
            throw new IllegalArgumentException("脚本执行异常", e);
        }
    }
    
    @Override
    public void destroy(ExecuteContext<String> context) {
        // do nothing
    }
    
    /**
     * 初始化groovy配置参数
     *
     * @return
     */
    private static void initCompilerConfiguration() {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setTargetBytecode(CompilerConfiguration.JDK8);
        // 脚本配置
        Properties properties = new Properties();
        try {
            InputStream stream = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + "groovy.properties")
                    .openStream();
            properties.load(stream);
        } catch (IOException e) {
            log.error("读取groovy.properties配置发生异常", e);
            compilerConfiguration = config;
            return;
        }
        ImportCustomizer customizer = new ImportCustomizer();
        properties.forEach((key, value) -> {
            String strVal = String.valueOf(value);
            String strKey = String.valueOf(key);
            if ("IMPORT_PACKAGE".equals(key)) {
                String[] importPackages = strVal.split(";");
                customizer.addStarImports(importPackages);
            } else if (strKey.startsWith("FNP.")) {
                String alias = strKey.substring(4);
                customizer.addImport(alias, strVal);
            }
        });
        config.addCompilationCustomizers(customizer);
        compilerConfiguration = config;
    }
    
    /**
     * 构建参数
     *
     * @param context 上下文
     * @return
     */
    private Binding wrapperGlobalVariables(ExecuteContext<String> context) {
        // 定义脚本局部变量
        Map<String, Object> param = Maps.newHashMapWithExpectedSize(8);
        return new Binding(param);
    }
}
