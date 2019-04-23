package cn.xeblog.xechat.service.impl;

import cn.xeblog.xechat.cache.UserCache;
import cn.xeblog.xechat.constant.RobotConstant;
import cn.xeblog.xechat.constant.StompConstant;
import cn.xeblog.xechat.domain.dto.ChatRecordDTO;
import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.domain.vo.MessageVO;
import cn.xeblog.xechat.domain.vo.ResponseVO;
import cn.xeblog.xechat.enums.CodeEnum;
import cn.xeblog.xechat.enums.MessageTypeEnum;
import cn.xeblog.xechat.enums.inter.Code;
import cn.xeblog.xechat.exception.ErrorCodeException;
import cn.xeblog.xechat.service.ChatRecordService;
import cn.xeblog.xechat.service.MessageService;
import cn.xeblog.xechat.service.RobotService;
import cn.xeblog.xechat.utils.CheckUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yanpanyi
 * @date 2019/4/18
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private SimpMessagingTemplate messagingTemplate;
    @Resource
    private ChatRecordService chatRecordService;
    @Resource
    private RobotService robotService;

    @Override
    public void sendErrorMessage(Code code, User user) {
        log.info("发送错误信息 -> {} -> {}", code, user);
        messagingTemplate.convertAndSendToUser(user.getUserId(), StompConstant.SUB_ERROR, new ResponseVO(code));
    }

    @Override
    public void sendMessage(String subAddress, MessageVO messageVO) throws Exception {
        if (!CheckUtils.checkSubAddress(subAddress)) {
            throw new ErrorCodeException(CodeEnum.INVALID_PARAMETERS);
        }

        // 添加聊天记录
        chatRecordService.addRecord(ChatRecordDTO.toChatRecordDTO(messageVO));

        messagingTemplate.convertAndSend(subAddress, buildResponseVo(messageVO));
    }

    @Override
    public void sendMessageToUser(String[] receiver, MessageVO messageVO) throws Exception {
        if (!CheckUtils.checkReceiver(receiver)) {
            throw new ErrorCodeException(CodeEnum.INVALID_PARAMETERS);
        }
        // 添加聊天记录
        chatRecordService.addRecord(ChatRecordDTO.toChatRecordDTO(messageVO));

        ResponseVO responseVO = buildResponseVo(messageVO);
        for (int i = 0, len = receiver.length; i < len; i++) {
            // 将消息发送到指定用户 参数说明：1.消息接收者 2.消息订阅地址 3.消息内容
            messagingTemplate.convertAndSendToUser(receiver[i], StompConstant.SUB_USER, responseVO);
        }
    }

    private ResponseVO buildResponseVo(MessageVO messageVO) throws ErrorCodeException {
        if (messageVO == null) {
            throw new ErrorCodeException(CodeEnum.INVALID_PARAMETERS);
        }

        return new ResponseVO(messageVO);
    }

    @Async
    @Override
    public void sendMessageToRobot(String subAddress, String message, User user) throws Exception {
        log.info("user: {} -> 发送消息到机器人 -> {}", user, message);
        String robotMessage = robotService.sendMessage(message.replaceFirst(RobotConstant.prefix, ""));
        log.info("机器人响应结果 -> {}", robotMessage);

        sendMessage(subAddress, new MessageVO(UserCache.getUser(RobotConstant.key), robotMessage, MessageTypeEnum.ROBOT));

    }
}
