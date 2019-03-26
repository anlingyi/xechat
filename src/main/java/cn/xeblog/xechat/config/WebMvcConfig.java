package cn.xeblog.xechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * mvc配置
 *
 * @author yanpanyi
 * @date 2019/03/27
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private FileConfig fileConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(fileConfig.getStaticAccessPath())
                .addResourceLocations("classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/",
                        fileConfig.getDirectoryMapping());
    }

}
