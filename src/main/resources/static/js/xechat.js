document.write('<script src="js/config.js"></script>');

// 头像数量
var head_num = 9;
// 用户id
var uid = null;
var stompClient = null;

// 页面加载完成后
window.onload = function () {
    // 初始化头像单选框
    showHeadPortrait();
    // 页面加载完成监听回车事件
    document.getElementById("content").addEventListener("keydown", function (e) {
        if (e.keyCode != 13) return;
        e.preventDefault();
        // 发送信息
        sendToChatRoom();
    });
}

// 监听窗口关闭事件，当窗口关闭时，主动去关闭stomp连接
window.onbeforeunload = disconnect;

/**
 * 连接服务器，订阅相关地址
 */
function connect() {
    var socket = new SockJS('/xechat');
    stompClient = Stomp.over(socket);
    // 配置stomp
    config();
    // 订阅地址
    sub();
}

/**
 * 订阅地址
 */
function sub() {
    stompClient.connect(createUser(), function (frame) {
        console.log('Connected: ', frame);
        uid = frame.headers['user-name'];
        console.log('uid -> ', uid);

        // 聊天室订阅
        stompClient.subscribe('/topic/chatRoom', function (data) {
            handleMessage(getData(data.body));
        });

        // 本地订阅
        stompClient.subscribe('/user/' + uid + '/chat', function (data) {
            console.log('resp: ' + data);
        });

        // 错误信息订阅
        stompClient.subscribe('/user/' + uid + '/error', function (data) {
            getData(data.body);
        });

        // 聊天室动态订阅
        stompClient.subscribe('/topic/status', function (data) {
            var obj = getData(data.body);
            showOnlineNum(obj.onlineCount);
            handleSystemMsg(obj);
        });

        setConnected(true);
    });

}

/**
 * 解析响应数据
 * @param data
 * @returns {*}
 */
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

/**
 * 生成头像列表
 */
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
        "message": htmlEncode(content)
    });
    sendMessage('/chatRoom', {}, data);
    $('#content').val('');
    changeBtn();
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
 * @returns {string}
 */
function createUser() {
    var username = $("#username").val();
    var option = $("input[name='headPortrait']:checked").val();
    var headPortrait = './images/' + (option > head_num || option < 0 ? 0 : option) + '.jpg';
    console.log('headPortrait ->', headPortrait);
    if (username.trim() != "" && username.trim().length < 9) {
        username = htmlEncode(username);
    } else {
        username = '匿名';
    }

    return {
        'username': username,
        'avatar': headPortrait
    };
}

/**
 * 处理系统消息
 * @param message
 */
function handleSystemMsg(data) {
    var message = '系统提示：';
    var username = data.user.username;
    if (data.user.status === 1) {
        message += username + '进入了聊天室！';
    } else {
        message += username + '离开了聊天室！';
    }

    showSystemMsg(message);
}

/**
 * 显示用户消息
 * @param data
 */
function showUserMsg(data) {
    var user = data.user;
    var isMe = user.userId === uid;
    var style_css = isMe ? 'even' : 'odd';
    var event = isMe ? 'ondblclick=revokeMessage("' + data.messageId + '")' : '';

    var showMessage = data.message == null ? '' : htmlEncode(data.message);
    var showImage = data.image == null ? '' : '<div class="show_image"><img src="' + data.image + '"/></div>';
    var li = '<li class=' + style_css + ' id=' + data.messageId + '>';
    var a = '<a class="user" href="#">';
    var avatar = '<img class="img-responsive avatar_" src=' + user.avatar + '\>';
    var span = '<span class="user-name">' + user.username + '</span></a>';
    var div = '<div class="reply-content-box"><span class="reply-time">' + data.sendTime + '&nbsp;From:' + user.address + '</span>';
    var div2 = '<div class="reply-content pr" ' + event + '><span class="arrow">&nbsp;</span>' + showMessage + showImage + '</div></div></li>';

    var html = li + a + avatar + span + div + div2;
    $("#show_content").append(html);
    jumpToLow();
}

/**
 * 跳到聊天界面最底下
 */
function jumpToLow() {
    $("ul").scrollTop($("ul")[0].scrollHeight);
}

/**
 * 处理消息
 * @param data
 */
function handleMessage(data) {
    switch (data.type) {
        case 'USER':
            showUserMsg(data);
            break;
        case 'SYSTEM':
            break;
        case 'REVOKE':
            showRevokeMessage(data);
            break;
        default:
            break;
    }
}

/**
 * 显示撤回消息信息
 * @param data
 */
function showRevokeMessage(data) {
    document.getElementById(data.revokeMessageId).remove();
    showSystemMsg(data.user.username + '撤回了一条消息！');
}

/**
 * 显示系统消息
 * @param message
 */
function showSystemMsg(message) {
    var li = '<li class="text-center join_li" id="join_message">' + message + '</li>';
    $("#show_content").append(li);
}


/**
 * 撤消消息
 * @param messageId
 */
function revokeMessage(messageId) {
    if (messageId === '' || messageId === undefined || !confirm('确定撤回这条消息吗？')) {
        return;
    }

    sendMessage('/chatRoom/revoke', {}, messageId);
}

/**
 * 控制按钮显示
 */
function changeBtn() {
    // 如果输入的内容不为空，则展示发送按钮，否则展示上传图片按钮
    if ($('#content').val().trim() != '') {
        $('#send_btn').show();
        $('#picture_btn').hide();
    } else {
        $('#picture_btn').show();
        $('#send_btn').hide();
    }
}

/**
 * 发送图片
 */
function sendImage() {
    var image = $("#file").val();
    var filename = image.replace(/.*(\/|\\)/, "");
    var fileExt = (/[.]/.exec(filename)) ? /[^.]+$/.exec(filename.toUpperCase()) : '';

    var file = $('#file').get(0).files[0];
    var fileSize = file.size;
    var mb = 30;
    var maxSize = mb * 1024 * 1024;

    if (fileExt != 'PNG' && fileExt != 'GIF' && fileExt != 'JPG' && fileExt != 'JPEG' && fileExt != 'BMP') {
        alert('发送失败，图片格式有误！');
        return;
    } else if (parseInt(fileSize) > parseInt(maxSize)) {
        alert('上传的图片不能超过' + mb + 'MB');
        return;
    } else {
        var data = new FormData();
        data.append('file', file);
        $.ajax({
            url: "/api/upload/image",
            type: 'POST',
            data: data,
            dataType: 'JSON',
            cache: false,
            processData: false,
            contentType: false,
            success: function (data) {
                codeMapping(data);
                var rep = data.data;
                sendImageToChatRoom(rep.path);
            }
        });
    }
}

/**
 * 选择文件
 */
function selectFile() {
    $('#file').click();
}

/**
 * 发送图片到聊天室
 */
function sendImageToChatRoom(image) {
    var data = JSON.stringify({
        "image": image
    });
    sendMessage('/chatRoom', {}, data);
}