package cn.xeblog.xechat.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词树节点
 *
 * @author yanpanyi
 * @date 2019/4/4
 */
@Getter
@Setter
@ToString
public class SensitiveWordNode {

    /**
     * 节点所代表的字符
     */
    private char key;

    /**
     * 节点的子节点
     */
    private Map<Character, SensitiveWordNode> nextNodes;

    /**
     * 该节点是否是最后一个
     */
    private boolean end;

    public SensitiveWordNode(char key) {
        this.key = key;
        this.nextNodes = new HashMap();
        this.end = false;
    }

    public SensitiveWordNode getNextNode(char key) {
        return nextNodes.get(key);
    }

    public void putNextNode(SensitiveWordNode node) {
        nextNodes.put(node.getKey(), node);
    }
}
