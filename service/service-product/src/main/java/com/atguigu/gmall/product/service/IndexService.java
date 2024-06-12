package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 首页使用的方法接口的接口类
 */
public interface IndexService {

    /**
     * 查询首页的分类信息
     * @return
     */
    public List<JSONObject> getIndexCategory();
}
