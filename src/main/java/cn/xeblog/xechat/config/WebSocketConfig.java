package cn.xeblog.xechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * websocket配置
 *
 * @author yanpanyi
 * @EnableWebSocketMessageBroker 注解开启使用STOMP协议来传输基于MessageBroker代理的消息
 * @date 2019/3/20
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置消息代理
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 广播式使用/topic，点对点式使用/user
        registry.enableSimpleBroker("/topic", "/user");
    }

    /**
     * 注册STOMP的节点，并映射指定的url
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP的endpoint，并指定使用SockJS协议
        registry.addEndpoint("/xechat").withSockJS();
    }
}
