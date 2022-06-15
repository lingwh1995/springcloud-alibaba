package org.openatom.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 不使用Ribbon实现负载均衡:使用SpringCloud自带的负载均衡组件
 *      特点：每个节点处理请求的权重相同,SpringCloud自带的负载均衡组件只有轮询这一种负载均衡算法
 */
@EnableDiscoveryClient
@SpringBootApplication
public class OrderServiceConsumerLoadBalanceDefault80 {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceConsumerLoadBalanceDefault80.class, args);
    }
}
