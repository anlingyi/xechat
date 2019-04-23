document.write('<script src="js/config.js"></script>');

// 头像数量
var head_num = 50;
// 用户id
var uid = null;
var stompClient = null;
var onlineUserList;
var address = '未知地区';

// 页面加载完成后
window.onload = function () {
    init();
    $('#content').bind('keyup', showToUserList);
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
    var user = createUser();
    stompClient.connect(user, function (frame) {
        cacheUser(user);
        uid = frame.headers['user-name'];

        // 聊天室订阅
        stompClient.subscribe('/topic/chatRoom', function (data) {
            handleMessage(getData(data.body));
        });

        // 本地订阅
        stompClient.subscribe('/user/' + uid + '/chat', function (data) {
            handleMessage(getData(data.body));
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
            showUserList(obj.onlineUserList);
        });

        setConnected(true);
    }, function (error) {
        alert('请重新连接！');
        refresh();
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
        refresh();
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
    for (var i = 0; i < head_num; i++) {
        $('.avatar_list_div').append('<img src=./images/avatar/' + i + '.jpeg />');
    }
    $('.avatar_list_div img').bind('click', function () {
        $('#avatarList').attr('src', $(this).attr('src'));
    });
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

    showToUserList(content);

    var toUser = [uid];
    var strs = content.split('@');

    for (var i = 1; i < strs.length; i++) {
        var index = strs[i].indexOf(' ');
        var str = getUserIdByName(strs[i].substr(0, index != -1 ? index : strs[i].length));
        if (str !== undefined && str !== '') {
            toUser.push(str);
        }
    }

    var data = {
        "message": htmlEncode(content)
    };

    var pub = '/chatRoom';
    if (toUser.length > 1) {
        pub = '/chat';
        data.receiver = toUser;
    }

    data = JSON.stringify(data);

    sendMessage(pub, {}, data);
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
    var username = '匿名';
    var avatar = './images/avatar/1.jpeg';
    var user = getUser();

    if (user !== '') {
        username = user.username;
        avatar = user.avatar;
    } else {
        var inputName = $('#username').val();
        var inputAvatar = $('#avatarList').attr('src');
        if (inputName.trim() !== '' && inputName.trim().length < 9) {
            username = htmlEncode(inputName);
        }
        if (inputAvatar !== undefined || inputAvatar !== '') {
            avatar = inputAvatar
        }
    }

    return {
        'username': username,
        'avatar': avatar,
        'address': address
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
    jumpToLow();
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
    var event2 = isMe ? '' : 'ondblclick=showToUser("' + user.username + '")';

    var showMessage = data.message == null ? '' : htmlEncode(data.message);
    var showImage = data.image == null ? '' : '<div class="show_image"><img src="' + data.image + '"/></div>';
    var li = '<li class=' + style_css + ' id=' + data.messageId + '>';
    var a = '<a class="user" ' + event2 + '>';
    var avatar = '<img class="img-responsive avatar_" src=' + user.avatar + '\>';
    var span = '<span class="user-name">' + user.username + '</span></a>';
    var div_me = '<div class="reply-content-box"><span class="reply-time"><i class="glyphicon glyphicon-time"></i> '
        + data.sendTime + '&nbsp;<i class="glyphicon glyphicon-map-marker"></i>' + user.address + '</span>';
    var div = '<div class="reply-content-box"><span class="reply-time"><i class="glyphicon glyphicon-map-marker"></i>'
        + user.address + '&nbsp;<i class="glyphicon glyphicon-time"></i> ' + data.sendTime + '</span>';
    var div2 = '<div class="reply-content pr" ' + event + '><span class="arrow">&nbsp;</span>' + showMessage + showImage + '</div></div></li>';

    var html = li + a + avatar + span + (isMe ? div_me : div) + div2;

    $("#show_content").append(html);
    jumpToLow();
}

/**
 * 跳到聊天界面最底下
 */
function jumpToLow() {
    $("ul").scrollTop($("ul")[1].scrollHeight);
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
        case 'ROBOT':
            showRobotMsg(data);
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
    var obj = document.getElementById(data.revokeMessageId);
    if (obj) {
        obj.remove();
        showSystemMsg(data.user.username + '撤回了一条消息！');
        jumpToLow();
    }
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
    if (image === '' || image === undefined) {
        return;
    }

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

/**
 * 用户列表
 * @param data
 */
function showUserList(data) {
    onlineUserList = data;
    $('#onlineUserList').html('');
    for (var i = 0; i < data.length; i++) {
        var obj = data[i];
        $('#onlineUserList').append('<li id=' + obj.userId + '><a href="#"><div><img class="img-responsive avatar_list"' +
            ' src=' + obj.avatar + '><div class="name_list">' + obj.username + '</div></div></a></li>');
    }
}

/**
 * 退出
 */
function exit() {
    if (confirm('确定退出吗？')) {
        disconnect();
    }
}

/**
 * 缓存用户信息
 * @param data
 */
function cacheUser(data) {
    Cookies.set('user', data);
}

/**
 * 获取用户信息
 * @returns {*}
 */
function getUser() {
    var data = Cookies.get('user');
    if (data !== undefined) {
        return JSON.parse(data);
    }
    return '';
}

/**
 * 初始化登陆信息
 */
function init() {
    // 定位
    getAddress();
    var user = getUser();
    if (user !== '') {
        $('#username').hide();
        $('.avatar_list_div').remove();
        $('#avatarList').attr('src', user.avatar);
        $('.login-name').html(user.username);
        $('#logout').bind('click', logout);
        $('#logout').show();
    } else {
        // 初始化头像单选框
        showHeadPortrait();
    }
    $('#joinChat').bind('click', function () {
        $(this).button('loading');
        connect();
    });
}

/**
 * 注销
 */
function logout() {
    Cookies.remove('user');
    refresh();
}

/**
 * 刷新
 */
function refresh() {
    window.location.reload();
}

/**
 * 显示@用户列表
 * @param str
 */
function showToUserList() {
    var str = $('#content').val();
    var index = str.lastIndexOf('@');
    var name = str.substring(index + 1, str.length);
    $('.toUserList ul').html('');

    if (index != -1) {
        for (var i = 0; i < onlineUserList.length; i++) {
            var obj = onlineUserList[i];
            if (obj.userId === uid || obj.userId === 'robot') {
                continue;
            }

            if (name === '' || obj.username.indexOf(name) != -1) {
                $('.toUserList ul').append('<li><a onclick="setToUser(this)" data-name="' + obj.username + '"' +
                    ' data-userid="' + obj.userId + '"><div><img class=\'img-responsive avatar_list\' src="' + obj.avatar + '">\n' +
                    '<div class="name_list">' + obj.username + '</div></div></a></li>');
            }
        }
    }
}

/**
 * 设置@用户
 * @param ele
 */
function setToUser(ele) {
    var str = $('#content').val();
    var name = $(ele).data('name') + ' ';
    var index = str.lastIndexOf('@') + 1;
    var substr = str.substr(index, str.length);

    if (substr === '') {
        $('#content').val(str + name);
    } else {
        $('#content').val(str.substr(0, index) + name);
    }

    $('.toUserList ul').html('');
}

/**
 * 通过name获取userid
 * @param name
 * @returns {Document.userId|string}
 */
function getUserIdByName(name) {
    if (name == '') {
        return '';
    }

    for (var i = 0; i < onlineUserList.length; i++) {
        var obj = onlineUserList[i];
        if (obj.userId != uid && obj.username.indexOf(name) != -1) {
            return obj.userId;
        }
    }
}

/**
 * 双击用户头像@用户
 * @param name
 */
function showToUser(name) {
    $('#content').val($('#content').val() + '@' + name + ' ');
}

/**
 * 定位
 */
function getAddress() {
    $.ajax({
        type: "GET",
        url: "https://api.map.baidu.com/location/ip",
        data: {ak: "mHGeNrAuzZscixVMAjfaq1PPgKPOnoPm"},
        dataType: "jsonp",
        async: false,
        success: function (data) {
            address = data.content.address;
        },
        error: function (e) {
            console.log("定位失败！", e);
        }
    });
}

var token;

/**
 * 获取聊天记录列表
 */
function listRecord() {
    $.ajax({
        type: "GET",
        url: "/api/record",
        headers: {
            "token": token
        },
        dataType: "json",
        success: function (data) {
            codeMapping(data);
            if (data.code === 200) {
                $('#passwordModel').modal('hide');
                $('#record').show();
                var list = data.data.list;
                for (var i = 0; i < list.length; i++) {
                    var obj = list[i];
                    $('#recordList').append('<li onclick="readContent(this)" data-url="' + obj.url + '">' + obj.name + '</li>');
                }
            }
        }
    });
}

/**
 * 读取文件内容
 * @param url
 */
function readContent(e) {
    $.ajax({
        type: "GET",
        url: $(e).data('url'),
        headers: {
            "token": token
        },
        cache: false,
        success: function (data) {
            $('#record #content').html(marked(data));
        }
    });
}

/**
 * 校验密码
 */
function checkPassword() {
    var val = $('#record_password').val();
    if (val === '') {
        alert("请输入访问密码！！！");
    } else {
        token = btoa(val);
        listRecord();
    }
}

/**
 * 显示机器人消息
 * @param data
 */
function showRobotMsg(data) {
    var user = data.user;
    var event = 'ondblclick=showToRobot()';

    var li = '<li class="odd" id=' + data.messageId + '>';
    var a = '<a class="user" ' + event + '>';
    var avatar = '<img class="img-responsive avatar_" src=' + user.avatar + '\>';
    var span = '<span class="user-name">' + user.username + '</span></a>';
    var div = '<div class="reply-content-box"><span class="reply-time"><i class="glyphicon glyphicon-map-marker"></i>'
        + user.address + '&nbsp;<i class="glyphicon glyphicon-time"></i> ' + data.sendTime + '</span>';
    var div2 = '<div class="reply-content pr"><span class="arrow">&nbsp;</span>' + data.message + '</div></div></li>';

    var html = li + a + avatar + span + div + div2;

    $("#show_content").append(html);
    jumpToLow();
}

/**
 * 与机器人对话
 * @param name
 */
function showToRobot() {
    var val = $('#content').val();
    if (val.startsWith('#')) {
        return;
    }
    $('#content').val('#' + val);
}