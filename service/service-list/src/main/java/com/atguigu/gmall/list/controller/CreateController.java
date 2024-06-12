package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsSerivce;
import com.atguigu.gmall.model.list.Goods;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * es创建索引创建映射创建数据相关的控制层
 */
@RestController
@RequestMapping(value = "/list/create")
public class CreateController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 创建索引,创建映射
     * @return
     */
    @GetMapping
    public Result createIndexAndMapping(){
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        //返回
        return Result.ok();
    }

    @Autowired
    private GoodsSerivce goodsSerivce;
    /**
     * 新增商品
     * @param skuId
     * @return
     */
    @GetMapping(value = "/add/{skuId}")
    public Result add(@PathVariable(value = "skuId") Long skuId){
        goodsSerivce.addGoodsFromDbToEs(skuId);
        return Result.ok();
    }

    /**
     * 删除商品
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/delete/{goodsId}")
    public Result delete(@PathVariable(value = "goodsId") Long goodsId){
        goodsSerivce.removeGoodsFromEs(goodsId);
        return Result.ok();
    }

    /**
     * 新增热度值
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/addHotScore/{goodsId}")
    public Result addHotScore(@PathVariable(value = "goodsId") Long goodsId){
        goodsSerivce.addHotScore(goodsId);
        return Result.ok();
    }
}
