package cn.xeblog.xechat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件相关
 *
 * @author yanpanyi
 * @date 2019/03/27
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {

    /**
     * 上传文件路径
     */
    private String uploadPath;
    /**
     * 静态资源访问路径
     */
    private String staticAccessPath;
    /**
     * 文件目录映射
     */
    private String directoryMapping;
    /**
     * 访问地址
     */
    private String accessAddress;

}
