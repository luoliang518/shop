package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * 用户收货地址相关的接口类
 */
public interface UserAdderssService {

    /**
     * 根据用户名查询用户的收货地址列表
     * @param username
     * @return
     */
    public List<UserAddress> getUserAddress(String username);
}
