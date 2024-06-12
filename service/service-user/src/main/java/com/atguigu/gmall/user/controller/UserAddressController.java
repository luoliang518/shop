package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.service.UserAdderssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/user")
public class UserAddressController {

    @Autowired
    private UserAdderssService userAdderssService;

    /**
     * 获取用户名
     * @return
     */
    @GetMapping(value = "/getUserAddress")
    public Result getUserAddress(){
        //用户名写死了---优化----TODO-----
        return Result.ok(userAdderssService.getUserAddress("liuyingjun"));
    }
}

