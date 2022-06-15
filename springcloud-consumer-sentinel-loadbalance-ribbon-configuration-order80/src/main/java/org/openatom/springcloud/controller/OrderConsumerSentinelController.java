package org.openatom.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.openatom.springcloud.entities.CommonResult;
import org.openatom.springcloud.entities.Payment;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 用于测试8003这个节点中的服务熔断、服务降级功能
 */
@RestController
@Slf4j
public class OrderConsumerSentinelController {

    //单机版
//    public static final String PAYMENT_URL = "http://localhost:8001";
//    public static final String PAYMENT_URL = "http://localhost:8002";

    //集群版
    //从配置文件中动态获取远程调用地址
    @Value("${service.provider.url}")
    private String SERVICE_PROVIDER_URL;

    //从配置文件中动态获服务提供方名称
    @Value("${service.provider.name}")
    private String SERVICE_PROVIDER_NAME;

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment) {
        return restTemplate.postForObject(SERVICE_PROVIDER_URL +"/provider/payment/create",payment, CommonResult.class);
    }

    /**
     * 正常获取Payment
     * 访问地址:
     *      localhost:/consumer/payment/ok/get/1
     * @param id
     * @return
     */
    @GetMapping("/consumer/payment/ok/get/{id}")
    public CommonResult<Payment> getPaymentByIdOk(@PathVariable("id") Long id) {
        return restTemplate.getForObject(SERVICE_PROVIDER_URL+"/provider/payment/ok/get/"+id,CommonResult.class);
    }

    /**
     * 延时获取Payment
     * 访问地址:
     * 访问地址:http://localhost/consumer/payment/timeout/get/1
     * 当大量线程访问这个接口的时候,服务调用者访问上面的接口getPaymentById()也会受到影响,因为Tomcat的线程池中的处理
     * 线程都被访问当前接口的多个请求占据了
     * @param id
     * @return
     */
    @GetMapping("/consumer/payment/timeout/get/{id}")
    public CommonResult<Payment> getPaymentByIdTimeout(@PathVariable("id") Long id) {
        return restTemplate.getForObject(SERVICE_PROVIDER_URL+"/provider/payment/timeout/get/"+id,CommonResult.class);
    }

    /**
     * 测试提供端服务降级
     * 访问地址:
     *      正常访问:
     *          http://localhost/consumer/payment/degradation_in_provider/get/1
     *      报异常访问:
     *          http://localhost/consumer/payment/degradation_in_provider/get/-1
     * 当调用发生异常时使用Sentinel在提供端对服务进行降级
     * @param id
     * @return
     */
    @GetMapping("/consumer/payment/degradation_in_provider/get/{id}")
    public CommonResult<Payment> getPaymentByIdUseSentinelDegradationInProvider(@PathVariable("id") Long id) {
        return restTemplate.getForObject(SERVICE_PROVIDER_URL+"/provider/payment/degradation_in_provider/get/"+id,CommonResult.class);
    }

    /**
     * 测试消费端服务降级
     * 访问地址:
     *      正常访问:
     *          http://localhost/consumer/payment/degradation_in_consumer/get/1
     *      报异常访问:
     *          http://localhost/consumer/payment/degradation_in_consumer/get/-1
     * 当调用发生异常时使用Sentinel在消费端对服务进行降级
     * @param id
     * @return
     */
    @GetMapping("/consumer/payment/degradation_in_consumer/get/{id}")
    //@SentinelResource(value = "fallback") //没有配置
    //@SentinelResource(value = "fallback",fallback = "fallbackHandler") //fallback只负责业务异常
    //@SentinelResource(value = "fallback",blockHandler = "blockHandler") //blockHandler只负责sentinel控制台配置违规
    @SentinelResource(value = "testFallbackAndBlockHandlerInConsumer",
            fallback = "fallbackHandler",
            blockHandler = "blockHandler")
//            exceptionsToIgnore = {IllegalArgumentException.class})
    public CommonResult<Payment> testFallbackAndBlockHandlerInConsumer(@PathVariable Long id) {
        if (id<0) {
            throw new IllegalArgumentException ("IllegalArgumentException,非法参数异常....");
        }
        return restTemplate.getForObject(SERVICE_PROVIDER_URL+"/provider/payment/degradation_in_provider/get/"+id,CommonResult.class);
    }

    /**
     * 当触发fallback后调用下面的方法
     *      Java程序内部出现故障时触发下面的方法
     * @param id
     * @param exception
     * @return
     */
    public CommonResult fallbackHandler(@PathVariable  Long id,Throwable exception) {
        Payment payment = new Payment(id,"null");
        return new CommonResult<>(444,"由于Java代码中发生了异常,所以触发了此代码" + exception.getMessage(),payment);
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
        return new CommonResult<>(445,"由于违反了Sentinel中配置的规则,所以触发了此代码" + blockException.getMessage(),payment);
    }

    /**
     * 测试默认的全局降级回调方法
     * 访问地址:
     *      http://localhost:/consumer/payment/degradation_in_consumer_default/get/1
     * @param id
     * @return
     */
//    @GetMapping("/consumer/payment/degradation_in_consumer_default/get/{id}")
//    public CommonResult<Payment> getPaymentByIdOkTestDefaultGlobalCallback(@PathVariable("id") Long id) {
//        //模拟发生了异常
//        int i = 10/0;
//        return paymentServiceSentinelOpenFeign.getPaymentByIdOk(id);
//    }

    /**
     * 访问地址:http://localhost:/consumer/payment/degradation_in_consumer_service/get/1
     * 测试在Service层实现服务降级,首先关闭8003服务,模拟8003服务宕机,访问下面的地址
     * @param id
     * @return
     */
//    @GetMapping("/consumer/payment/degradation_in_consumer_service/get/{id}")
//    public CommonResult<Payment> getPaymentByIdUseSentinelDegradationInConsumerService(@PathVariable("id") Long id) {
//        //测试在Service层进行服务降级处理
//        return paymentServiceSentinelOpenFeign.getPaymentByIdUseSentinelDegradation(id);
//    }

    /**
     * 测试服务熔断:
     *      1.模拟发生异常熔断服务:
     *          http://localhost/consumer/payment/circuitbreaker/get/-1
     *      2.模拟不发生异常让服务自动恢复:
     *          http://localhost/consumer/payment/circuitbreaker/get/1
     *  测试方式:先多次访问路径1，将服务熔断,再多次访问路径2,刚开始访问依然返回的是异常信息,多次访问后可以看到服务恢复正常
     * @param id
     * @return
     */
//    @GetMapping("/consumer/payment/circuitbreaker/get/{id}")
//    public CommonResult<Payment> getPaymentByIdOkSentinelCircuitBreaker(@PathVariable("id") Long id) {
//        return paymentServiceSentinelOpenFeign.getPaymentByIdOkSentinelCircuitBreaker(id);
//    }
}
