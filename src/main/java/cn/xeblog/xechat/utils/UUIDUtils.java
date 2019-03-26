package cn.xeblog.xechat.utils;

import java.util.UUID;

/**
 * uuid工具类
 *
 * @author yanpanyi
 * @date 2019/03/27
 */
public class UUIDUtils {

    /**
     * 生成uuid
     *
     * @return
     */
    public static String create() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
