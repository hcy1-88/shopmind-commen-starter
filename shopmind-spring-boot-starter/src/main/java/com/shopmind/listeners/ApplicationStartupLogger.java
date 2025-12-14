// 路径：com.shopmind.starter.logging.ApplicationStartupLogger.java
package com.shopmind.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 通用应用启动成功日志监听器
 * 自动注册，所有引入 shopmind-starter 的服务都会生效
 */
@Slf4j
public class ApplicationStartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 避免子上下文重复触发（如测试上下文）
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        Environment env = event.getApplicationContext().getEnvironment();
        String appName = env.getProperty("spring.application.name", "application");
        String[] activeProfiles = env.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";
        String port = env.getProperty("server.port", "8080");

        // 启动耗时
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        double seconds = (System.currentTimeMillis() - startTime) / 1000.0;

        // 主机地址
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            host = "localhost";
        }
        String baseUrl = "http://" + host + ":" + port;

        // 构建美观 banner
        printBanner(appName, profile, port, baseUrl, seconds);
    }

    private void printBanner(String appName, String profile, String port, String baseUrl, double seconds) {
        String title = "🚀 " + capitalize(appName) + " 启动成功！";
        StringBuilder banner = new StringBuilder();
        banner.append("\n")
              .append("╔══════════════════════════════════════════════════════════════════════╗\n")
              .append("║                                                                      ║\n")
              .append(String.format("║   %-66s║\n", title))
              .append("║                                                                      ║\n")
              .append(String.format("║   • 应用名称：%-56s║\n", appName))
              .append(String.format("║   • 激活环境：%-56s║\n", profile))
              .append(String.format("║   • 服务端口：%-56s║\n", port))
              .append(String.format("║   • 主机地址：%-56s║\n", baseUrl))
              .append(String.format("║   • 启动耗时：%-56s║\n", String.format("%.2f 秒", seconds)))
              .append("║                                                                      ║\n")
              .append("╚══════════════════════════════════════════════════════════════════════╝");

        log.info(banner.toString());
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).replace('-', ' ').replace('_', ' ');
    }
}