package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品管理微服务提供的feign接口
 */
@FeignClient(name = "service-product", path = "/api/item")
public interface ProductFeign {

    /**
     * 查询sku的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询分类的信息
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id);

    /**
     * 根据sku的id 查询sku的图片列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuImage/{skuId}")
    public List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询商品的价格
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询页面展示用的销售属性信息并且标识出当前sku的属性
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSpuSaleAttr/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId);
    /**
     * 查询键值对
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSkuSaleAttrKeysAndValue/{spuId}")
    public Map getSkuSaleAttrKeysAndValue(@PathVariable(value = "spuId") Long spuId);

    /**
     * 查询品牌的信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable(value = "id") Long id);

    /**
     * 查询指定sku的所有平台属性和值的列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId") Long skuId);
}
