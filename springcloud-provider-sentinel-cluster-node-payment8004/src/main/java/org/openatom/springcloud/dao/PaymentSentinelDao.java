package org.openatom.springcloud.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openatom.springcloud.entities.Payment;

/**
 * 用于测试Sentinel
 */
@Mapper
public interface PaymentSentinelDao {

    int create(Payment payment);

    Payment getPaymentById(@Param("id") Long id);
}
