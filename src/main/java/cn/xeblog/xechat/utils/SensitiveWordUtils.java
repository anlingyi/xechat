package cn.xeblog.xechat.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感词处理
 *
 * @author yanpanyi
 * @date 2019/4/4
 */
@Slf4j
@Configuration
public class SensitiveWordUtils {

    private final static String[] LOVE_CHINA = {"富强", "民主", "文明", "和谐", "自由", "平等", "公正", "法治", "爱国",
            "敬业", "诚信", "友善"};

    /**
     * 敏感词库
     */
    private static Set<String> keyWords;
    /**
     * 敏感词根节点
     */
    private static SensitiveWordNode rootNode;

    /**
     * 读取敏感词
     *
     * @return
     */
    private static void readSensitiveWords() {
        keyWords = new HashSet<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(SensitiveWordUtils.class
                    .getResourceAsStream("/sensitive-word.txt"), "utf-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                keyWords.add(line.trim());
            }
        } catch (Exception e) {
            log.error("读取敏感词库出现异常！ error -> {}", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * 初始化敏感词库
     */
    private static void init() {
        if (rootNode != null) {
            return;
        }
        if (keyWords == null) {
            // 读取敏感词库
            readSensitiveWords();
            log.info("初始化敏感词库，共有{}个敏感词", keyWords.size());
        }

        // 初始化根节点
        rootNode = new SensitiveWordNode(' ');
        log.info("初始化敏感词节点");

        // 创建敏感词
        for (String keyWord : keyWords) {
            buildSensitiveWordNode(keyWord);
        }
    }

    /**
     * 构建敏感词节点
     *
     * @param keyWord
     * @return
     */
    private static void buildSensitiveWordNode(String keyWord) {
        SensitiveWordNode nowNode = rootNode;

        for (Character c : keyWord.toCharArray()) {
            SensitiveWordNode nextNode = nowNode.getNextNode(c);
            if (nextNode == null) {
                nextNode = new SensitiveWordNode(c);
                nowNode.putNextNode(nextNode);
            }
            nowNode = nextNode;
        }
        nowNode.setEnd(true);
    }

    /**
     * 判断是否存在敏感词
     *
     * @param text
     * @return
     */
    public static boolean hasSensitiveWord(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        if (rootNode == null) {
            log.info("敏感词节点未被初始化！");
            return false;
        }

        // 清除非法字符
        text = invalidClear(text);
        StringBuilder sb = new StringBuilder();
        SensitiveWordNode nowNode;

        for (int i = 0; i < text.length(); i++) {
            nowNode = rootNode;
            for (int j = i; j < text.length(); j++) {
                nowNode = nowNode.getNextNode(text.charAt(j));
                if (nowNode == null) {
                    sb.setLength(0);
                    break;
                }

                sb.append(nowNode.getKey());

                if (nowNode.isEnd()) {
                    log.debug("[{}] => 存在敏感词 -> {}", text, sb.toString());
                    return true;
                }
            }
        }

        return false;
    }

    @PostConstruct
    public void initData() {
        init();
    }

    /**
     * 热爱祖国，热爱人民
     *
     * @param text
     * @return
     */
    public static String loveChina(String text) {
        if (hasSensitiveWord(text)) {
            return LOVE_CHINA[(int) (Math.random() * LOVE_CHINA.length)];
        }

        return text;
    }

    /**
     * 清除非法字符
     *
     * @param str
     */
    private static String invalidClear(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？·\\s*|\t|\r|\n]";
        Matcher m = Pattern.compile(regEx).matcher(str);
        return m.replaceAll("").trim();
    }
}
