package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录的控制层
 */
@RestController
@RequestMapping(value = "/user/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public Result login(String username,
                        String password,
                        HttpServletRequest request){
        //登录
        AuthToken authToken = loginService.login(username, password);
        //获取登录时候的ip地址
        String ipAddress = IpUtil.getIpAddress(request);
        //将令牌和当前登录的ip地址绑定,将这个绑定关系存储到redis
        stringRedisTemplate.opsForValue().set(ipAddress, authToken.getAccessToken());
        //返回
        return Result.ok(authToken);
    }
}
