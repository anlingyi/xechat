package cn.xeblog.xechat.domain.vo;

import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.enums.MessageTypeEnum;
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
@NoArgsConstructor
public class DynamicMsgVo extends MessageVO implements Serializable {

    private static final long serialVersionUID = 6784139265883590057L;

    /**
     * 在线人数
     */
    private int onlineCount;

    public DynamicMsgVo(User user, String message, MessageTypeEnum type, int onlineCount) {
        super(user, message, type);
        this.onlineCount = onlineCount;
    }
}
