package com.pigman.community.enums;

public enum NotificationEnum {

    UNREAD(0),READ(1)
    ;

    private int status;

    public int getState() {
        return status;
    }

    NotificationEnum(int status) {
        this.status = status;
    }





}
