package cn.xeblog.xechat.service;

import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.domain.vo.MessageVO;
import cn.xeblog.xechat.enums.inter.Code;

/**
 * 消息处理
 *
 * @author yanpanyi
 * @date 2019/4/15
 */
public interface MessageService {

    /**
     * 发送消息
     *
     * @param subAddress 消息订阅地址
     * @param messageVO  消息视图
     * @throws Exception
     */
    void sendMessage(String subAddress, MessageVO messageVO) throws Exception;

    /**
     * 发送消息到指定用户
     *
     * @param receiver  消息接收者，是一个存入用户id的string数组
     * @param messageVO 消息视图
     * @throws Exception
     */
    void sendMessageToUser(String[] receiver, MessageVO messageVO) throws Exception;

    /**
     * 发送错误消息
     *
     * @param code 错误码
     * @param user 发送消息的用户信息，将发送错误消息到该用户
     */
    void sendErrorMessage(Code code, User user);

    /**
     * 发送消息到机器人
     *
     * @param subAddress 消息订阅地址
     * @param message    消息文本
     * @param user       发送消息的用户信息
     * @throws Exception
     */
    void sendMessageToRobot(String subAddress, String message, User user) throws Exception;

    /**
     * 发送机器人消息
     *
     * @param subAddress 消息订阅地址
     * @param message    消息文本
     * @throws Exception
     */
    void sendRobotMessage(String subAddress, String message) throws Exception;

}
