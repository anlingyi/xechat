document.write('<script src="js/config.js"></script>');

var stompClient = null;

function connect(topic) {
    var socket = new SockJS('/xechat');
    stompClient = Stomp.over(socket);
    config();
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/' + topic, function (data) {
            console.log('resp: ' + data);
            showChatRoomMessage(data.body)
        });

        var userId = $('input[name = userId]').val();

        stompClient.subscribe('/user/' + userId + '/chat', function (data) {
            console.log('resp: ' + data);
            showMessage(data.body);
        });

        stompClient.subscribe('/user/' + userId + '/error', function (data) {
            console.log('resp: ' + data);
        });

        stompClient.subscribe('/topic/status', function (data) {
            console.log('resp: ' + data);
        });
        setConnected(true);
    });
}

function config() {
    // 每隔30秒做一次心跳检测
    stompClient.heartbeat.outgoing = 30000;
    // 客户端不接收服务器的心跳检测
    stompClient.heartbeat.incoming = 0;
}

function disconnect() {
    setConnected(false);

    if (stompClient !== null) {
        stompClient.disconnect();
    }

    console.log('Disconnected');
}

function setConnected(connected) {
    console.log(connected);
    sendMessage('/status', getUser(connected ? 1 : 0));
}

function sendMessage(topic, data) {
    stompClient.send(topic, {}, data);
}

function sendToUser() {
    var data = JSON.stringify({
        "receiver": $('input[name = toUserId]').val(),
        "sender": $('input[name = userId]').val(),
        "message": $('#message').val()
    });
    $('#content').append('<p>你说：' + $('#message').val() + '</p>');
    sendMessage('/chat', data);
}

function sendToChatRoom() {
    var data = JSON.stringify({
        "receiver": $('input[name = toUserId]').val(),
        "sender": $('input[name = userId]').val(),
        "message": $('#message').val()
    });
    sendMessage('/chatRoom', data);
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