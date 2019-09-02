package com.pigman.community.dto;


import lombok.Data;

@Data
public class SocketMsg {

    private int type;
    private String fromUser;
    private String toUser;
    private String msg;



}
