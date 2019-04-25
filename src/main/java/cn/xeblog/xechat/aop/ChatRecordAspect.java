package cn.xeblog.xechat.aop;

import cn.xeblog.xechat.domain.dto.ChatRecordDTO;
import cn.xeblog.xechat.domain.vo.MessageVO;
import cn.xeblog.xechat.service.ChatRecordService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 聊天记录切面类
 *
 * @author yanpanyi
 * @date 2019/4/23
 */
@Aspect
@Component
@Slf4j
public class ChatRecordAspect {

    @Resource
    private ChatRecordService chatRecordService;

    @Pointcut("@annotation(cn.xeblog.xechat.annotation.ChatRecord)")
    public void chatRecordPointcut() {
    }

    @Before("chatRecordPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        log.debug("before -> {}", joinPoint);

        MessageVO messageVO = null;
        Object[] args = joinPoint.getArgs();
        for (Object obj : args) {
            if (obj instanceof MessageVO) {
                messageVO = (MessageVO) obj;
                break;
            }
        }

        Assert.notNull(messageVO, "方法必需以MessageVO类或该类的子类作为参数");
        log.debug("添加聊天记录 -> {}", messageVO);
        chatRecordService.addRecord(ChatRecordDTO.toChatRecordDTO(messageVO));
    }

}
