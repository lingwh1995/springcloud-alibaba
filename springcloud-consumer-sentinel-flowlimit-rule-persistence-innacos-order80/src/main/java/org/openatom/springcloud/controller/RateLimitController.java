package org.openatom.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.openatom.springcloud.myhandler.CustomerBlockHandler;
import org.openatom.springcloud.entities.CommonResult;
import org.openatom.springcloud.entities.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 使用Sentinel进行限流降级:WEB界面配置方式
 * @SentinelResource注解使用
 */
@RestController
public class RateLimitController {

    /**
     * 使用@SentinelResource()中value作为限流配置项
     * @return
     */
    @GetMapping("/byResource")
    @SentinelResource(value = "byResource",blockHandler = "byResourceBlockHandler")
    public CommonResult byResource() {
        return new CommonResult(200,"按资源名称限流测试OK",new Payment(2020L,"serial001"));
    }
    public CommonResult byResourceBlockHandler(BlockException exception) {
        return new CommonResult(444,exception.getClass().getCanonicalName()+"服务不可用");
    }

    /**
     * 使用url作为限流配置项
     * @return
     */
    @GetMapping("/rateLimit/byUrl")
    @SentinelResource(value = "byUrl")//为了使得簇点链路中可以查询到这个资源,可以添加这行代码
    public CommonResult byUrl() {
        return new CommonResult(200,"按url限流测试OK",new Payment(2020L,"serial002"));
    }

    /**
     * 把所有的降级方法配置到一个单独的类中
     * @return
     */
    @GetMapping("/rateLimit/customerBlockHandler")
    @SentinelResource(value = "customerBlockHandler",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "handlerException2")
    public CommonResult customerBlockHandler() {
        return new CommonResult(200,"按客戶自定义",new Payment(2020L,"serial003"));
    }
}