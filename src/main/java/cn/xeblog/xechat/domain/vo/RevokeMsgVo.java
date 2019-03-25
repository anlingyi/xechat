package cn.xeblog.xechat.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

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
public class RevokeMsgVo extends MessageVO implements Serializable {

    private static final long serialVersionUID = 6030369531530945838L;

    /**
     * 撤回的消息id
     */
    private String revokeMessageId;

}
