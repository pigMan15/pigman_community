package com.pigman.community.dto;


import lombok.Data;

@Data
public class SocketMsg {

    private int type;
    private int level; //消息等级
    private String fromUser;
    private String toUser;
    private String msg;
    private String avatar;


}
