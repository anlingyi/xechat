package cn.xeblog.xechat.controller;

import cn.xeblog.xechat.constant.DateConstant;
import cn.xeblog.xechat.constant.StompConstant;
import cn.xeblog.xechat.domain.dto.ChatRecordDTO;
import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.domain.ro.MessageRO;
import cn.xeblog.xechat.domain.vo.MessageVO;
import cn.xeblog.xechat.domain.vo.ResponseVO;
import cn.xeblog.xechat.domain.vo.RevokeMsgVo;
import cn.xeblog.xechat.enums.CodeEnum;
import cn.xeblog.xechat.enums.MessageTypeEnum;
import cn.xeblog.xechat.enums.inter.Code;
import cn.xeblog.xechat.exception.ErrorCodeException;
import cn.xeblog.xechat.service.ChatRecordService;
import cn.xeblog.xechat.utils.CheckUtils;
import cn.xeblog.xechat.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    @Resource
    private ChatRecordService chatRecordService;

    /**
     * 聊天室发布订阅
     *
     * @param messageRO
     * @param user
     * @return
     */
    @MessageMapping(StompConstant.PUB_CHAT_ROOM)
    @SendTo(StompConstant.SUB_CHAT_ROOM)
    public ResponseVO chatRoom(MessageRO messageRO, User user) {
        log.debug("来自聊天室的消息：{}", messageRO);

        return buildResponseVo(messageRO, user, MessageTypeEnum.USER);
    }

    /**
     * 给单个用户发送消息
     *
     * @param messageRO
     * @param user
     * @return
     * @throws Exception
     */
    @MessageMapping(StompConstant.PUB_USER)
    public void sendToUser(MessageRO messageRO, User user) {
        log.debug("来自用户的消息：{}", messageRO);

        if (messageRO.getReceiver() == null) {
            return;
        }

        final ResponseVO responseVO = buildResponseVo(messageRO, user, MessageTypeEnum.USER);

        String[] receiver = messageRO.getReceiver();
        for (int i = 0, len = receiver.length; i < len; i++) {
            /*
            将消息发送到指定用户
            参数说明：1.消息接受者 2.消息订阅地址 3.消息内容
            */
            messagingTemplate.convertAndSendToUser(receiver[i], StompConstant.SUB_USER, responseVO);
        }
    }

    /**
     * 消息异常处理
     *
     * @param e
     * @param user
     */
    @MessageExceptionHandler(Exception.class)
    public void handleExceptions(Exception e, User user) {
        Code code = CodeEnum.INTERNAL_SERVER_ERROR;

        if (e instanceof ErrorCodeException) {
            code = ((ErrorCodeException) e).getCode();
        } else {
            log.error("error:", e);
        }

        messagingTemplate.convertAndSendToUser(user.getUserId(), StompConstant.SUB_ERROR, new ResponseVO(code));
    }

    /**
     * 撤回消息
     *
     * @param messageId
     * @param user
     */
    @MessageMapping(StompConstant.PUB_CHAT_ROOM_REVOKE)
    public void revokeMessage(String messageId, User user) throws ErrorCodeException {
        CheckUtils.checkMessageId(messageId, user.getUserId());

        RevokeMsgVo revokeMsgVo = new RevokeMsgVo();
        revokeMsgVo.setRevokeMessageId(messageId);
        revokeMsgVo.setUser(user);
        revokeMsgVo.setType(MessageTypeEnum.REVOKE);
        revokeMsgVo.setSendTime(DateUtils.getDate(DateConstant.SEND_TIME_FORMAT));

        chatRecordService.addRecord(ChatRecordDTO.toChatRecordDTO(revokeMsgVo));

        messagingTemplate.convertAndSend(StompConstant.SUB_CHAT_ROOM, new ResponseVO(revokeMsgVo));
    }

    private ResponseVO buildResponseVo(MessageRO messageRO, User user, MessageTypeEnum messageTypeEnum) {
        MessageVO messageVO = new MessageVO();
        messageVO.setSendTime(DateUtils.getDate(DateConstant.SEND_TIME_FORMAT));
        messageVO.setUser(user);
        messageVO.setMessage(messageRO.getMessage());
        messageVO.setType(messageTypeEnum);
        messageVO.setImage(messageRO.getImage());

        chatRecordService.addRecord(ChatRecordDTO.toChatRecordDTO(messageVO));

        return new ResponseVO(messageVO);
    }

}
