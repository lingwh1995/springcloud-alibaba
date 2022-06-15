package org.openatom.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 支付接口提供者
 *  使用Nacos作为注册中心
 *  查看是否将服务注册进了Nacos:
 *      访问http://localhost:8848/nacos/在面板查看微服务
 */
//@EnableDiscoveryClient当注册中心不是Eureka时使用这个注解来暴露服务
@EnableDiscoveryClient
@SpringBootApplication
public class PaymentServiceProviderClusterNode8001 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceProviderClusterNode8001.class, args);
    }
}
