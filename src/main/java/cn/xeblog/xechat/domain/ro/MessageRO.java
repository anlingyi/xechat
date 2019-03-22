package cn.xeblog.xechat.domain.ro;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 消息请求
 *
 * @author yanpanyi
 * @date 2019/3/20
 */
@Getter
@Setter
@ToString
public class MessageRO {

    /**
     * 接受者
     */
    private String receiver;
    /**
     * 发送者
     */
    private String sender;
    /**
     * 消息
     */
    private String message;

}
