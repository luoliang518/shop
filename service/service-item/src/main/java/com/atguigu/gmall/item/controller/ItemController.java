package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 商品详情页数据查询的接口
 */
@RestController
@RequestMapping(value = "/item")
public class ItemController {

    @Autowired
    private ItemService itemService;
    /**
     * 获取商品详情页需要的全部的数据
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    public Map<String, Object> getItemInfo(@PathVariable(value = "skuId") Long skuId){
        return itemService.getItemInfo(skuId);
    }
}
