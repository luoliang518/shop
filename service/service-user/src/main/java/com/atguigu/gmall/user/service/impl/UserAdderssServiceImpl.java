package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAdderssService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户收货地址相关的接口类的实现类
 */
@Service
public class UserAdderssServiceImpl implements UserAdderssService {

    @Resource
    private UserAddressMapper userAddressMapper;
    /**
     * 根据用户名查询用户的收货地址列表
     *
     * @param username
     * @return
     */
    @Override
    public List<UserAddress> getUserAddress(String username) {
        return userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, username));
    }
}
