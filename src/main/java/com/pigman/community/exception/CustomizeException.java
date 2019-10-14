package com.pigman.community.exception;



public class CustomizeException extends RuntimeException{

    public String message;
    public Integer code;


    public CustomizeException(ICustomizeErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public Integer getCode(){
        return code;
    }
}
