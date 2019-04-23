package cn.xeblog.xechat.service;

/**
 * 机器人
 *
 * @author yanpanyi
 * @date 2019/4/9
 */
public interface RobotService {

    /**
     * 发送消息到机器人
     *
     * @param text 发送的消息内容
     * @return 机器人的答复信息
     * @throws Exception
     */
    String sendMessage(String text);
}
