package org.openatom.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 支付接口提供者
 *  使用Eureka作为注册中心
 */
@EnableDiscoveryClient
@SpringBootApplication
public class PaymentServiceProviderSleuthAndZipkinClusterNode8005 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceProviderSleuthAndZipkinClusterNode8005.class, args);
    }

}
