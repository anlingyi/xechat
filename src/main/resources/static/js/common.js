//会话id
var sessionId = null;
//用户名
var username = "匿名";
//头像
var headPortrait = "0.jpg";
//头像数量
var head_num = 9;
//定位地址
var address = "未知地区";
//页面加载完成监听回车事件
$(function () {
    document.getElementById("content").addEventListener("keydown", function (e) {
        if (e.keyCode != 13) return;
        e.preventDefault();
        //发送信息
        $("#send_btn").click();
    });
    //初始化头像单选框
    showHeadPortrait();
})

//生成头像单选
function showHeadPortrait() {
    var ck = "checked";
    for (var i = 1; i < head_num; i++) {
        if (i > 1) {
            ck = '';
        }
        $("#showHead").append("<div class='head_div'><img src='./images/" + i + ".jpg' width='44' height='44'><input type='radio' value='" + i + "' name='headPortrait' " + ck + "></div>");
    }
}

//发送信息
function sendMessage() {
    //获取发送的内容
    var content = $("#content").val();
    //内容不能为空
    if (content.trim().length < 1) {
        return;
    }
    websocket.send(htmlEncode(content));
    $("#content").val("");
}

//显示信息
function show_content(data) {
    var style_css = "odd";
    if (isMe(data.id)) {
        style_css = "even";
    }
    var li = "<li class=" + style_css + ">";
    var a = "<a class=\"user\" href=\"#\">";
    var img = "<img class=\"img-responsive avatar_\" src=\"./images/" + data.headPortrait + "\">";
    var span = "<span class=\"user-name\">" + data.username + "</span></a>";
    var div = "<div class=\"reply-content-box\"><span class=\"reply-time\">" + data.time + "&nbsp;From:" + data.address + "</span>";
    var div2 = "<div class=\"reply-content pr\"><span class=\"arrow\">&nbsp;</span>" + data.content + "</div></div></li>";
    var html = li + a + img + span + div + div2;
    $("#show_content").append(html);
    low_list();
}

//跳到聊天界面最底下
function low_list() {
    $("ul").scrollTop($("ul")[0].scrollHeight);
}

//转义 防止html注入
function htmlEncode(str) {
    var ele = document.createElement("span");
    ele.appendChild(document.createTextNode(str));
    return ele.innerHTML;
}

//显示系统信息
function show_sys_message(message) {
    var li = "<li class=\"text-center join_li\" id=\"join_message\">系统提示：" + message + "</li>";
    $("#show_content").append(li);
}

//显示在线人数
function show_online_num(num) {
    $("#online_num").html(num);
}

//显示机器人信息
function show_robot_message(data) {
    var img_url = "";
    if ("url" in data) {
        img_url = "<br/><a href='" + data.url + "' target='_blank'>打开链接</a>";
    }
    var style_css = "odd";
    var li = "<li class=" + style_css + ">";
    var a = "<a class=\"user\" href=\"#\">";
    var img = "<img class=\"img-responsive avatar_\" src=\"statics/images/" + data.headPortrait + "\">";
    var span = "<span class=\"user-name\">" + data.username + "</span></a>";
    var div = "<div class=\"reply-content-box\"><span class=\"reply-time\">" + data.time + "&nbsp;From:" + data.address + "</span>";
    var div2 = "<div class=\"reply-content pr\"><span class=\"arrow\">&nbsp;</span>" + data.text + img_url + "</div></div></li>";
    var html = li + a + img + span + div + div2;
    $("#show_content").append(html);
    low_list();
}

//判断是否是本人发送的信息
function isMe(id) {
    if (sessionId == id) {
        return true;
    }
    return false;
}

/*websocket*/
var websocket = null;//初始化websocket
//判断当前浏览器是否支持websocket
function isWebSocket() {
    if ('WebSocket' in window) {
        return true;
    }
    return false;
}

//获取用户信息后连接服务器
function createChat(path) {
    name = $("#username").val();
    head = $("input[name='headPortrait']:checked").val();
    if (name.trim() != "" && name.trim().length < 9) {
        username = htmlEncode(name);
    }
    if (head > 0 && head < head_num) {
        headPortrait = head + ".jpg";
    }
    var data = path + "/chat" + "/" + username + "/" + headPortrait + "/" + address;
    chatService(data);
}

//聊天界面显隐
function showChat(isShow) {
    if (isShow) {
        $("#login").hide();
        $("#showChat").show();
        return;
    }
    $("#login").show();
    $("#showChat").hide();
}

/**
 * 通信服务
 * @param data
 */
function chatService(data) {
    if (isWebSocket()) {
        if (websocket == null) {
            websocket = new WebSocket("ws://" + data);
        }
        //连接发生错误的回调方法
        websocket.onerror = function () {
//	    	console.log("连接失败！");
            alert("连接服务器失败，请稍后再试！");
        };

        //连接成功建立的回调方法
        websocket.onopen = function () {
//	    	  console.log("连接成功！");
            showChat(true);
        };

        //接收到消息的回调方法
        websocket.onmessage = function (event) {
//	    	console.log("收到新消息！");
            var obj = $.parseJSON(event.data);
            //系统消息
            if ("System" in obj) {
                show_online_num(obj.System.count);
                show_sys_message(obj.System.message);
            }
            //用户消息
            if ("User" in obj) {
                show_content(obj.User);
            }
            //机器人消息
            if ("Robot" in obj) {
                show_robot_message(obj.Robot);
            }
            low_list();
        };

        //连接关闭的回调方法
        websocket.onclose = function () {
            closeWebSocket();
            alert("与服务器断开连接，请重新连接服务器！");
            showChat(false);
        };
    } else {
        console.log("当前浏览器不支持websocket,请更换浏览器！");
        alert("当前浏览器不支持websocket,请更换浏览器！");
    }
}

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
    closeWebSocket();
};

//关闭WebSocket连接
function closeWebSocket() {
    if (websocket != null) {
        websocket.close();
        websocket = null;
    }
}

/*定位服务*/
function getAddress() {
    $.ajax({
        type: "GET",
        url: "https://api.map.baidu.com/location/ip",
        data: {ak: "mHGeNrAuzZscixVMAjfaq1PPgKPOnoPm"},
        dataType: "jsonp",
        success: function (data) {
            address = data.content.address;
        },
//		error:function(){
//			alert("定位失败！");
//		}
    });
}
