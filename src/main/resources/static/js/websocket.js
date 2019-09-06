var  websocket = null;
var user = null;
var IsActive = null;
var activeNums = null;
function connectWebSocket(SessionUser){

    user = SessionUser;

    if(user == null){
        alert("请登录后再使用！")
        return;
    }

    //var name = document.getElementById("nickname").value;

    var name = user.name;


    //显示房间内的用户



    findUsersByActive()


    if('WebSocket' in window){

        //防止重复登陆

        findUserById()
            if(IsActive == 1) {
                console.log(IsActive)
                alert("请勿重复登录！")
                return;
            }
        console.log(IsActive)
        websocket = new WebSocket("ws://139.155.36.35:8080/websocket/"+name);
    }else{
        alert("当前浏览器不支持WebSocket")
    }






    websocket.onerror = function(){
        setStatusInnerHTML("error");
        negative()
    };

    websocket.onopen = function(event){
        setStatusInnerHTML("我在线上");
        active()
        //console.log(event.data)
    };



    websocket.onmessage = function(event){
        document.getElementById("activeNums").innerHTML = activeNums

       // console.log(event.data)
       // console.log((event.data.split("|")[3]))

        //有新用户加入时
        findUsersByActive()



        if(event.data.split("|")[2] == 3){
            findUsersByNegative()
        }


        //封装消息对象
        if(event.data.split("|")[2] != "") {

            var mediaLeftElement = $("<div/>", {
                "class": "media-left"
            }).append($("<img/>", {
                "class": "media-object img-rounded",
                "src": event.data.split("|")[2]
            }));

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
        setStatusInnerHTML("我下线了")
         $("#"+user.name).empty()
         negative()
         findUsersByActive()


    }

    window.onbeforeunlod = function(){
        websocket.close();
        negative()
    }
}

//获取用户的active
function findUserById(){

    $.ajax({
        type:"POST",
        url:"/findUserById",
        contentType:"application/json",
        data:JSON.stringify({
            "id":user.id,
            "accountId":user.accountId,
            "name":user.name,
            "gmtCreate":user.gmtCreate,
            "gmtModified":user.gmtModified,
            "bio":user.bio,
            "avatarUrl":user.avatarUrl,
            "active":user.active
        }),
        success:function(response){
            console.log(response[0].active)

            IsActive = response[0].active
        },
        dataType:"json"
    });
}

//查找进入聊天室的用户
function findUsersByActive(){
    $.ajax({
        type:"GET",
        url:"/findUsersByActive",
        contentType:"application/json",
        success:function(response){

            activeNums = response.length;
            $("#contact").empty();
            for( m in response){
                var mediaLeftElement = $("<div/>", {
                    "class": "media-left",
                    "id":response[m].name
                }).append($("<img/>", {
                    "class": "media-object img-rounded",
                    "src": response[m].avatarUrl
                }));


                setContactInnerHTML(mediaLeftElement)
            }

        },
        dataType:"json"
    });
}

//查找离开聊天室的用户
function findUsersByNegative(){
    $.ajax({
        type:"GET",
        url:"/findUsersByNegative",
        contentType:"application/json",
        success:function(response){
            // console.log(response)
            ActiveUsers = response;

            for( m in response){
                $("#"+response[m].name).remove()


            }

        },
        dataType:"json"
    });
}

//用户上线，进入聊天室
function active(){
    $.ajax({
        type:"POST",
        url:"/user/active",
        contentType:"application/json",
        data:JSON.stringify({
            "id":user.id,
            "accountId":user.accountId,
            "name":user.name,
            "gmtCreate":user.gmtCreate,
            "gmtModified":user.gmtModified,
            "bio":user.bio,
            "avatarUrl":user.avatarUrl,
            "active":user.active
        }),
        success:function(response){
            console.log(response)
        },
        dataType:"json"
    });
}

//用户下线，离开聊天室
function negative(){
    $.ajax({
        type:"POST",
        url:"/user/negative",
        contentType:"application/json",
        data:JSON.stringify({
            "id":user.id,
            "accountId":user.accountId,
            "name":user.name,
            "gmtCreate":user.gmtCreate,
            "gmtModified":user.gmtModified,
            "bio":user.bio,
            "avatarUrl":user.avatarUrl,
            "active":user.active
        }),
        success:function(response){
            console.log(response)
        },
        dataType:"json"
    });
}



function setMessageInnerHTML(innerHTML){
    //document.getElementById("message").innerHTML += innerHTML+'<br/>';
    $('#message').append(innerHTML)
}

function setContactInnerHTML(innerHTML){

   $("#contact").append(innerHTML)
}

function setStatusInnerHTML(innerHTML){
    document.getElementById("status").innerHTML = innerHTML +'<br/>';
}

function closeWebSocket(){
    websocket.close();
    negative()
    findUsersByActive()

}

function send(SessionUser){

    var message = document.getElementById('text').value;
    var toUser = document.getElementById("toUser").value;
    //console.log(SessionUser)
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
