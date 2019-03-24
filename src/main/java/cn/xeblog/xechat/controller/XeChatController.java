package cn.xeblog.xechat.controller;

import cn.xeblog.xechat.cache.UserCache;
import cn.xeblog.xechat.constant.StompConstant;
import cn.xeblog.xechat.domain.ro.MessageRO;
import cn.xeblog.xechat.domain.vo.MessageVO;
import cn.xeblog.xechat.domain.vo.ResponseVO;
import cn.xeblog.xechat.enums.CodeEnum;
import cn.xeblog.xechat.enums.MessageTypeEnum;
import cn.xeblog.xechat.enums.inter.Code;
import cn.xeblog.xechat.exception.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.Principal;

/**
 * 消息主控制器
 *
 * @author yanpanyi
 * @date 2019/3/20
 */
@RestController
@Slf4j
public class XeChatController {

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 聊天室发布订阅
     *
     * @param messageRO
     * @return
     */
    @MessageMapping(StompConstant.PUB_CHAT_ROOM)
    @SendTo(StompConstant.SUB_CHAT_ROOM)
    public ResponseVO chatRoom(MessageRO messageRO, Principal principal) {
        log.debug("来自聊天室的消息：{}", messageRO);

        MessageVO messageVO = new MessageVO();
        messageVO.setUser(UserCache.getUser(principal.getName()));
        messageVO.setMessage(messageRO.getMessage());
        messageVO.setType(MessageTypeEnum.USER);

        return new ResponseVO(messageVO);
    }

    /**
     * 给单个用户发送消息
     *
     * @param messageRO
     * @return
     * @throws Exception
     */
    @MessageMapping(StompConstant.PUB_USER)
    public void sendToUser(MessageRO messageRO, Principal principal) {
        log.debug("来自用户的消息：{}", messageRO);

        // 设置发送者
        messageRO.setSender(principal.getName());
        /*
            将消息发送到指定用户
            参数说明：1.消息接受者 2.消息订阅地址 3.消息内容
         */
        messagingTemplate.convertAndSendToUser(messageRO.getReceiver(), StompConstant.SUB_USER, messageRO);
    }

    /**
     * 消息异常处理
     *
     * @param e
     * @param messageRO
     */
    @MessageExceptionHandler(Exception.class)
    public void handleExceptions(Exception e, MessageRO messageRO) {
        Code code = CodeEnum.INTERNAL_SERVER_ERROR;

        if (e instanceof ErrorCodeException) {
            code = ((ErrorCodeException) e).getCode();
        } else {
            log.error("error:", e);
        }

        messagingTemplate.convertAndSendToUser(messageRO.getSender(), StompConstant.SUB_ERROR, new ResponseVO(code));
    }

}
