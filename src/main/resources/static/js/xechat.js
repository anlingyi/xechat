document.write('<script src="js/config.js"></script>');

// 头像数量
var head_num = 9;
// 聊天室用户id，存储在cookie中的key
var cr_userId = 'cr_userId';
var stompClient = null;

/**
 * 连接服务器，订阅相关地址
 */
function connect() {
    var socket = new SockJS('/xechat');
    stompClient = Stomp.over(socket);
    config();
    sub(setUserId(cr_userId));
}

/**
 * 订阅地址
 */
function sub(uid) {
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chatRoom', function (data) {
            showUserMsg(getData(data.body), false);
        });

        stompClient.subscribe('/user/' + uid + '/chat', function (data) {
            console.log('resp: ' + data);
            showMessage(data.body);
        });

        stompClient.subscribe('/user/' + uid + '/error', function (data) {
            console.log('resp: ' + data);
        });

        stompClient.subscribe('/topic/status', function (data) {
            var obj = getData(data.body);
            showOnlineNum(obj.onlineCount);
            showSystemMsg(obj);
        });

        setConnected(true);
    });

}

function getData(data) {
    var obj = JSON.parse(data);
    codeMapping(obj);
    return obj.data;
}

/**
 * stomp配置
 */
function config() {
    // 每隔30秒做一次心跳检测
    stompClient.heartbeat.outgoing = 30000;
    // 客户端不接收服务器的心跳检测
    stompClient.heartbeat.incoming = 0;
}

/**
 * 关闭连接
 */
function disconnect() {
    if (stompClient !== null) {
        setConnected(false);
        stompClient.disconnect();
        console.log('Disconnected');
    }
}

/**
 * 设置连接状态
 * @param connected true连接成功，false连接失败
 */
function setConnected(connected) {
    sendMessage('/status', {}, createUser(connected ? 1 : 0));
    showChatRoom(connected);
}

/**
 * 发送信息到指定地址
 * @param pub 发布地址
 * @param header 设置请求头
 * @param data 发送的内容
 */
function sendMessage(pub, header, data) {
    stompClient.send(pub, header, data);
}

function sendToUser() {
    var data = JSON.stringify({
        "receiver": $('input[name = toUserId]').val(),
        "sender": $('input[name = userId]').val(),
        "message": $('#message').val()
    });
    $('#content').append('<p>你说：' + $('#message').val() + '</p>');
    sendMessage('/chat', {}, data);
}

function showMessage(data) {
    var obj = JSON.parse(data);
    $('#content').append('<p>好友' + obj.sender + '说：' + obj.message + '</p>');
}

function showChatRoomMessage(data) {
    var obj = JSON.parse(data).data;
    $('#content').append('<p>来自聊天室消息(' + obj.address + ')' + obj.username + '说：' + obj.message + '</p>');
}

function getUser(data) {
    return JSON.stringify({
        "userId": $('input[name = userId]').val(),
        "username": '马云',
        "status": data
    });
}

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
});

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

/**
 * 发送信息到聊天室
 */
function sendToChatRoom() {
    // 获取发送的内容
    var content = $("#content").val();
    // 内容不能为空
    if (content.trim().length < 1) {
        return;
    }
    var data = JSON.stringify({
        "receiver": '',
        "sender": Cookies.get(cr_userId),
        "message": htmlEncode(content)
    });
    sendMessage('/chatRoom', {}, data);
    $("#content").val("");
}

/**
 * 响应码映射
 * @param date
 */
function codeMapping(date) {
    switch (date.code) {
        case 200:
            break;
        case 404:
            alert("404");
            break;
        default:
            alert(date.desc);
            break;
    }
}

/**
 * 设置用户id
 * @param key
 * @returns {*}
 */
function setUserId(key) {
    var uid = Cookies.get(key);

    if (uid !== undefined) {
        console.log('你已登陆!');
    } else {
        console.log('创建uid!');
        uid = createUid();
    }

    console.log('uid -> ', uid);
    Cookies.set(key, uid);

    return uid;
}

/**
 * 创建用户id
 */
function createUid() {
    // guid
    return (s4() + s4() + "-" + s4() + "-" + s4() + "-" + s4() + "-" + s4() + s4() + s4());
}

/**
 * 生成四位随机字符
 * @returns {string}
 */
function s4() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
}

/**
 * 转义 防止html注入
 * @param str
 * @returns {string}
 */
function htmlEncode(str) {
    var ele = document.createElement("span");
    ele.appendChild(document.createTextNode(str));
    return ele.innerHTML;
}

/**
 * 显示在线人数
 * @param num
 */
function showOnlineNum(num) {
    $("#online_num").html(num);
}

/**
 * 聊天室界面显隐
 * @param isShow
 */
function showChatRoom(isShow) {
    if (isShow) {
        $("#login").hide();
        $("#showChat").show();
        return;
    }
    $("#login").show();
    $("#showChat").hide();
}

/**
 * 创建用户
 * @param status
 * @returns {string}
 */
function createUser(status) {
    var username = $("#username").val();
    var option = $("input[name='headPortrait']:checked").val();
    var headPortrait = './images/' + (option > head_num || option < 0 ? 0 : option) + '.jpg';
    console.log('headPortrait ->', headPortrait);
    if (username.trim() != "" && username.trim().length < 9) {
        username = htmlEncode(username);
    } else {
        username = '匿名';
    }

    var data = JSON.stringify({
        'userId': Cookies.get(cr_userId),
        'username': username,
        'avatar': headPortrait,
        'status': status
    });

    return data;
}

// 监听窗口关闭事件，当窗口关闭时，主动去关闭stomp连接
window.onbeforeunload = function () {
    disconnect();
};

/**
 * 显示系统消息
 * @param message
 */
function showSystemMsg(data) {
    var message = '系统提示：';
    var username = data.user.username;
    if (data.user.status === 1) {
        message += username + '进入了聊天室！';
    } else {
        message += username + '离开了聊天室！';
    }

    var li = '<li class="text-center join_li" id="join_message">' + message + '</li>';
    $("#show_content").append(li);
}

/**
 * 显示用户消息
 * @param data
 */
function showUserMsg(data, isMe) {
    var user = data.user;

    var style_css = user.userId === Cookies.get(cr_userId) ? 'even' : 'odd';
    var li = '<li class=' + style_css + '>';
    var a = '<a class="user" href="#">';
    var img = '<img class="img-responsive avatar_" src=' + user.avatar + '\>';
    var span = '<span class="user-name">' + user.username + '</span></a>';
    var div = '<div class="reply-content-box"><span class="reply-time">' + data.time + '&nbsp;From:' + user.address + '</span>';
    var div2 = '<div class="reply-content pr"><span class="arrow">&nbsp;</span>' + htmlEncode(data.message) + '</div></div></li>';
    var html = li + a + img + span + div + div2;
    $("#show_content").append(html);
    jumpToLow();
}

/**
 * 跳到聊天界面最底下
 */
function jumpToLow() {
    $("ul").scrollTop($("ul")[0].scrollHeight);
}