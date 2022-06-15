package org.openatom.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OrderServiceConsumerSentinelLoadBalanceRibbonConfiguration80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceConsumerSentinelLoadBalanceRibbonConfiguration80.class, args);
    }
}
