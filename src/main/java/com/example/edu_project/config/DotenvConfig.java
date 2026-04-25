package com.example.edu_project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigTreeEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * .env 文件加载器
 * 在 Spring Boot 启动前加载 .env 文件中的环境变量
 *
 * 使用方式：将 .env 文件放在项目根目录或运行目录
 */
public class DotenvConfig implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DotenvConfig.class);

    private static final String ENV_FILE = ".env";

    @Override
    public void postProcessEnvironment(org.springframework.core.env.ConfigurableEnvironment environment, SpringApplication application) {
        Path envPath = findEnvFile();
        if (envPath == null) {
            log.debug("未找到 .env 文件，将使用默认配置或系统环境变量");
            return;
        }

        log.info("加载 .env 配置文件: {}", envPath.toAbsolutePath());

        try {
            Map<String, Object> properties = loadEnvFile(envPath);
            PropertySource<?> propertySource = new MapPropertySource("dotenv", properties);
            environment.getPropertySources().addFirst(propertySource);
        } catch (IOException e) {
            log.warn("加载 .env 文件失败: {}", e.getMessage());
        }
    }

    /**
     * 查找 .env 文件
     * 搜索顺序：项目根目录 -> 当前工作目录 -> 用户主目录
     */
    private Path findEnvFile() {
        // 1. 当前工作目录
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path envFile = currentDir.resolve(ENV_FILE);
        if (Files.exists(envFile)) {
            return envFile;
        }

        // 2. 用户主目录
        Path userHome = Paths.get(System.getProperty("user.home"));
        envFile = userHome.resolve(ENV_FILE);
        if (Files.exists(envFile)) {
            return envFile;
        }

        // 3. 项目根目录 (D:\MyCode\edu_project)
        Path projectRoot = Paths.get("D:/MyCode/edu_project");
        envFile = projectRoot.resolve(ENV_FILE);
        if (Files.exists(envFile)) {
            return envFile;
        }

        return null;
    }

    /**
     * 加载 .env 文件内容
     */
    private Map<String, Object> loadEnvFile(Path envPath) throws IOException {
        Map<String, Object> properties = new HashMap<>();
        Resource resource = new FileSystemResource(envPath);

        Files.lines(envPath)
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    int equalsIndex = line.indexOf('=');
                    if (equalsIndex > 0) {
                        String key = line.substring(0, equalsIndex).trim();
                        String value = line.substring(equalsIndex + 1).trim();
                        // 移除引号
                        value = stripQuotes(value);
                        properties.put(key, value);
                        log.debug("  {} = ****", key);
                    }
                });

        return properties;
    }

    /**
     * 移除字符串首尾的引号
     */
    private String stripQuotes(String value) {
        if ((value.startsWith("\"") && value.endsWith("\"")) ||
            (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
