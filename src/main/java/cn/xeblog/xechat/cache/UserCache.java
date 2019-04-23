package cn.xeblog.xechat.cache;

import cn.xeblog.xechat.constant.RobotConstant;
import cn.xeblog.xechat.constant.UserStatusConstant;
import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.utils.SensitiveWordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存用户信息
 *
 * @author yanpanyi
 * @date 2019/3/24
 */
public class UserCache {

    /**
     * 在线用户列表
     */
    private static ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>(32);

    static {
        // 初始化机器人信息
        String uid = RobotConstant.key;
        User user = new User();
        user.setUserId(uid);
        user.setUsername(RobotConstant.name);
        user.setAvatar(RobotConstant.avatar);
        user.setAddress(RobotConstant.address);
        user.setStatus(UserStatusConstant.ONLINE);

        // 将机器人加入到用户列表
        userMap.put(uid, user);
    }

    /**
     * 添加用户
     *
     * @param key
     * @param user
     */
    public static void addUser(String key, User user) {
        if (null != getUser(key)) {
            return;
        }

        userMap.put(key, user);
    }

    /**
     * 获取用户
     *
     * @param key
     * @return
     */
    public static User getUser(String key) {
        return userMap.get(key);
    }

    /**
     * 删除用户
     *
     * @param key
     */
    public static void removeUser(String key) {
        userMap.remove(key);
    }

    /**
     * 获取在线用户数
     *
     * @return
     */
    public static int getOnlineCount() {
        return userMap.size();
    }

    /**
     * 获取所有的在线用户
     *
     * @return
     */
    public static List<User> listUser() {
        return new ArrayList<>(userMap.values());
    }

}
