package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 后台管理页面使用的接口类
 */
public interface ManageService {

    /**
     * 查询所有的一级分类
     * @return
     */
    public List<BaseCategory1> getCategory1();

    /**
     * 根据一级分类查询二级分类的信息
     * @param id
     * @return
     */
    public List<BaseCategory2> getCategory2(Long id);

    /**
     * 根据二级分类查询三级分类的信息
     * @param id
     * @return
     */
    public List<BaseCategory3> getCategory3(Long id);

    /**
     * 新增平台属性
     * @param baseAttrInfo
     */
    public void addAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据一级二级三级分类查询平台属性的名字和平台属性的值的列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfoByCategory(Long category1Id,
                                                        Long category2Id,
                                                        Long category3Id);

    /**
     * 根据平台属性id查询这个平台属性的值列表
     * @param attrId
     * @return
     */
    public List<BaseAttrValue> getBaseAttrValue(Long attrId);

    /**
     * 查询所有品牌的数据
     * @return
     */
    public List<BaseTrademark> getBaseTrademark();

    /**
     * 获取基础销售属性的列表
     * @return
     */
    public List<BaseSaleAttr> getBaseSaleAttr();

    /**
     * 保存spu信息
     * @param spuInfo
     */
    public void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 分页条件查询spu的信息
     * @param page
     * @param size
     * @param c3Id
     * @return
     */
    public IPage<SpuInfo> pageSpuInfo(Integer page, Integer size, Long c3Id);

    /**
     * 查询spu的销售属性信息
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    /**
     * 查询spu的图片
     * @param spuId
     * @return
     */
    public List<SpuImage> getSpuImage(Long spuId);

    /**
     * 新增或修改sku
     * @param skuInfo
     */
    public void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询sku的信息
     * @param page
     * @param size
     * @return
     */
    public IPage<SkuInfo> list(Integer page, Integer size);

    /**
     * 商品上下架
     * @param skuId
     * @param status
     */
    public void upOrDown(Long skuId, Short status);
}
