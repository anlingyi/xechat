package cn.xeblog.xechat.utils;

import cn.xeblog.xechat.enums.CodeEnum;
import cn.xeblog.xechat.exception.ErrorCodeException;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验相关
 *
 * @author yanpanyi
 * @date 2019/3/25
 */
public class CheckUtils {

    /**
     * 撤消消息过期时间 3分钟
     */
    private static final long MESSAGE_EXPIRE_DATE = 180000;

    /**
     * 校验撤消的消息id
     *
     * @param messageId
     * @return
     */
    public static void checkMessageId(String messageId, String userId) throws ErrorCodeException {
        if (StringUtils.isEmpty(messageId)) {
            throw new ErrorCodeException(CodeEnum.INVALID_PARAMETERS);
        }

        String[] str = StringUtils.split(messageId, ':');

        if (!userId.equals(str[0])) {
            throw new ErrorCodeException(CodeEnum.INVALID_TOKEN);
        }

        // 判断消息是否过期
        if (System.currentTimeMillis() > Long.parseLong(str[1]) + MESSAGE_EXPIRE_DATE) {
            throw new ErrorCodeException(CodeEnum.MESSAGE_HAS_EXPIRED);
        }
    }
}
