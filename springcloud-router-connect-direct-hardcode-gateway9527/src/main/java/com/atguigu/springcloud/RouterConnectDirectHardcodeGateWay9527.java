package com.atguigu.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关直连服务模式:Java硬编码设置路由规则
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RouterConnectDirectHardcodeGateWay9527 {
    public static void main(String[] args) {
        SpringApplication.run(RouterConnectDirectHardcodeGateWay9527.class, args);
    }
}
