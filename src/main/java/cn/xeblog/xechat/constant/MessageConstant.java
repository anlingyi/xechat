package cn.xeblog.xechat.constant;

/**
 * 消息模板
 *
 * @author anlingyi
 * @date 2019/5/7
 */
public interface MessageConstant {
    /**
     * 进入聊天室广播消息
     */
    String ONLINE_MESSAGE = "%s进入了聊天室";
    /**
     * 离开聊天室广播消息
     */
    String OFFLINE_MESSAGE = "%s离开了聊天室";
    /**
     * 机器人欢迎消息
     */
    String ROBOT_WELCOME_MESSAGE = "@%s 欢迎来到聊天室！消息内容以'#'开头的我就能收到哦（PS：双击我的头像与我聊天），" +
            "随时来撩我呀！";
}
