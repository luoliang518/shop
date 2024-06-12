package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.service.GoodsSerivce;
import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * es的商品相关的接口的实现类
 */
@Service
public class GoodsSerivceImpl implements GoodsSerivce {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ProductFeign productFeign;
    /**
     * 将商品的数据写入es
     *
     * @param skuId
     */
    @Override
    public void addGoodsFromDbToEs(Long skuId) {
        //参数校验
        if(skuId == null){
            return;
        }
        //初始化goods对象
        Goods goods = new Goods();
        //远程调用product微服务获取商品的信息
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if(skuInfo == null || skuInfo.getId() == null){
            return;
        }
        //补全goods的数据
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        //远程查询价格
        BigDecimal price = productFeign.getPrice(skuId);
        goods.setPrice(price.doubleValue());
        goods.setCreateTime(new Date());
        //品牌设置
        BaseTrademark baseTrademark =
                productFeign.getBaseTrademark(skuInfo.getTmId());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //分类
        Long category3Id = skuInfo.getCategory3Id();
        BaseCategoryView category =
                productFeign.getCategory(category3Id);
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());
        //平台属性
        List<BaseAttrInfo> baseAttrInfoList =
                productFeign.getBaseAttrInfo(skuId);
        List<SearchAttr> attrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            //包装goods对象需要的类型
            SearchAttr searchAttr = new SearchAttr();
            //设置平台属性id
            searchAttr.setAttrId(baseAttrInfo.getId());
            //设置平台属性名称
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            //设置平台属性值
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //返回
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(attrs);
        //保存商品到es
        goodsDao.save(goods);
    }

    /**
     * 删除es中商品的数据
     *
     * @param goodsId
     */
    @Override
    public void removeGoodsFromEs(Long goodsId) {
        goodsDao.deleteById(goodsId);
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 为商品增加热度值
     *
     * @param goodsId
     */
    @Override
    public void addHotScore(Long goodsId) {
        //参数校验
        if(goodsId == null){
            return;
        }
        //在redis中执行++操作: 初始值不设置的话就是0
        Double score =
                redisTemplate.opsForZSet().incrementScore("Goods_Hot_Score", goodsId, 1);
        //更新到es中去
        if(score.intValue() % 10 == 0){
            //每10分同步一次
            Optional<Goods> optional = goodsDao.findById(goodsId);
            if(optional.isPresent()){
                Goods goods = optional.get();
                goods.setHotScore(score.longValue());
                //更新
                goodsDao.save(goods);
            }
        }
    }
}
