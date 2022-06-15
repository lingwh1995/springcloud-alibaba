package org.openatom.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.openatom.springcloud.service.PaymentSentinelService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.openatom.springcloud.entities.CommonResult;
import org.openatom.springcloud.entities.Payment;

import javax.annotation.Resource;

/**
 * 用于测试Sentinel
 */
@RestController
@Slf4j
public class PaymentSentinelController {

    @Resource
    private PaymentSentinelService paymentSentinelService;

    @Value("${server.port}")
    private String serverPort;

    //从配置文件中动态获取服务名称
    @Value("${spring.application.name}")
    private String APPLICATION_NAME;

    @PostMapping(value = "/provider/payment/create")
    public CommonResult create(@RequestBody Payment payment) {
        int result = paymentSentinelService.create(payment);
        log.info("*****插入结果："+result);
        if(result > 0) {
            return new CommonResult(200,"插入数据库成功,serverPort: "+serverPort,result);
        }else{
            return new CommonResult(444,"插入数据库失败",null);
        }
    }

    /**
     * 正常获取Payment
     * @param id
     * @return
     */
    @GetMapping(value = "/provider/payment/ok/get/{id}")
    public CommonResult<Payment> getPaymentByIdOk(@PathVariable("id") Long id) {
        log.info(APPLICATION_NAME + serverPort);
        Payment payment = paymentSentinelService.getPaymentByIdOk(id);
        if(payment != null){
            return new CommonResult(200,"查询成功,serverPort:  "+serverPort,payment);
        }else{
            return new CommonResult(444,"没有对应记录,查询ID: "+id,null);
        }
    }

    /**
     * 延时获取Payment
     * @param id
     * @return
     */
    @GetMapping(value = "/provider/payment/timeout/get/{id}")
    public CommonResult<Payment> getPaymentByIdTimeout(@PathVariable("id") Long id) {
        log.info(APPLICATION_NAME + serverPort);
        Payment payment = paymentSentinelService.getPaymentByIdTimeout(id);
        if(payment != null){
            return new CommonResult(200,"查询成功,serverPort:  "+serverPort,payment);
        }else{
            return new CommonResult(444,"没有对应记录,查询ID: "+id,null);
        }
    }

    /**
     * 测试服务提供方服务降级
     * 访问地址:
     *      正常访问:
     *          http://localhost:8004/provider/payment/degradation_in_provider/get/1
     *      报异常访问:
     *          http://localhost:8004/provider/payment/degradation_in_provider/get/-1
     * 当调用发生异常时使用Sentinel对服务进行降级
     * @param id
     * @return
     */
    //@SentinelResource(value = "fallback") //没有配置
    //@SentinelResource(value = "fallback",fallback = "fallbackHandler") //fallback只负责业务异常
    //@SentinelResource(value = "fallback",blockHandler = "blockHandler") //blockHandler只负责sentinel控制台配置违规
    @SentinelResource(value = "testFallbackAndBlockHandlerInProvider",
            fallback = "fallbackHandler",
            blockHandler = "blockHandler")
    //exceptionsToIgnore = {IllegalArgumentException.class})
    @GetMapping(value = "/provider/payment/degradation_in_provider/get/{id}")
    public CommonResult<Payment> getPaymentByIdUseSentinelDegradation(@PathVariable("id") Long id) {
        if(id < 0){
            throw new IllegalArgumentException ("IllegalArgumentException,非法参数异常....");
        }
        log.info(APPLICATION_NAME + serverPort);
        Payment payment = paymentSentinelService.getPaymentByIdUseSentinelDegradation(id);
        if(payment != null){
            return new CommonResult(200,"查询成功,serverPort:  "+serverPort,payment);
        }else{
            return new CommonResult(444,"没有对应记录,查询ID: "+id,null);
        }
    }

    /**
     * 当触发fallback后调用下面的方法
     *      Java程序内部出现故障时触发下面的方法
     * @param id
     * @param exception
     * @return
     */
    public CommonResult fallbackHandler(@PathVariable Long id, Throwable exception) {
        Payment payment = new Payment(id,"null");
        return new CommonResult<>(444,"服务提供者端:由于Java代码中发生了异常,所以触发了此代码" + exception.getMessage(),payment);
    }

    /**
     * 当触发blockHandler后调用下面的方法
     *      Sentinel 违反WEB界面配置规则时触发下面的方法
     * @param id
     * @param blockException
     * @return
     */
    public CommonResult blockHandler(@PathVariable  Long id, BlockException blockException) {
        Payment payment = new Payment(id,"null");
        return new CommonResult<>(445,"服务提供者端:由于违反了Sentinel中配置的规则,所以触发了此代码" + blockException.getMessage(),payment);
    }

    /**
     * 服务熔断测试方法
     * @param id
     * @return
     */
    @GetMapping(value = "/provider/payment/circuitbreaker/get/{id}")
    public CommonResult<Payment> getPaymentByIdUseSentinelCircuitBreaker(@PathVariable("id") Long id) {
        log.info(APPLICATION_NAME + serverPort);
        Payment payment = paymentSentinelService.getPaymentByIdUseSentinelCircuitBreaker(id);
        if(payment != null){
            return new CommonResult(200,"查询成功,serverPort:  "+serverPort,payment);
        }else{
            return new CommonResult(444,"没有对应记录,查询ID: "+id,null);
        }
    }
}
