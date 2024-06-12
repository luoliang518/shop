package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.Java1227Cache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品管理微服务提供的内部接口的控制层
 */
@RestController
@RequestMapping(value = "/api/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 查询sku的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    @Java1227Cache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId){
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 查询分类的信息
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/getCategory/{category3Id}")
    @Java1227Cache(prefix = "getCategory:")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id){
        return itemService.getCategory(category3Id);
    }

    /**
     * 根据sku的id 查询sku的图片列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuImage/{skuId}")
    @Java1227Cache(prefix = "getSkuImage:")
    public List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId){
        return itemService.getSkuImage(skuId);
    }

    /**
     * 查询商品的价格
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getPrice/{skuId}")
    @Java1227Cache(prefix = "getPrice:")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 查询页面展示用的销售属性信息并且标识出当前sku的属性
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSpuSaleAttr/{skuId}/{spuId}")
    @Java1227Cache(prefix = "getSpuSaleAttr:")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId){
        return itemService.getSpuSaleAttr(skuId, spuId);
    }

    /**
     * 查询键值对
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSkuSaleAttrKeysAndValue/{spuId}")
    @Java1227Cache(prefix = "getSkuSaleAttrKeysAndValue:")
    public Map getSkuSaleAttrKeysAndValue(@PathVariable(value = "spuId") Long spuId){
        return itemService.getSkuSaleAttrKeysAndValue(spuId);
    }

    /**
     * 查询品牌的信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    @Java1227Cache(prefix = "getBaseTrademark:")
    public BaseTrademark getBaseTrademark(@PathVariable(value = "id") Long id){
        return itemService.getBaseTrademark(id);
    }

    /**
     * 查询指定sku的所有平台属性和值的列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    @Java1227Cache(prefix = "getBaseAttrInfo:")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId") Long skuId){
        return itemService.getBaseAttrInfo(skuId);
    }
}
