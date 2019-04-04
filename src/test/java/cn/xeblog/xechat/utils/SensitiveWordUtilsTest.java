package cn.xeblog.xechat.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yanpanyi
 * @date 2019/4/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class SensitiveWordUtilsTest {

    @Test
    public void hasSensitiveWord() {
        Assert.assertTrue(SensitiveWordUtils.hasSensitiveWord("我们是共产主义接班人"));
        Assert.assertTrue(SensitiveWordUtils.hasSensitiveWord("中国的全称是中华人民共和国"));
        Assert.assertTrue(SensitiveWordUtils.hasSensitiveWord("我是一个合格的共产党员"));
        Assert.assertTrue(SensitiveWordUtils.hasSensitiveWord("我们在党中央的英明领导下生活越来越好了"));
        Assert.assertTrue(SensitiveWordUtils.hasSensitiveWord("热爱祖国，拥护中国共产党"));
        Assert.assertTrue(SensitiveWordUtils.hasSensitiveWord("cctv中央电视台正在播放新闻联播，庆祝中国共产党第十九次全国代表大会的顺利召开！"));
        Assert.assertFalse(SensitiveWordUtils.hasSensitiveWord("恭祝全球华人阖家欢乐！"));
        Assert.assertFalse(SensitiveWordUtils.hasSensitiveWord("人民代表大会制度是中国的根本政治制度，是中国人民民主专政政权的组织形式"));
    }

    @Test
    public void loveChina() {
        for (int i = 0; i < 100; i++) {
            System.out.println(SensitiveWordUtils.loveChina("中华"));
        }
    }
}