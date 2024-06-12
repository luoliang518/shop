package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * 商品详情页使用的接口类
 */
public interface ItemService {

    /**
     * 查询商品的详细的信息
     * @param skuId
     * @return
     */
    public Map<String, Object> getItemInfo(Long skuId);
}
