package com;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@MapperScan("com.mapper")
@ServletComponentScan
@EnableTransactionManagement
public class ReggieTakeOutMain {
    public static void main(String[] args) {
       SpringApplication.run(ReggieTakeOutMain.class);
       log.info("项目启动成功");
    }
}
