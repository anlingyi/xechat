package cn.xeblog.xechat.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring工具类
 *
 * @author yanpanyi
 * @date 2019/4/25
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 通过类从spring上下文中获取bean
     *
     * @param cs
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> cs) {
        return applicationContext.getBean(cs);
    }
}
