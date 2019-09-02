package com.pigman.community.provider;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigman.community.dto.SocketMsg;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket实现类
 */
@ServerEndpoint(value="/websocket/{name}")
@Component
public class MyWebSocket {

    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();

    private Session session;

    private String name;

    private static Map<String,Session> map = new HashMap<String,Session>();


    /**
     * 建立连接
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name){
        this.session = session;
        this.name = name;
        map.put(session.getId(),session);
        webSocketSet.add(this);
        System.out.println("有新连接加入！" +name +"当前在线总人数为： "+webSocketSet.size());
       // this.session.getAsyncRemote().sendText(name+"   成功连接上WebSocket(其频道号: "+session.getId()+")-->当前在线总人数为："+webSocketSet.size());
        broadcast(name+"|进入了房间(其频道号为"+session.getId()+")-->当前在线总人数为："+webSocketSet.size()+"||0");
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        System.out.println("一用户退出连接！当前在线总人数为："+webSocketSet.size());
    }

    /**
     * 发送消息
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message,Session session,@PathParam("name") String name){
        System.out.println("来自客户端的消息: "+name+":"+message);

        ObjectMapper objectMapper = new ObjectMapper();
        SocketMsg socketMsg;
        try {
            socketMsg = objectMapper.readValue(message,SocketMsg.class);
            if(socketMsg.getType() == 1){
                //单聊
                socketMsg.setFromUser(session.getId());
                Session fromSession = map.get(socketMsg.getFromUser());
                Session toSession = map.get(socketMsg.getToUser());
                if(toSession != null){
                    fromSession.getAsyncRemote().sendText(name+"|"+socketMsg.getMsg()+"|"+socketMsg.getAvatar()+"|2");
                    toSession.getAsyncRemote().sendText(name+"|"+socketMsg.getMsg()+"|"+socketMsg.getAvatar()+"|2");
                }else{
                    fromSession.getAsyncRemote().sendText("|系统消息：对方不在线或者您输入的频道号不对||1");
                }
            }else{
                broadcast(name+"|"+socketMsg.getMsg()+"|"+socketMsg.getAvatar()+"|2");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 连接出错
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session ,Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 广播消息
     * @param message
     */
    public void broadcast(String message){
        for(MyWebSocket webSocket : webSocketSet) {
            webSocket.session.getAsyncRemote().sendText(message);
        }
    }


}
