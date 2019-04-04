package cn.xeblog.xechat.controller;

import cn.xeblog.xechat.domain.vo.ResponseVO;
import cn.xeblog.xechat.service.ChatRecordService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 聊天记录
 *
 * @author yanpanyi
 * @date 2019/4/4
 */
@RestController
@RequestMapping("/api/record")
public class ChatRecordController {

    @Resource
    private ChatRecordService chatRecordService;

    /**
     * 聊天记录列表
     *
     * @return
     */
    @GetMapping
    public ResponseVO listChatRecord() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", chatRecordService.listRecord());

        return new ResponseVO(jsonObject);
    }
}
