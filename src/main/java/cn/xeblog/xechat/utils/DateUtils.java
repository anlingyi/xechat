package cn.xeblog.xechat.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * 日期处理
 * @author yanpanyi
 * @date 2019/3/25
 */
public class DateUtils {

    /**
     * 格式化当前日期
     *
     * @param format 日期格式
     * @return 返回格式化后的日期字符串
     */
    public static String getDate(String format) {
        Assert.notNull(format, "日期格式不能为空");
        return DateFormatUtils.format(new Date(), format);
    }

    /**
     * 格式化指定时间戳
     *
     * @param timestamp 需要格式化的时间戳
     * @param format    日期格式
     * @return 返回格式化后的日期字符串
     */
    public static String getDate(long timestamp, String format) {
        Assert.state(timestamp >= 0, "时间戳不能为负数");
        Assert.notNull(format, "日期格式不能为空");
        return DateFormatUtils.format(timestamp, format);
    }

    /**
     * 格式化指定日期对象
     *
     * @param date   需要格式化的日期对象
     * @param format 日期格式
     * @return 返回格式化后的日期字符串
     */
    public static String getDate(Date date, String format) {
        Assert.notNull(date, "日期对象不能为空");
        Assert.notNull(format, "日期格式不能为空");
        return DateFormatUtils.format(date, format);
    }
}
