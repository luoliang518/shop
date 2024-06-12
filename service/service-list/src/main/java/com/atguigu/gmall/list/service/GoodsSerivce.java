package com.atguigu.gmall.list.service;

/**
 * es的商品相关的接口
 */
public interface GoodsSerivce {

    /**
     * 将商品的数据写入es
     * @param skuId
     */
    public void addGoodsFromDbToEs(Long skuId);

    /**
     * 删除es中商品的数据
     * @param goodsId
     */
    public void removeGoodsFromEs(Long goodsId);

    /**
     * 为商品增加热度值
     * @param goodsId
     */
    public void addHotScore(Long goodsId);
}
