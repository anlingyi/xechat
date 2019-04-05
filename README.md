[xechat](https://chat.xeblog.cn:8088/)
===

> 基于SpringBoot+STOMP协议实现的web聊天室

## 环境

* JDK1.8
* Maven

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

### 编译运行

finalName: `maven`打包的文件名，在`pom.xml`文件中由`<build>`标签内的`<finalName>`指定

env: 项目运行环境，测试环境`test` 生产环境`prod`
```
mvn clean install -Dmaven.test.skip=true
java -jar ${finalName}.jar --spring.profiles.active=${env}
```

## 功能

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

