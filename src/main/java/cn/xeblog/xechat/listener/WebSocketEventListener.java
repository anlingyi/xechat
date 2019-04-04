package cn.xeblog.xechat.listener;

import cn.xeblog.xechat.cache.UserCache;
import cn.xeblog.xechat.constant.DateConstant;
import cn.xeblog.xechat.constant.StompConstant;
import cn.xeblog.xechat.constant.UserStatusConstant;
import cn.xeblog.xechat.domain.dto.ChatRecordDTO;
import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.domain.vo.DynamicMsgVo;
import cn.xeblog.xechat.domain.vo.ResponseVO;
import cn.xeblog.xechat.enums.MessageTypeEnum;
import cn.xeblog.xechat.service.ChatRecordService;
import cn.xeblog.xechat.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import javax.annotation.Resource;

/**
 * websocket事件监听
 *
 * @author yanpanyi
 * @date 2019/3/24
 */
@Slf4j
@Component
public class WebSocketEventListener {

    @Resource
    private SimpMessagingTemplate messagingTemplate;
    @Resource
    private ChatRecordService chatRecordService;

    private User user;

    /**
     * 建立连接监听
     *
     * @param sessionConnectedEvent
     */
    @EventListener
    public void handleConnectListener(SessionConnectedEvent sessionConnectedEvent) {
        log.debug("建立连接 -> {}", sessionConnectedEvent);

        user = (User) sessionConnectedEvent.getUser();
        UserCache.addUser(user.getUserId(), user);
    }

    /**
     * 断开连接监听
     *
     * @param sessionDisconnectEvent
     */
    @EventListener
    public void handleDisconnectListener(SessionDisconnectEvent sessionDisconnectEvent) {
        log.debug("断开连接 -> {}", sessionDisconnectEvent);

        String userId = sessionDisconnectEvent.getUser().getName();
        User user = UserCache.getUser(userId);
        if (null == user) {
            log.debug("用户不存在 uid ->", userId);
            return;
        }

        user.setStatus(UserStatusConstant.OFFLINE);
        UserCache.removeUser(userId);

        // 广播离线消息
        sendMessage(buildResponse(user));
        log.debug("广播离线消息 -> {}", user);
    }

    /**
     * 订阅监听
     *
     * @param sessionSubscribeEvent
     */
    @EventListener
    public void handleSubscribeListener(SessionSubscribeEvent sessionSubscribeEvent) {
        log.debug("新的订阅 -> {}", sessionSubscribeEvent);
        StompHeaderAccessor stompHeaderAccessor = MessageHeaderAccessor.getAccessor(sessionSubscribeEvent.getMessage(),
                StompHeaderAccessor.class);

        if (StompConstant.SUB_STATUS.equals(stompHeaderAccessor.getFirstNativeHeader(StompHeaderAccessor
                .STOMP_DESTINATION_HEADER))) {
            if (user != null) {
                try {
                    // 延迟100ms，防止客户端来不及接收上线消息
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log.error("中断异常，error -> {}", e);
                }

                // 广播上线消息
                sendMessage(buildResponse(user));
                log.debug("广播上线消息 -> {}", user);
            }

        }
    }

    /**
     * 构建响应数据
     *
     * @param user
     * @return
     */
    private ResponseVO buildResponse(User user) {
        DynamicMsgVo dynamicMsgVo = new DynamicMsgVo();
        dynamicMsgVo.setSendTime(DateUtils.getDate(DateConstant.SEND_TIME_FORMAT));
        dynamicMsgVo.setType(MessageTypeEnum.SYSTEM);
        dynamicMsgVo.setUser(user);
        dynamicMsgVo.setOnlineCount(UserCache.getOnlineCount());
        dynamicMsgVo.setOnlineUserList(UserCache.listUser());

        chatRecordService.addRecord(ChatRecordDTO.toChatRecordDTO(dynamicMsgVo));

        return new ResponseVO(dynamicMsgVo);
    }

    /**
     * 发送订阅消息，广播用户动态
     *
     * @param responseVO
     */
    private void sendMessage(ResponseVO responseVO) {
        messagingTemplate.convertAndSend(StompConstant.SUB_STATUS, responseVO);
    }

}