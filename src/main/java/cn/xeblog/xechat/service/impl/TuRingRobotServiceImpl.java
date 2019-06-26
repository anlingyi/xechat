package cn.xeblog.xechat.service.impl;

import cn.xeblog.xechat.service.RobotService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 使用图灵机器人api的实现
 *
 * @author yanpanyi
 * @date 2019/4/9
 */
@Service
@Slf4j
public class TuRingRobotServiceImpl implements RobotService {

    @Resource
    private RestTemplate restTemplate;

    /**
     * api地址
     */
    @Value("${turing.apiUrl}")
    private String apiUrl;

    /**
     * apikey
     */
    @Value("${turing.apiKey}")
    private String apiKey;

    @Override
    public String sendMessage(String userId, String text) {
        ResponseEntity<JSONObject> resp = restTemplate.exchange(apiUrl, HttpMethod.POST,
                buildHttpEntity(userId, text), JSONObject.class);

        return parseData(resp);
    }

    /**
     * 构建请求实体
     *
     * @param userId 用户id
     * @param text 消息内容
     * @return HttpEntity
     */
    private HttpEntity buildHttpEntity(String userId, String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject inputText = new JSONObject();
        inputText.put("text", text);

        JSONObject input = new JSONObject();
        input.put("inputText", inputText);

        JSONObject userInfo = new JSONObject();
        userInfo.put("apiKey", apiKey);
        userInfo.put("userId", userId);

        JSONObject body = new JSONObject();
        body.put("reqType", 0);
        body.put("perception", input);
        body.put("userInfo", userInfo);

        return new HttpEntity(body, headers);
    }

    /**
     * 解析响应数据
     *
     * @param resp 响应数据
     * @return 解析后的字符串
     */
    private String parseData(ResponseEntity<JSONObject> resp) {
        if (resp.getStatusCodeValue() != HttpStatus.SC_OK) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        JSONObject data = resp.getBody();
        log.debug("data -> {}", data);

        JSONArray results = data.getJSONArray("results");

        JSONObject obj;
        JSONObject values;
        JSONArray news;
        JSONObject newsInfo;
        for (int i = 0; i < results.size(); i++) {
            obj = results.getJSONObject(i);
            String type = obj.getString("resultType");
            values = obj.getJSONObject("values");

            switch (type) {
                case "text":
                    sb.append(StringEscapeUtils.unescapeHtml4(values.getString("text")));
                    break;
                case "url":
                    String url = values.getString("url");
                    sb.append("<a href='");
                    sb.append(url);
                    sb.append("' target='_blank'>");
                    sb.append(url);
                    sb.append("</a><br/>");
                    break;
                case "news":
                    news = values.getJSONArray("news");
                    for (int j = 0; j < news.size(); j++) {
                        newsInfo = news.getJSONObject(j);
                        sb.append("<br/><a href='");
                        sb.append(newsInfo.getString("detailurl"));
                        sb.append("' target='_blank'>");
                        sb.append(j + 1);
                        sb.append(". ");
                        sb.append(newsInfo.getString("name"));
                        sb.append("</a>");
                        sb.append("<br/><img src='");
                        sb.append(newsInfo.getString("icon"));
                        sb.append("' >");
                    }
                    break;
                default:
                    break;
            }
        }

        return sb.toString();
    }
}
