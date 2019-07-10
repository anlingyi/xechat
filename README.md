[xechat](https://chat.xeblog.cn:8088)
===

> 基于SpringBoot+STOMP协议实现的web聊天室

* [目录](#xechat)
   * [环境](#环境)
   * [部署](#部署)
      * [创建目录并授权](#创建目录并授权)
      * [修改应用地址](#修改应用地址)
      * [修改百度地图API](#修改百度地图api)
      * [编译运行](#编译运行)
   * [功能](#功能)
      * [登入](#登入)
      * [发送消息](#发送消息)
      * [@好友可进行私聊](#好友可进行私聊)
      * [敏感词检测](#敏感词检测)
      * [注销](#注销)
      * [消息撤消](#消息撤消)
      * [聊天记录](#聊天记录)
         * [密码加密方式](#密码加密方式)
   * [v1.2更新](#v12更新)
      * [接入图灵机器人](#接入图灵机器人)
      * [配置机器人](#配置机器人)
      * [新消息通知](#新消息通知)

## 环境

* JDK1.8
* Maven
* IDEA
* Lombok Plugin

## 部署

### 创建目录并授权

```
# 存放日志的目录
sudo mkdir /var/log/xechat
# 资源映射的目录
sudo mkdir /xechat
# 授权
sudo chmod 777 /var/log/xechat
sudo chmod 777 /xechat
```

### 修改应用地址

`application.properties` 配置文件里将 `app.url` 的值修改即可

```
#项目地址
app.url=http://localhost:${server.port}
```

### 修改百度地图API

项目中的定位功能使用的是百度地图的API

需将 `ak` 的值替换成自己的

![](https://i.loli.net/2019/04/06/5ca84c07051d7.png)

### 编译运行

finalName: `maven`打包的文件名，在`pom.xml`文件中由`<build>`标签内的`<finalName>`指定

env: 项目运行环境，测试环境`test` 生产环境`prod`
```
mvn clean install -Dmaven.test.skip=true
java -jar ${finalName}.jar --spring.profiles.active=${env}
```

## 功能

> https://xeblog.cn/articles/13

### 登入

> 登入成功后会将用户信息缓存到cookie中

登入界面

![](https://i.loli.net/2019/04/06/5ca79db0e46ca.png
)

选择头像输入昵称后点击登入按钮

![](https://i.loli.net/2019/04/06/5ca7ad4eca558.png)

![](https://i.loli.net/2019/04/06/5ca79db12d5f8.png)

系统广播上线消息

![](https://i.loli.net/2019/04/06/5ca79db1198e2.png)

### 发送消息

发送文本消息

![](https://i.loli.net/2019/04/06/5ca79db12e464.png)

发送图片消息

![](https://i.loli.net/2019/04/06/5ca79db14f6cf.png)

再次登入两个用户

![](https://i.loli.net/2019/04/06/5ca79db12d397.png)

![](https://i.loli.net/2019/04/06/5ca79db12e764.png)

左侧按钮为显示在线用户列表，右侧按钮为退出聊天室

![](https://i.loli.net/2019/04/06/5ca79db147f7b.png)

### @好友可进行私聊

![](https://i.loli.net/2019/04/06/5ca7a0cd0a49b.png)

![](https://i.loli.net/2019/04/06/5ca7a0cd0b406.png)

![](https://i.loli.net/2019/04/06/5ca7a0cd0bb47.png)

小毅这个用户收不到胖虎和小丸子的私聊信息

![](https://i.loli.net/2019/04/06/5ca7a0cd09e53.png)

### 敏感词检测

> 使用DFA算法检测文本是否包含敏感词（DFA：Deterministic Finite Automaton 确定性有限自动机）

敏感词列表

> 敏感词的配置是在 `resources` 目录下的 `sensitive-word.txt` 文件中，敏感词一行一个

![](https://i.loli.net/2019/04/06/5ca7a0ccb2ef1.png)

发送一个包含敏感词的文本信息（此处输入的敏感词为嘻嘻和哈哈）

![](https://i.loli.net/2019/04/06/5ca7a0cd05b1a.png)

成功被系统河蟹😏 河蟹后的处理是随机返回一个社会主义核心价值观。

> 富强、民主、文明、和谐、自由、平等、公正、法治、爱国、敬业、诚信、友善

![](https://i.loli.net/2019/04/06/5ca7a0cd0aae3.png)


过滤日志

![](https://i.loli.net/2019/04/06/5ca7a0cd12e86.png)

### 注销

点击右侧按钮退出聊天室，系统广播离线消息，在线用户列表和在线人数自动更新

![](https://i.loli.net/2019/04/06/5ca7a76746726.png)

点击注销按钮可清除当前的登入信息，不注销则可继续以当前的信息登入

![](https://i.loli.net/2019/04/06/5ca7a0ccf1886.png)

不注销直接登入

![](https://i.loli.net/2019/04/06/5ca7a7673d067.png)

### 消息撤消

双击自己发送的消息弹出撤消提示，确定后即可撤消这条消息（只可撤消3分钟内的消息）

![](https://i.loli.net/2019/04/06/5ca7a7674299c.png)

消息已经撤消，系统广播撤消消息

![](https://i.loli.net/2019/04/06/5ca7a767405c8.png)

![](https://i.loli.net/2019/04/06/5ca7a767447ad.png)

### 聊天记录

聊天记录访问地址 `http://ip:端口/record.html`

进入聊天记录页面需要输入访问密码 默认的访问密码为 `xechat`

#### 密码加密方式

> 先将密码经过 `BASE64` 加密后再进行 `MD5` 的32位小写加密

**加密流程**

 密码 `xechat` 经过 `BASE64` 加密后变为 `eGVjaGF0` ，再进行 `MD5` 32位小写加密后变为 `c85715fec7827f3b388af185b8e7db77`

**修改密码**

`application.properties` 配置文件里将 `chatrecord.password` 的值替换即可

```
#聊天记录页面访问密码(密码先经过base64加密后再进行32位md5加密)
chatrecord.password=c85715fec7827f3b388af185b8e7db77
```

输入密码点击确定

![](https://i.loli.net/2019/04/06/5ca83f71dbaba.png)

密码输入正确后显示聊天记录文件列表，按照 `yyyyMMdd` 的方式每天自动生成一个 `.md` 格式的文件

![](https://i.loli.net/2019/04/06/5ca83f71eeacf.png)

点击列表中的文件可以查看当天的聊天记录

![](https://i.loli.net/2019/04/06/5ca83f724e593.png)

![](https://i.loli.net/2019/04/06/5ca83f7252659.png)

## v1.2更新

### 接入图灵机器人

> [https://xeblog.cn/articles/14](https://xeblog.cn/articles/14)

> 图灵机器人官网 [http://www.turingapi.com](http://www.turingapi.com)  
> API文档 [https://www.kancloud.cn/turing/www-tuling123-com/718227](https://www.kancloud.cn/turing/www-tuling123-com/718227)

![xechat_v1.2.png](https://i.loli.net/2019/05/09/5cd3ed540d264.png)

### 配置机器人

**修改图灵ApiKey**

![turing_apikey.png](https://i.loli.net/2019/05/09/5cd3fa34df040.png)

`application.properties` 配置文件里修改 `turing.apiKey` 的值

```
#图灵apikey
turing.apiKey=xxx
```

**修改机器人信息**

修改 `cn.xeblog.xechat.constant.RobotConstant` 类中对应常量

```
package cn.xeblog.xechat.constant;

/**
 * 机器人相关常量
 *
 * @author yanpanyi
 * @date 2019/4/10
 */
public interface RobotConstant {
    /**
     * 存储的key
     */
    String key = "robot";
    /**
     * 触发机器人聊天的消息前缀
     */
    String prefix = "#";
    /**
     * 机器人名称
     */
    String name = "小小毅";
    /**
     * 机器人头像
     */
    String avatar = "./images/avatar/robot.jpeg";
    /**
     * 机器人地理位置
     */
    String address = "火星";
}
```

**修改机器人欢迎信息**

修改 `cn.xeblog.xechat.constant.MessageConstant` 类中对应常量

```
package cn.xeblog.xechat.constant;

/**
 * 消息模板
 *
 * @author anlingyi
 * @date 2019/5/7
 */
public interface MessageConstant {
    /**
     * 进入聊天室广播消息
     */
    String ONLINE_MESSAGE = "%s进入了聊天室";
    /**
     * 离开聊天室广播消息
     */
    String OFFLINE_MESSAGE = "%s离开了聊天室";
    /**
     * 机器人欢迎消息
     */
    String ROBOT_WELCOME_MESSAGE = "@%s 欢迎来到聊天室！消息内容以'#'开头的我就能收到哦（PS：双击我的头像与我聊天），" +
            "随时来撩我呀！";
}

```

**实际效果**

![xechat_v1.2_1.png](https://i.loli.net/2019/05/09/5cd4175b9b284.png)

![xechat_v1.2_2.png](https://i.loli.net/2019/05/09/5cd4175b98e71.png)

![xechat_v1.2_3.png](https://i.loli.net/2019/05/09/5cd4175b9fb63.png)

![xechat_v1.2_4.png](https://i.loli.net/2019/05/09/5cd4175bb280f.png)

![xechat_v1.2_5.png](https://i.loli.net/2019/05/09/5cd4175bacd75.png)

![靓仔语塞.gif](https://i.loli.net/2019/05/09/5cd4196fd7e4c.gif)

### 新消息通知

> [https://xeblog.cn/articles/22](https://xeblog.cn/articles/22)

通过勾选新消息通知、消息提示音等设置项开启相应功能。

![xechat_v1.2_6.png](https://oss.xeblog.cn/prod/89d1f5d2fa8d41e7b6e8d54014de9919.png)

通知效果

![xechat_v1.2_7.png](https://oss.xeblog.cn/prod/e8fc9629eaff4751aeb7b0e5330ffda5.png)