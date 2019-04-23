package cn.xeblog.xechat.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * restTemplate配置
 *
 * @author: yanpanyi
 * @date 2019/4/9
 **/
@Configuration
public class RestTemplateConfig {

    /**
     * 总连接数
     */
    @Value("${restTemplate.threadSize}")
    private int poolSize;

    /**
     * 连接不够时的等待时间，单位ms
     */
    @Value("${restTemplate.waitingTime}")
    private int waitingTime;

    /**
     * 连接超时时间，单位ms
     */
    @Value("${restTemplate.connectTimeOut}")
    private int connectTimeOut;

    /**
     * 读取超时时间，单位ms
     */
    @Value("${restTemplate.readTimeOut}")
    private int readTimeOut;

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        //总连接数
        connMgr.setMaxTotal(poolSize + 1);
        //同路由的并发数
        connMgr.setDefaultMaxPerRoute(poolSize);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(connectTimeOut);
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(readTimeOut);
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(waitingTime);
        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        clientHttpRequestFactory.setBufferRequestBody(false);

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.setRequestFactory(clientHttpRequestFactory);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

        return restTemplate;
    }
}
