package com.controller;

import com.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //定义一个文件路径
    @Value("${reggis.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file会生成一个临时的文件，如果我们没有指定文件的话，请求结束后，文件就会消失
        log.info("当前文件是：{}",file);
        //获取原始文件名，因为文件名后面有后缀，所以我们把后缀截取出来
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //生成随机的UUID和文件后缀拼接
        String filename =  UUID.randomUUID().toString()+suffix;
        File dir = new File(basePath);
        //如果文件不存在
        if(!dir.exists()){//文件是不是存在，true代表不存在，false代表存在
            //创建文件
            dir.mkdir();
        }
        try {
            //我们指定file存放的目录
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }

    /**
     * 文件下载
     * @param name  获取文件名
     * @param response  下载后返回给浏览器
     */
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response){

        try {
            //输入流  从计算机中读取文件
            FileInputStream fileInputStream = new FileInputStream(basePath+name);
            //输出流，将文件返回到浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            //指定返回文件的数据类型
            response.setContentType("image/jpeg");

            //进行读取
            int len = 0 ;
            byte[] bytes = new byte[1024];

            //每次读一行，知道读到文件尾
            while((len=fileInputStream.read(bytes))!=-1){
                //写入前端
                outputStream.write(bytes);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
