package org.openatom.springcloud.services;

import org.openatom.springcloud.entities.CommonResult;
import org.openatom.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用于测试8003这个节点中的服务熔断、服务降级功能
 */
@Component
@FeignClient(name="SPRINGCLOUD-PROVIDER-SENTINEL-PAYMENT-SERVICE-CLUSTER")
public interface PaymentServiceSentinelOpenFeign {
    @PostMapping(value = "/provider/payment/create")
    CommonResult create(@RequestBody Payment payment);

    @GetMapping(value = "/provider/payment/ok/get/{id}")
    CommonResult<Payment> getPaymentByIdOk(@PathVariable("id") Long id);

    @GetMapping(value = "/provider/payment/timeout/get/{id}")
    CommonResult<Payment> getPaymentByIdTimeout(@PathVariable("id") Long id);

    /**
     * 测试服务降级
     *  @FeignClient中:fallback = PaymentServiceSentinelOpenFeign.class
     * @param id
     * @return
     */
    @GetMapping(value = "/provider/payment/degradation_in_provider/get/{id}")
    CommonResult<Payment> getPaymentByIdUseSentinelDegradation(@PathVariable("id") Long id);

    /**
     * 测试服务熔断
     * @param id
     * @return
     */
    @GetMapping(value = "/provider/payment/circuitbreaker/get/{id}")
    CommonResult<Payment> getPaymentByIdOkSentinelCircuitBreaker(@PathVariable("id") Long id);
}
