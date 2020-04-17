package com.ruoyi;

import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })

public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        ConfigurableApplicationContext context = SpringApplication.run(RuoYiApplication.class, args);
        ConfigurableEnvironment env = context.getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String contextPath = "";
        if (env.getProperty("server.servlet.contextPath") != null) {
            contextPath = env.getProperty("server.servlet.contextPath");
        }
        String profiles = StringUtils.arrayToCommaDelimitedString(context.getEnvironment().getActiveProfiles());
        System.out.println("\n----------------------------------------------------------" +
                "\n\tApplication "+env.getProperty("ruoyi.name")+" is running! Access URLs:" +
                "\n\tLocal: \t\t"+protocol+"://localhost:"+env.getProperty("server.port")+"" +
                "\n\tExternal: \t"+protocol+"://"+InetAddress.getLoopbackAddress().getHostAddress()+":"+env.getProperty("server.port")+"" +
                "\n\tProfile(s): \t"+profiles+"\n----------------------------------------------------------");


    }
}