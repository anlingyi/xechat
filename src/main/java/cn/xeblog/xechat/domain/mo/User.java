package cn.xeblog.xechat.domain.mo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.security.Principal;

/**
 * 用户信息
 *
 * @author yanpanyi
 * @date 2019/3/22
 */
@Getter
@Setter
@ToString
public class User implements Principal, Serializable {

    private static final long serialVersionUID = 5114506546129512029L;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户昵称
     */
    private String username;
    /**
     * 地址
     */
    private String address;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 用户状态
     */
    private int status;

    @Override
    public String getName() {
        return userId;
    }
}
