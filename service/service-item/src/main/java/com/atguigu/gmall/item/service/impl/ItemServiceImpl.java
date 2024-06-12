package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 商品详情页使用的接口类的实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeign productFeign;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 查询商品的详细的信息
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItemInfo(Long skuId) {
        //返回结果初始化
        Map<String, Object> result = new ConcurrentHashMap<>();
        //参数校验
        if(skuId == null){
            return result;
        }
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
            //查询skuinfo的信息
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
            //判断商品是否存在,若不存在直接返回空
            if (skuInfo == null || skuInfo.getId() == null) {
                return null;
            }
            result.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolExecutor);
        //查询分类的信息
        CompletableFuture<Void> future2 = future1.thenAcceptAsync((skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView category = productFeign.getCategory(category3Id);
            result.put("category", category);
        }), threadPoolExecutor);
        //查询图片的列表
        CompletableFuture<Void> future3 = future1.thenAcceptAsync((skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            List<SkuImage> skuImageList = productFeign.getSkuImage(skuId);
            result.put("skuImageList", skuImageList);
        }), threadPoolExecutor);
        //查询商品的价格
        CompletableFuture<Void> future4 = future1.thenAcceptAsync((skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            BigDecimal price = productFeign.getPrice(skuId);
            result.put("price", price);
        }), threadPoolExecutor);
        //查询商品所属的spu的全部销售属性名称和值的列表,以及标识出当前的sku的销售属性值是哪几个
        CompletableFuture<Void> future5 = future1.thenAcceptAsync((skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            List<SpuSaleAttr> spuSaleAttrList =
                    productFeign.getSpuSaleAttr(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrList);
        }), threadPoolExecutor);
        //查询当前商品所属的spu下全部的sku的id和sku的销售属性值列表的键值对
        CompletableFuture<Void> future6 = future1.thenAcceptAsync((skuInfo -> {
            if (skuInfo == null) {
                return;
            }
            Map skuSaleAttrKeysAndValue = productFeign.getSkuSaleAttrKeysAndValue(skuInfo.getSpuId());
            result.put("skuSaleAttrKeysAndValue", skuSaleAttrKeysAndValue);
        }), threadPoolExecutor);
        //等所有的任务执行完成才能返回
        CompletableFuture.allOf(future1, future2, future3, future4, future5, future6).join();
        //返回结果
        return result;
    }
}
