var  websocket = null;
var user = null;

function connectWebSocket(){
    var name = document.getElementById("nickname").value;

    console.log(name)
    if(name === ""){
        alert("请输入用户昵称!");
        return;

    }

    setContactInnerHTML(name);

    if('WebSocket' in window){
        websocket = new WebSocket("ws://localhost:8080/websocket/"+name);
    }else{
        alert("当前浏览器不支持WebSocket")
    }

    websocket.onerror = function(){
        setStatusInnerHTML("error");
    };

    websocket.onopen = function(event){
        setStatusInnerHTML("Loc MSG:连接建立成功");

    };

    websocket.onmessage = function(event){
        var splits = event.data.split(":");
        console.log(user)
        setMessageInnerHTML(event.data);
    }

    websocket.onclose = function(){
        setStatusInnerHTML("Loc MSG:关闭连接");
    }

    window.onbeforeunlod = function(){
        websocket.close();
    }
}

function setMessageInnerHTML(innerHTML){
    document.getElementById("message").innerHTML += innerHTML+'<br/>';
}

function setContactInnerHTML(innerHTML){
    document.getElementById("contact").innerHTML += innerHTML +'<br/>';
}

function setStatusInnerHTML(innerHTML){
    document.getElementById("status").innerHTML += innerHTML +'<br/>';
}

function closeWebSocket(){
    websocket.close();
}

function send(SessionUser){
    user = SessionUser;
    var message = document.getElementById('text').value;
    var toUser = document.getElementById("toUser").value;
    var socketMsg = {msg:message,toUser:toUser};

    if(toUser == ''){
        socketMsg.type = 0;
    }else{
        socketMsg.type = 1;
    }
    websocket.send(JSON.stringify(socketMsg));


    refresh();


}

function refresh(){
    var scrollDiv = document.getElementById('message');
    if(scrollDiv.scrollHeight > scrollDiv.clientHeight) {
        //设置滚动条到最底部
        scrollDiv.scrollTop = scrollDiv.scrollHeight;

    }

    setTimeout(refresh,2000);
}
