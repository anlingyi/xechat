package cn.xeblog.xechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class XechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(XechatApplication.class, args);
    }

}
