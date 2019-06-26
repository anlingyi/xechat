package cn.xeblog.xechat.service.impl;

import cn.xeblog.xechat.constant.DateConstant;
import cn.xeblog.xechat.domain.dto.ChatRecordDTO;
import cn.xeblog.xechat.domain.mo.User;
import cn.xeblog.xechat.enums.MessageTypeEnum;
import cn.xeblog.xechat.service.ChatRecordService;
import cn.xeblog.xechat.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

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

    /**
     * 生成的文件后缀
     */
    private static final String FILE_SUFFIX = ".md";

    @Async
    @Override
    public void addRecord(ChatRecordDTO chatRecordDTO) {
        File file = new File(createFileName());
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
     * @return 文件名
     */
    private String createFileName() {
        Calendar calendar = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        sb.append(path);
        sb.append(calendar.get(Calendar.YEAR));
        sb.append(File.separator);
        sb.append(calendar.get(Calendar.MONTH) + 1);
        sb.append(File.separator);
        sb.append(DateUtils.getDate(calendar.getTime(), DateConstant.CHAT_RECORD_FILE_NAME));
        sb.append(FILE_SUFFIX);

        return sb.toString();
    }

    /**
     * 格式化内容
     *
     * @param chatRecordDTO 聊天记录对象
     * @return 格式化后的字符串
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
    public List<HashMap<String, Object>> listRecord(String directoryName) {
        File file = new File(path + directoryName);
        if (!file.exists()) {
            return null;
        }

        String[] tempList = file.list();
        if (tempList == null || tempList.length < 1) {
            return null;
        }

        List<HashMap<String, Object>> list = new ArrayList<>(tempList.length);
        HashMap<String, Object> map;
        String url = null;
        for (String name : tempList) {
            map = new HashMap<>(3, 1.0f);
            // 是否是文件
            boolean isFile = name.lastIndexOf(FILE_SUFFIX) != -1;
            if (isFile) {
                // 文件访问地址
                url = accessAddress + directoryName + name;
            }
            map.put("name", name);
            map.put("url", url);
            map.put("file", isFile);

            list.add(map);
        }

        return list;
    }

    /**
     * 格式化系统类型的消息
     *
     * @param sb StringBuffer对象
     * @param chatRecordDTO 聊天记录对象
     */
    private void formatSystemMsg(StringBuffer sb, ChatRecordDTO chatRecordDTO) {
        sb.append("#### [");
        sb.append(chatRecordDTO.getSendTime());
        sb.append("] 系统消息：\r\n");
        sb.append("> ");
        sb.append(chatRecordDTO.getMessage());
        sb.append("\r\n");
    }

    /**
     * 格式化用户类型的消息
     *
     * @param sb StringBuffer对象
     * @param chatRecordDTO 聊天记录对象
     */
    private void formatUserMsg(StringBuffer sb, ChatRecordDTO chatRecordDTO) {
        final User user = chatRecordDTO.getUser();
        String tag = chatRecordDTO.getType() == MessageTypeEnum.ROBOT ? "[系统机器人] " : "";
        sb.append("#### [");
        sb.append(chatRecordDTO.getSendTime());
        sb.append("] ");
        sb.append(tag);
        sb.append(user.getUsername());
        sb.append("(");
        sb.append(user.getAddress());
        sb.append(")：\r\n");

        if (!StringUtils.isEmpty(chatRecordDTO.getImage())) {
            sb.append("> ![](");
            sb.append(chatRecordDTO.getImage());
            sb.append(")\r\n");
        }
        if (!StringUtils.isEmpty(chatRecordDTO.getMessage())) {
            sb.append("> ");
            sb.append(chatRecordDTO.getMessage());
            sb.append("\r\n");
        }
    }
}
