package com.atguigu.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关连接注册中心实现负载均衡模式:Java硬编码设置路由规则
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RouterConnectLoadbalanceHardcodeGateWay9527 {
    public static void main(String[] args) {
        SpringApplication.run(RouterConnectLoadbalanceHardcodeGateWay9527.class, args);
    }
}
