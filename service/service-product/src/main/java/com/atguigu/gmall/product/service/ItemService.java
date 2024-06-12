package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品管理微服务提供的内部接口类
 */
public interface ItemService {

    /**
     * 根据sku的id查询sku的信息
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfo(Long skuId);

    /**
     * 从redis或数据查询数据
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfoFromRedisOrDb(Long skuId);

    /**
     * 根据三级分类查询一级二级三级分类的信息
     * @param category3Id
     * @return
     */
    public BaseCategoryView getCategory(Long category3Id);

    /**
     * 查询sku的图片列表
     * @param skuId
     * @return
     */
    public List<SkuImage> getSkuImage(Long skuId);

    /**
     * 查询商品的价格
     * @param skuId
     * @return
     */
    public BigDecimal getPrice(Long skuId);

    /**
     * 查询页面展示用的销售属性信息并且标识出当前sku的属性
     * @param skuId
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId);

    /**
     * 查询skuid和销售属性值的键值对
     * @param spuId
     * @return
     */
    public Map getSkuSaleAttrKeysAndValue(Long spuId);

    /**
     * 查询品牌的详情
     * @param id
     * @return
     */
    public BaseTrademark getBaseTrademark(Long id);

    /**
     * 查询指定sku的所有平台属性和值的列表
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId);

}
