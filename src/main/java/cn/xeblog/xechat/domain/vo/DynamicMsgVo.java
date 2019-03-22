package cn.xeblog.xechat.domain.vo;

import lombok.*;

import java.io.Serializable;

/**
 * 聊天室动态消息
 *
 * @author yanpanyi
 * @date 2019/3/22
 */
@Getter
@Setter
@ToString
public class DynamicMsgVo extends MessageVO implements Serializable {

    private static final long serialVersionUID = 6784139265883590057L;

    /**
     * 在线人数
     */
    private int onlineCount;
}
