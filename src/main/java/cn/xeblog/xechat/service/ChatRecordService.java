package cn.xeblog.xechat.service;

import cn.xeblog.xechat.domain.dto.ChatRecordDTO;

import java.util.HashMap;
import java.util.List;

/**
 * 聊天记录
 *
 * @author yanpanyi
 * @date 2019/4/4
 */
public interface ChatRecordService {

    /**
     * 添加聊天记录
     *
     * @param chatRecordDTO
     */
    void addRecord(ChatRecordDTO chatRecordDTO);

    /**
     * 聊天记录列表
     *
     * @return
     * @throws Exception
     */
    List<HashMap<String, String>> listRecord();
}
