package cn.xeblog.xechat.service.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author yanpanyi
 * @date 2019/4/9
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class TuRingRobotServiceImplTest {

    @Resource
    private TuRingRobotServiceImpl tuRingRobotService;

    @Test
    public void sendMessage() {
//        System.out.println(tuRingRobotService.sendMessage("杭州天气"));
//        System.out.println(tuRingRobotService.sendMessage("杭州附近的酒店"));
//        System.out.println(tuRingRobotService.sendMessage("最新新闻"));
        System.out.println(tuRingRobotService.sendMessage("你长啥样"));
    }
}