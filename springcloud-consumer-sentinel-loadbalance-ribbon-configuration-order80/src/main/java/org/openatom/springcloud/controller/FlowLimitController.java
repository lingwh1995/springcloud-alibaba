package org.openatom.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  使用Sentinel进行限流降级:WEB界面配置方式
 *  配置流控规则配置注意事项:
 *      1.访问url和@SentinelResource中value都可以配置,只要这个值是唯一的就行
 *      2.如果要使用自定义的降级方法来返回信息,则
 *          第一步要添加@SentinelResource,
 *          第二步要在@SentinelResource中添加blockHandler相关配置,这样才能使用自定义的降级方法
 */
@RestController
@Slf4j
public class FlowLimitController {

    /**
     *  测试使用Sentinel自带的的降级方式,让程序返回Sentinel自带的降级信息
     *      使用方式是在方法上不作任何处理,在配置界面中填写资源名称为 访问url的值/@SentinelResource中 value的值
     *  测试流控规则->QPS(每秒访问量)
     *      当每秒访问量超过单机阈值则显示Blocked by Sentinel (flow limiting)
     * @return
     */
    @GetMapping("/testA")
    @SentinelResource(value = "testA")
    public String testA() {
        return "------testA";
    }

    /**
     *  测试使用Sentinel自带的的降级方式,让程序返回Sentinel自带的降级信息
     *      使用方式是在方法上不作任何处理,在配置界面中填写资源名称为 访问url的值/@SentinelResource中 value的值
     *  测试流控规则->并发线程数
     *      当每秒访问量超过单机阈值则显示Blocked by Sentinel (flow limiting)
     * @return
     */
    @GetMapping("/testB")
    @SentinelResource(value = "testB")
    public String testB() {
        return "------testB";
    }

    /**
     *  测试流控规则->QPS(每秒访问量)/并发线程数->流控模式(关联)->关联资源:/testB
     *  如果是testA关联testB,则意味着当testB每秒访问量超过单机阈值时testA显示Blocked by Sentinel (flow limiting)
     * @return
     */

    /**
     *  测试使用自定义的降级方法,让程序返回自定义的降级信息
     *      注意:1.url中不含有参数
     *          2.使用方式是在方法上添加@SentinelResource,在配置界面中填写资源名称为 @SentinelResource中value的值
     * @return
     */
    @GetMapping("/testC")
    @SentinelResource(value = "testC",blockHandler = "testCBlockHandler")
    public String testC() {
        return "------testC";
    }

    /**
     * testC的降级方法:不带有参数
     * @param exception 异常信息
     * @return
     */
    public String testCBlockHandler (BlockException exception) {
        return "------testCBlockHandler被执行,说明testC被降级";
    }

    /**
     *  测试使用自定义的降级方法,让程序返回自定义的降级信息
     *      注意:1.url中含有参数
     *          2.使用方式是在方法上添加@SentinelResource,在配置界面中填写资源名称为 @SentinelResource中value的值
     * @return
     */
    @GetMapping("/testD/{id}")
    @SentinelResource(value = "testD",blockHandler = "testDBlockHandler")
    public String testD(@PathVariable("id") String id) {
        System.out.println(id);
        return "------testD";
    }

    /**
     * testD的降级方法:不带有参数
     * @param exception 异常信息
     * @return
     */
    public String testDBlockHandler (String id,BlockException exception) {
        System.out.println(id);
        return "------testDBlockHandler被执行,说明testD被降级";
    }

    @GetMapping("/testE")
    public String testE() {
        log.info("testE 测试异常数");
        int age = 10/0;
        return "------testE 测试异常数";
    }

    /**
     * 测试热点key
     *      注意:热点key的参数的数据类型就是方法参数中p1的数据类型
     * @param p1
     * @param p2
     * @return
     */
    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "testHotKeyBlockHandler")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2) {
//        int age = 10/0;
        return "------testHotKey";
    }

    public String testHotKeyBlockHandler (String p1, String p2, BlockException exception) {
        return "------testHotKeyBlockHandler被执行,说明testHotKey被降级";
    }

}
