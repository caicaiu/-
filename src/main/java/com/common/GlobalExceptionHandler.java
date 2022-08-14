package com.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author 才
 */
//这个是扫描类，专门扫描RestController，Controller注解的
@ControllerAdvice(annotations = {RestController.class, Controller.class})
//响应注解
@ResponseBody

@Slf4j
public class GlobalExceptionHandler {

    //进行异常处理方法

    /**
     *  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)  指定他要出现的异常，内部是一个数组
     * @param ex  是出现的异常信息
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
 /*       //打印异常信息
        log.error(ex.getMessage());*/

        //如果异常信息含有Duplicate entry，那么就返回名字
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg=split[2]+"已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    /**
     * 分类包含菜品异常信息
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.info("当前的异常信息:{}" , ex.getMessage());
        return R.error(ex.getMessage());
    }
}