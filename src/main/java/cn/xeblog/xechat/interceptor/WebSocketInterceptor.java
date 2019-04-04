package cn.xeblog.xechat.interceptor;

import cn.xeblog.xechat.constant.UserStatusConstant;
import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.utils.SensitiveWordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * webcocket拦截器
 *
 * @author yanpanyi
 * @date 2019/3/24
 */
@Component
@Slf4j
public class WebSocketInterceptor implements ChannelInterceptor {

    /**
     * 绑定用户信息
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.debug("进入拦截器 -> preSend");
        StompHeaderAccessor stompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(stompHeaderAccessor.getCommand())) {
            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setUsername(SensitiveWordUtils.loveChina(stompHeaderAccessor.getFirstNativeHeader("username")));
            user.setAvatar(stompHeaderAccessor.getFirstNativeHeader("avatar"));
            user.setAddress(stompHeaderAccessor.getFirstNativeHeader("address"));
            user.setStatus(UserStatusConstant.ONLINE);

            stompHeaderAccessor.setUser(user);
            log.debug("绑定用户信息 -> {}", user);
        }

        return message;
    }
}
