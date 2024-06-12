package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 台管理页面使用的接口类实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }


    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    /**
     * 根据一级分类查询二级分类的信息
     *
     * @param id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long id) {
        return baseCategory2Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory2>()
                        .eq(BaseCategory2::getCategory1Id, id));
    }

    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    /**
     * 根据二级分类查询三级分类的信息
     *
     * @param id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long id) {
        return baseCategory3Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory3>()
                        .eq(BaseCategory3::getCategory2Id, id));
    }


    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    /**
     * 新增平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void addAttrInfo(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if(baseAttrInfo == null){
            throw new RuntimeException("参数错误");
        }
        //获取id
        if(baseAttrInfo.getId() != null){
            //修改
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if(update < 0){
                throw new RuntimeException("修改平台属性失败!");
            }
            //删除旧的平台属性值
            int delete = baseAttrValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>()
                            .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            if(delete < 0){
                throw new RuntimeException("修改平台属性失败!");
            }
        }else{
            //新增平台属性名称对象
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if(insert <= 0){
                throw new RuntimeException("新增平台属性失败!");
            }
        }

        //新增成功以后,平台属性名称的id就有值了attrId
        Long attrId = baseAttrInfo.getId();
        //将attrId补充到平台属性的每一个值中去,完成每个值的新增
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //方案一: 串行化执行
//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            //补全值
//            baseAttrValue.setAttrId(attrId);
//            //新增
//            int insert1 = baseAttrValueMapper.insert(baseAttrValue);
//            if(insert1 <= 0){
//                throw new RuntimeException("新增平台属性失败!");
//            }
//        }
        attrValueList.stream().forEach(baseAttrValue ->{
            //补全值
            baseAttrValue.setAttrId(attrId);
            //新增
            int insert1 = baseAttrValueMapper.insert(baseAttrValue);
            if(insert1 <= 0){
                throw new RuntimeException("新增平台属性失败!");
            }
        });
    }

    /**
     * 根据一级二级三级分类查询平台属性的名字和平台属性的值的列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoByCategory(Long category1Id,
                                                        Long category2Id,
                                                        Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoByCategory(category1Id, category2Id, category3Id);
    }

    /**
     * 根据平台属性id查询这个平台属性的值列表
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getBaseAttrValue(Long attrId) {
        return baseAttrValueMapper.selectList(
                new LambdaQueryWrapper<BaseAttrValue>()
                        .eq(BaseAttrValue::getAttrId, attrId));
    }

    @Resource
    private BaseTradeMarkMapper baseTradeMarkMapper;
    /**
     * 查询所有品牌的数据
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getBaseTrademark() {
        return baseTradeMarkMapper.selectList(null);
    }

    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    /**
     * 获取基础销售属性的列表
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttr() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Resource
    private SpuInfoMapper spuInfoMapper;
    /**
     * 保存spu信息
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //参数校验
        if(spuInfo == null){
            return;
        }
        //判断spu是新增还是修改
        if(spuInfo.getId() != null){
            //修改
            int update = spuInfoMapper.updateById(spuInfo);
            if(update < 0){
                throw new RuntimeException("修改失败");
            }
            //删除图片表
            int delete = spuImageMapper.delete(
                    new LambdaQueryWrapper<SpuImage>()
                            .eq(SpuImage::getSpuId, spuInfo.getId()));
            //删除销售属性表
            int delete1 = spuSaleAttrMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttr>()
                            .eq(SpuSaleAttr::getSpuId, spuInfo.getId()));
            //删除销售属性值表
            int delete2 = spuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttrValue>()
                            .eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
            if(delete < 0 || delete1 < 0 || delete2 < 0){
                throw new RuntimeException("修改失败");
            }
        }else{
            //保存spu的信息
            int insert = spuInfoMapper.insert(spuInfo);
            if(insert <= 0){
                throw new RuntimeException("新增失败");
            }
        }
        //新增图片表
        saveSpuImage(spuInfo.getSpuImageList(), spuInfo.getId());
        //新增销售属性和值
        saveSpuSaleAttr(spuInfo.getSpuSaleAttrList(), spuInfo.getId());
    }

    /**
     * 分页条件查询spu的信息
     *  @param page
     * @param size
     * @param c3Id
     * @return
     */
    @Override
    public IPage<SpuInfo> pageSpuInfo(Integer page, Integer size, Long c3Id) {
        return spuInfoMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<SpuInfo>()
                        .eq(SpuInfo::getCategory3Id, c3Id));
    }

    /**
     * 查询spu的销售属性信息
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuId(spuId);
    }

    /**
     * 查询spu的图片
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImage(Long spuId) {
        return spuImageMapper.selectList(
                new LambdaQueryWrapper<SpuImage>()
                        .eq(SpuImage::getSpuId, spuId));
    }

    @Resource
    private SkuInfoMapper skuInfoMapper;
    /**
     * 新增或修改sku
     *
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //参数校验
        if(skuInfo == null){
            return;
        }
        //判断是新增还是修改
        if(skuInfo.getId() == null){
            //新增
            int insert = skuInfoMapper.insert(skuInfo);
            if(insert <= 0){
                throw new RuntimeException("新增sku失败");
            }
        }else{
            //修改
            int update = skuInfoMapper.updateById(skuInfo);
            if(update < 0){
                throw new RuntimeException("修改sku失败");
            }
            //删除图片表
            int delete = skuImageMapper.delete(
                    new LambdaQueryWrapper<SkuImage>()
                            .eq(SkuImage::getSkuId, skuInfo.getId()));
            //删除销售属性表
            int delete1 = skuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuSaleAttrValue>()
                            .eq(SkuSaleAttrValue::getSkuId, skuInfo.getId()));
            //删除平台属性表
            int delete2 = skuAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuAttrValue>()
                            .eq(SkuAttrValue::getSkuId, skuInfo.getId()));
            if(delete < 0 || delete1 < 0 || delete2 < 0){
                throw new RuntimeException("修改sku失败");
            }
        }
        //保存sku的图片信息
        saveSkuImage(skuInfo.getSkuImageList(), skuInfo.getId());
        //保存sku的销售属性信息
        saveSkuSaleAttrValue(skuInfo.getSkuSaleAttrValueList(), skuInfo.getId(), skuInfo.getSpuId());
        //保存sku的平台属性信息
        saveSkuAttrValue(skuInfo.getSkuAttrValueList(), skuInfo.getId());
    }

    /**
     * 分页查询sku的信息
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> list(Integer page, Integer size) {
        return skuInfoMapper.selectPage(new Page<>(page, size), null);
    }

    @Autowired
    private ListFeign listFeign;
    /**
     * 商品上下架
     *
     * @param skuId
     * @param status
     */
    @Override
    public void upOrDown(Long skuId, Short status) {
        //参数校验
        if(skuId == null){
            return;
        }
        //修改
        int i = skuInfoMapper.updateSaleInfo(skuId, status);
        if(i <= 0){
            return;
        }
        //需要同步es的数据-----同步调用-------待优化为异步调用--------------TODO-----消息队列MQ
        if(status.equals(ProductConst.SKU_ON_SALE)){
            //上架
            listFeign.add(skuId);
        }else{
            //下架
            listFeign.delete(skuId);
        }
    }

    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    /**
     * 保存sku的平台属性信息
     * @param skuAttrValueList
     * @param skuId
     */
    private void saveSkuAttrValue(List<SkuAttrValue> skuAttrValueList, Long skuId) {
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            //补全skuid
            skuAttrValue.setSkuId(skuId);
            //保存数据
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if(insert <= 0){
                throw new RuntimeException("新增sku的平台属性值失败");
            }
        });
    }

    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    /**
     * 保存sku的销售属性信息
     * @param skuSaleAttrValueList
     * @param skuId
     * @param spuId
     */
    private void saveSkuSaleAttrValue(List<SkuSaleAttrValue> skuSaleAttrValueList, Long skuId, Long spuId) {
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {
            //补全spu的id
            skuSaleAttrValue.setSpuId(spuId);
            //补全sku的id
            skuSaleAttrValue.setSkuId(skuId);
            //保存数据
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if(insert <= 0){
                throw new RuntimeException("新增sku的销售属性值失败");
            }
        });
    }

    @Resource
    private SkuImageMapper skuImageMapper;
    /**
     * 保存sku的图片的信息
     * @param skuImageList
     * @param skuId
     */
    private void saveSkuImage(List<SkuImage> skuImageList, Long skuId) {
        skuImageList.stream().forEach(skuImage -> {
            //补全sku的id
            skuImage.setSkuId(skuId);
            //保存数据
            int insert = skuImageMapper.insert(skuImage);
            if(insert <= 0){
                throw new RuntimeException("新增sku的图片失败");
            }
        });
    }

    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    /**
     *
     * @param spuSaleAttrList
     * @param spuId
     */
    private void saveSpuSaleAttr(List<SpuSaleAttr> spuSaleAttrList, Long spuId) {
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            //补全spu的id
            spuSaleAttr.setSpuId(spuId);
            //新增
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if(insert <= 0){
                throw new RuntimeException("保存spu的销售属性名称失败");
            }
            //保存这个销售属性的值的列表
            saveSpuSaleAttrValue(spuSaleAttr.getSpuSaleAttrValueList(), spuId, spuSaleAttr.getSaleAttrName());
        });
    }

    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    /**
     * 保存spu的销售属性值
     * @param spuSaleAttrValueList
     * @param spuId
     * @param saleAttrName
     */
    private void saveSpuSaleAttrValue(List<SpuSaleAttrValue> spuSaleAttrValueList, Long spuId, String saleAttrName) {
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            //补全spu的id
            spuSaleAttrValue.setSpuId(spuId);
            //补全销售属性的名称
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            //保存值的信息
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if(insert <= 0){
                throw new RuntimeException("保存spu的销售属性值失败");
            }
        });
    }


    @Resource
    private SpuImageMapper spuImageMapper;
    /**
     * 新增spu的图片信息
     * @param spuImageList
     * @param spuId
     */
    private void saveSpuImage(List<SpuImage> spuImageList, Long spuId) {
        spuImageList.stream().forEach(spuImage -> {
            //补全spuId
            spuImage.setSpuId(spuId);
            //保存图片
            int insert = spuImageMapper.insert(spuImage);
            if(insert <= 0){
                throw new RuntimeException("保存spu的图片失败");
            }
        });
    }
}
