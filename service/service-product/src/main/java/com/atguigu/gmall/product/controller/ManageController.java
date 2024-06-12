package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理页面使用的控制层
 */
@RestController
@RequestMapping(value = "/admin/product")
public class ManageController {

    @Autowired
    private ManageService manageService;

    /**
     * 查询所有的一级分类
     * @return
     */
    @GetMapping(value = "/getCategory1")
    public Result getCategory1(){
        return Result.ok(manageService.getCategory1());
    }

    /**
     * 根据一级分类查询二级分类的信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable(value = "category1Id") Long id){
        return Result.ok(manageService.getCategory2(id));
    }

    /**
     * 根据二级分类查询三级分类的信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable(value = "category2Id") Long id){
        return Result.ok(manageService.getCategory3(id));
    }

    /**
     * 保存平台属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.addAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据一级二级三级分类查询平台属性的名字和平台属性的值的列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") Long category1Id,
                               @PathVariable("category2Id") Long category2Id,
                               @PathVariable("category3Id") Long category3Id){
        return Result.ok(manageService.getBaseAttrInfoByCategory(category1Id, category2Id, category3Id));
    }

    /**
     * 根据平台属性id查询这个平台属性的值列表
     * @param attrId
     * @return
     */
    @GetMapping(value = "/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable(value = "attrId") Long attrId){
        return Result.ok(manageService.getBaseAttrValue(attrId));
    }

    /**
     * 查询所有的品牌列表
     * @return
     */
    @GetMapping(value = "/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        return Result.ok(manageService.getBaseTrademark());
    }

    /**
     * 查询销售属性列表
     * @return
     */
    @GetMapping(value = "/baseSaleAttrList")
    public Result baseSaleAttrList(){
        return Result.ok(manageService.getBaseSaleAttr());
    }

    /**
     * 保存和修改spu
     * @param spuInfo
     * @return
     */
    @PostMapping(value = "/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 分页条件查询spu的信息
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/{page}/{size}")
    public Result pageSpuInfo(@PathVariable(value = "page") Integer page,
                              @PathVariable(value = "size") Integer size,
                              Long category3Id){
        return Result.ok(manageService.pageSpuInfo(page, size, category3Id));
    }

    /**
     * 查询spu的销售属性信息
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable(value = "spuId") Long spuId){
        return Result.ok(manageService.spuSaleAttrList(spuId));
    }

    /**
     * 查询spu的图片列表信息
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable(value = "spuId") Long spuId){
        return Result.ok(manageService.getSpuImage(spuId));
    }

    /**
     * 保存sku的信息
     * @param skuInfo
     * @return
     */
    @PostMapping(value = "/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 分页查询sku的信息
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/list/{page}/{size}")
    public Result list(@PathVariable(value = "page") Integer page,
                       @PathVariable(value = "size") Integer size){
        return Result.ok(manageService.list(page, size));
    }

    /**
     * 上架
     * @param skuId
     * @return
     */
    @GetMapping(value = "/onSale/{skuId}")
    public Result onSale(@PathVariable(value = "skuId") Long skuId){
        manageService.upOrDown(skuId, ProductConst.SKU_ON_SALE);
        return Result.ok();
    }

    /**
     * 下架
     * @param skuId
     * @return
     */
    @GetMapping(value = "/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable(value = "skuId") Long skuId){
        manageService.upOrDown(skuId, ProductConst.SKU_CANCEL_SALE);
        return Result.ok();
    }


}
