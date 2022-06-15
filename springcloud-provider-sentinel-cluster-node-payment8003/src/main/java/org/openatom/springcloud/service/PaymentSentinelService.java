package org.openatom.springcloud.service;

import org.apache.ibatis.annotations.Param;
import org.openatom.springcloud.entities.Payment;

/**
 * 用于测试Sentinel
 */
public interface PaymentSentinelService {

    /**
     * 未设置降级和熔断的方法
     * @param payment
     * @return
     */
    int create(Payment payment);
    /**
     * 未设置降级和熔断的方法
     * @param id
     * @return
     */
    Payment getPaymentByIdOk(@Param("id") Long id);
    /**
     * 未设置降级和熔断的方法
     * @param id
     * @return
     */
    Payment getPaymentByIdTimeout(@Param("id") Long id);

    /**
     * 测试服务降级的方法
     * @param id
     * @return
     */
    Payment getPaymentByIdUseSentinelDegradation(@Param("id") Long id);

    /**
     * 测试服务熔断的方法
     * @param id
     * @return
     */
    Payment getPaymentByIdUseSentinelCircuitBreaker(@Param("id") Long id);
}
