package org.openatom.springcloud.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.openatom.springcloud.dao.PaymentSentinelDao;
import org.openatom.springcloud.entities.CommonResult;
import org.openatom.springcloud.service.PaymentSentinelService;
import org.springframework.stereotype.Service;
import org.openatom.springcloud.entities.Payment;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 用于测试Sentinel
 */
@Service
public class PaymentSentinelServiceImpl implements PaymentSentinelService {

    @Resource
    private PaymentSentinelDao paymentSentinelDao;

    @Override
    public int create(Payment payment) {
        return paymentSentinelDao.create(payment);
    }

    @Override
    public Payment getPaymentByIdOk(Long id) {
        return paymentSentinelDao.getPaymentById(id);
    }

    @Override
    public Payment getPaymentByIdTimeout(Long id) {
        //睡眠3秒
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return paymentSentinelDao.getPaymentById(id);
    }

    /**
     * 服务降级
     * @param id
     * @return
     */
    @Override
    public Payment getPaymentByIdUseSentinelDegradation(Long id) {
        return paymentSentinelDao.getPaymentById(id);
    }

    /**
     * 当下游服务(服务提供方)发生故障时对服务下游服务(服务提供方)进行降级
     *  10内请求失败,失败率为60%时熔断服务
     * @param id
     * @return
     */
    @Override
    public Payment getPaymentByIdUseSentinelCircuitBreaker(Long id) {
        //当ID小于0时,消费端使用不合理的参数多次调用此服务,则服务熔断
        if(id<0){
            throw new RuntimeException("id不能小于0");
        }
        return paymentSentinelDao.getPaymentById(id);
    }

    /**
     * 当方法getPaymentByIdUseSentinelCircuitBreaker()执行失败时,执行下面的方法
     * @param id
     * @return
     */
    public Payment getPaymentByIdUseSentinelCircuitBreakerFallback(Long id) {
        return new Payment(id,"服务提供方:测试服务熔断成功");
    }
}
