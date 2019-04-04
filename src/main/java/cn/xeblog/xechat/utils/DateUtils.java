package cn.xeblog.xechat.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 日期处理
 * @author yanpanyi
 * @date 2019/3/25
 */
public class DateUtils {

    public static String getDate(String format) {
        return DateFormatUtils.format(new Date(), format);
    }
}
