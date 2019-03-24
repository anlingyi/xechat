package cn.xeblog.xechat.cache;

import cn.xeblog.xechat.domain.mo.User;

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

}
