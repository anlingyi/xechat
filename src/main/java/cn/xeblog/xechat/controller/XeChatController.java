package cn.xeblog.xechat.controller;

import cn.xeblog.xechat.domain.ro.MessageRO;
import cn.xeblog.xechat.domain.vo.MessageVO;
import cn.xeblog.xechat.domain.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 主控制器
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
     * 聊天室
     *
     * @param messageRO
     * @return
     */
    @MessageMapping("/chatRoom")
    @SendTo("/topic/chatRoom")
    public ResponseVO chatRoom(MessageRO messageRO) {
        log.debug("来自聊天室的消息：{}", messageRO);

        MessageVO messageVO = new MessageVO();
        messageVO.setAddress("浙江杭州");
        messageVO.setUsername(messageRO.getSender());
        messageVO.setMessage(messageRO.getMessage());

        return new ResponseVO(messageVO);
    }

    /**
     * 给单个用户发送消息
     *
     * @param messageRO
     * @return
     * @throws Exception
     */
    @MessageMapping("/chat")
    public void toUser(MessageRO messageRO) {
        log.debug("来自用户的消息：{}", messageRO);

        // 参数：1.消息接受者 2.消息订阅地址 3.消息内容
        messagingTemplate.convertAndSendToUser(messageRO.getReceiver(), "/chat", messageRO);
    }

}
