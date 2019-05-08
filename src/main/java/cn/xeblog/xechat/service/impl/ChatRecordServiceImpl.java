package cn.xeblog.xechat.service.impl;

import cn.xeblog.xechat.constant.DateConstant;
import cn.xeblog.xechat.domain.dto.ChatRecordDTO;
import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.enums.MessageTypeEnum;
import cn.xeblog.xechat.service.ChatRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author yanpanyi
 * @date 2019/4/4
 */
@Slf4j
@Service
public class ChatRecordServiceImpl implements ChatRecordService {

    @Value("${chatrecord.path}")
    private String path;
    @Value("${chatrecord.accessAddress}")
    private String accessAddress;

    @Async
    @Override
    public void addRecord(ChatRecordDTO chatRecordDTO) {
        File file = new File(path + createFileName());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),
                "UTF-8"))) {
            out.write(formatContent(chatRecordDTO));
        } catch (IOException e) {
            log.error("添加聊天记录异常，error ->", e);
        }
    }

    /**
     * 创建文件名
     *
     * @return
     */
    private String createFileName() {
        return DateFormatUtils.format(new Date(), DateConstant.CHAT_RECORD_FILE_NAME) + ".md";
    }

    /**
     * 格式化内容
     *
     * @param chatRecordDTO
     * @return
     */
    private String formatContent(ChatRecordDTO chatRecordDTO) {
        if (null == chatRecordDTO) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        User user = chatRecordDTO.getUser();
        switch (chatRecordDTO.getType()) {
            case ROBOT:
            case USER:
                formatUserMsg(sb, chatRecordDTO);
                break;
            case SYSTEM:
                formatSystemMsg(sb, chatRecordDTO);
                break;
            case REVOKE:
                chatRecordDTO.setMessage(user.getUsername() + "撤回了一条消息！");
                formatSystemMsg(sb, chatRecordDTO);
                break;
            default:
                break;
        }

        return sb.toString();
    }

    @Override
    public List<HashMap<String, String>> listRecord() {
        List<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> map;
        File file = new File(path);
        String[] tempList = file.list();

        for (String name : tempList) {
            map = new HashMap<>(2);
            map.put("name", name);
            map.put("url", accessAddress + name);

            list.add(map);
        }

        return list;
    }

    /**
     * 格式化系统类型的消息
     *
     * @param sb
     * @param chatRecordDTO
     */
    private void formatSystemMsg(StringBuffer sb, ChatRecordDTO chatRecordDTO) {
        sb.append("#### [" + chatRecordDTO.getSendTime() + "] 系统消息：\r\n");
        sb.append("> " + chatRecordDTO.getMessage() + "\r\n");
    }

    /**
     * 格式化用户类型的消息
     *
     * @param sb
     * @param chatRecordDTO
     */
    private void formatUserMsg(StringBuffer sb, ChatRecordDTO chatRecordDTO) {
        final User user = chatRecordDTO.getUser();
        String tag = chatRecordDTO.getType() == MessageTypeEnum.ROBOT ? "[系统机器人] " : "";
        sb.append("#### [" + chatRecordDTO.getSendTime() + "] " + tag + user.getUsername() + "("
                + user.getAddress() + ")：\r\n");

        if (!StringUtils.isEmpty(chatRecordDTO.getImage())) {
            sb.append("> ![](" + chatRecordDTO.getImage() + ")\r\n");
        }
        if (!StringUtils.isEmpty(chatRecordDTO.getMessage())) {
            sb.append("> " + chatRecordDTO.getMessage() + "\r\n");
        }
    }
}
