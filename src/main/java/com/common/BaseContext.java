package com.common;

/**
 * 线程局部变量
 */
public class BaseContext  {

    public static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static Long getId(){
        return threadLocal.get();
    }

    public static void setId(Long id){
        threadLocal.set(id);
    }

}
