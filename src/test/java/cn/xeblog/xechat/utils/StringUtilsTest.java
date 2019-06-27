package cn.xeblog.xechat.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

/**
 * @author anlingyi
 * @date 2019/6/27
 */
public class StringUtilsTest {

    @Test
    public void stringEscapeUtils() {
        System.out.println(StringEscapeUtils.escapeHtml4("<h1>标题</h1>"));
        System.out.println(StringEscapeUtils.escapeHtml4("<script>alert(1);</script>"));
        System.out.println(StringEscapeUtils.escapeHtml4("#123 <<< >>> &nbsp;"));
    }
}
