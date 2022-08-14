package com.common;

/**
 * 自定义异常类
 *
 * @author 覃江才
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }
}
