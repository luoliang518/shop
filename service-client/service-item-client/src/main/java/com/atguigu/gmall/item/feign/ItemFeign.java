package com.atguigu.gmall.item.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 商品详情页的feign接口定义
 */
@FeignClient(name = "service-item", path = "/item")
public interface ItemFeign {

    /**
     * 获取商品详情页需要的全部的数据
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    public Map<String, Object> getItemInfo(@PathVariable(value = "skuId") Long skuId);
}
