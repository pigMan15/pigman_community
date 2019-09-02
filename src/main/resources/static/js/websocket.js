var  websocket = null;
var user = null;

function connectWebSocket(SessionUser){

    user = SessionUser;

    if(user == null){
        alert("请登录后再使用！")
        return;
    }

    //var name = document.getElementById("nickname").value;

    var name = user.name;
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
        setStatusInnerHTML("我在线上");

    };



    websocket.onmessage = function(event){


        console.log(event.data)
        console.log((event.data.split("|")[2]))

        if(event.data.split("|")[3] == 0){

        }

        //封装消息对象
        if(event.data.split("|")[2] != "") {

            var mediaLeftElement = $("<div/>", {
                "class": "media-left"
            }).append($("<img/>", {
                "class": "media-object img-rounded",
                "src": event.data.split("|")[2]
            }));

        }else{
            setContactInnerHTML(mediaLeftElement)
        }

        var mediaBodyElement = $("<div/>",{
            "class":"media-body"
        }).append($("<h4/>",{
            "class":"media-heading",
            "html":event.data.split("|")[0]
        })).append($("<div/>",{
            "class":"menu",
            "html":event.data.split("|")[1]
        }));


        var mediaElement = $("<div/>",{
            "class":"media"
        }).append(mediaLeftElement)
            .append(mediaBodyElement);






        setMessageInnerHTML(mediaElement);

        refresh()
    }

    websocket.onclose = function(){
        setStatusInnerHTML("我下线了");
    }

    window.onbeforeunlod = function(){
        websocket.close();
    }
}

function setMessageInnerHTML(innerHTML){
    //document.getElementById("message").innerHTML += innerHTML+'<br/>';
    $('#message').append(innerHTML)
}

function setContactInnerHTML(innerHTML){
   $("#contact").append(innerHTML)
}

function setStatusInnerHTML(innerHTML){
    document.getElementById("status").innerHTML += innerHTML +'<br/>';
}

function closeWebSocket(){
    websocket.close();
}

function send(SessionUser){

    var message = document.getElementById('text').value;
    var toUser = document.getElementById("toUser").value;
    console.log(SessionUser)
    user = SessionUser;





    var socketMsg = {msg:message,toUser:toUser,avatar:user.avatarUrl,level:1};

    if(toUser == ''){
        socketMsg.type = 0;
    }else{
        socketMsg.type = 1;
    }
    websocket.send(JSON.stringify(socketMsg));


    refresh()


}

function setRefresh(){

    refresh()

    setTimeout(setRefresh,2000);
}

function refresh(){
    var scrollDiv = document.getElementById('message');
    if(scrollDiv.scrollHeight > scrollDiv.clientHeight) {
        //设置滚动条到最底部
        scrollDiv.scrollTop = scrollDiv.scrollHeight;

    }
}
