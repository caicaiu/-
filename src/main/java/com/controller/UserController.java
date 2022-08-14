package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.R;
import com.emtity.User;
import com.service.UserService;
import com.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号码吗
        String phone = user.getPhone();
        //严谨一点，判断号码是否为空
        if(StringUtils.isNotEmpty(phone)){
            //获取随机验证码
            String code  = ValidateCodeUtils.generateValidateCode(4).toString();
            //打印是否有验证码
            log.info("验证码信息为：{}",code);
            //发送短信给手机
            /*SMSUtils.sendMessage("你的短信验证码为","",phone,code);*/

            //将验证码存到session中
            session.setAttribute(phone,code);
            return R.success("发送验证码成功");

        }

        return R.success("未知错误");
    }


    /**
     * 用户登录
     * @param map 手机和验证码
     * @param session 获取存放的session数据
     * @return  返回用户信息，让浏览器也保存一份
     */
    @PostMapping("/login")
    public R<User> code(@RequestBody Map<String,String> map, HttpSession session){
        //测试是否能成功获取
        log.info("map接收的信息{}",map);
        //获取手机号码
        String phone = map.get("phone");
        //获取code
        String code = map.get("code");

        //从session中取出code
        Object phoneCode = session.getAttribute(phone).toString();

        //判断是否想等
        if(code!=null&&code.equals(phoneCode)){
            //判断用户是否是新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            //是新用户，存储到数据库中
            if(user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            //如果是老用户，就直接放行
            //然后存放用户的id到session
            session.setAttribute("user",user.getId());

            //成功返回,为什么返回用户信息，因为在返回给浏览器，让他保存用户信息
            return R.success(user);
        }

        return R.error("手机验证码错误");
    }
}
