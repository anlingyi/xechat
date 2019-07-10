package cn.xeblog.xechat.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 撤消消息
 *
 * @author yanpanyi
 * @date 2019/3/25
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RevokeMsgVo extends MessageVO {

    /**
     * 撤回的消息id
     */
    private String revokeMessageId;
}
