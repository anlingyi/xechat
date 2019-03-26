package cn.xeblog.xechat.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * 文件上传配置
 *
 * @author yanpanyi
 * @date 2019/03/27
 */
@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 文件上传大小
        factory.setMaxFileSize("30MB");
        // 总文件大小
        factory.setMaxRequestSize("30MB");

        return factory.createMultipartConfig();
    }

}
