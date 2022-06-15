package org.openatom.springcloud.listener;


import com.purgeteam.dynamic.config.starter.event.ActionConfigEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class NacosConfigListener implements ApplicationListener<ActionConfigEvent> {
    @Override
    public void onApplicationEvent(ActionConfigEvent event) {
        log.info("接收事件");
        Map<String, HashMap> map = event.getPropertyMap();
        for (Map.Entry<String, HashMap> entry : map.entrySet()) {
            String key = entry.getKey();
            Map changeMap = entry.getValue();
            String before = String.valueOf(changeMap.get("before"));
            String after = String.valueOf(changeMap.get("after"));
            if(log.isInfoEnabled()){
                log.info("配置[key:{}]被改变，改变前before：{}，改变后after：{}",key,before,after);
            }
            // 落表记录
        }
    }
}
