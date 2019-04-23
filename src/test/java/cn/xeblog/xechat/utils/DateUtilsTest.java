package cn.xeblog.xechat.utils;

import cn.xeblog.xechat.constant.DateConstant;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author yanpanyi
 * @date 2019/4/23
 */
@Ignore
public class DateUtilsTest {

    @Test
    public void getDate() {
        System.out.println(DateUtils.getDate(DateConstant.SEND_TIME_FORMAT));
        System.out.println(DateUtils.getDate(System.currentTimeMillis(), DateConstant.SEND_TIME_FORMAT));
    }

}