//强制关闭浏览器  调用websocket.close（）,进行正常关闭
window.onunload = function() {
    disconnect()
}
function connect(){
    stompClient.connect({},function(frame){//连接WebSocket服务端
        console.log('====>Connected:' + frame);
        // 监听消息
        receive();
    });
}

//接收消息
function receive() {
    stompClient.subscribe('/topic/hi',function(response){
        showResponse(response.body);
    });
}


//发送消息
function send(msg){
    //可以理解成直接让应用将推送消息转发到broker中。
    //stompClient.send("/user/zhangsan/aaa/hello",{},"222")

    //先进@messageMapping的controller方法，再决定消息的去向
    stompClient.send("/app/hi",{}, msg)

}

//关闭双通道
function disconnect(){
    if(stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function showResponse(message){
    var response = $("#response");
    response.append("<span>"+message+"</span></br>");
}


$(function(){
    (function(scope){
        var socket = new SockJS('http://127.0.0.1:8080/ws'); //连接SockJS的endpoint名称为"endpointOyzc"
        //var socket = new SockJS('/ws'); //在static下的也可以这么写
        stompClient = Stomp.over(socket);//使用STMOP子协议的WebSocket客户端
        // 注册到全局
        scope.stompClient = stompClient;
        // 建立连接
        connect();
        $('#submitBtn').click(function(){
            send($('#inputArea').val());
        });
    })(window);
});
