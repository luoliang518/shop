package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 商品管理微服务提供的内部接口类的实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private SkuInfoMapper skuInfoMapper;
    /**
     * 根据sku的id查询sku的信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    /**
     * 从redis或数据查询数据
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoFromRedisOrDb(Long skuId) {
        //参数校验
        if(skuId == null){
            return null;
        }
        //从redis中获取sku的数据--> key=sku:1:info
        SkuInfo skuInfo =
                (SkuInfo)redisTemplate.opsForValue().get("sku:" + skuId + ":info");
        //redis中有值,返回
        if(skuInfo != null){
            return skuInfo;
        }
        //获取锁 锁的key=sku:1:lock
        RLock lock = redissonClient.getLock("sku:" + skuId + ":lock");
        //redis没有值,需要查询数据库,希望同一个商品每次只有一个用户查询数据库
        try {
            if(lock.tryLock(100, 100, TimeUnit.SECONDS)){
                try {
                    //从redis中获取sku的数据--> key=sku:1:info
                    skuInfo =
                            (SkuInfo)redisTemplate.opsForValue().get("sku:" + skuId + ":info");
                    //redis中有值,返回
                    if(skuInfo != null){
                        return skuInfo;
                    }
                    //加锁成功的可以查询数据库
                    skuInfo = skuInfoMapper.selectById(skuId);
                    if(skuInfo == null || skuInfo.getId() == null){
                        //redis没有数据,数据没有数据: 击穿或穿透
                        skuInfo = new SkuInfo();
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 300, TimeUnit.SECONDS);
                    }else{
                        //redis没有数据,数据库有数据,设置值1天过期
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 24*3600, TimeUnit.SECONDS);
                    }
                    return skuInfo;
                }catch (Exception e){
                    System.out.println("加锁成功, 但是逻辑处理出现异常");
                    e.printStackTrace();
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }
        }catch (Exception e){
            System.out.println("加锁失败, 出现了异常");
            e.printStackTrace();
        }
        return null;
    }

    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    /**
     * 根据三级分类查询一级二级三级分类的信息
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Resource
    private SkuImageMapper skuImageMapper;
    /**
     * 查询sku的图片列表
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImage(Long skuId) {
        return skuImageMapper.selectList(
                new LambdaQueryWrapper<SkuImage>()
                        .eq(SkuImage::getSkuId, skuId));
    }

    /**
     * 查询商品的价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        return skuInfoMapper.selectById(skuId).getPrice();
    }

    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    /**
     * 查询页面展示用的销售属性信息并且标识出当前sku的属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuIdAndSkuId(skuId, spuId);
    }

    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    /**
     * 查询skuid和销售属性值的键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuSaleAttrKeysAndValue(Long spuId) {
        //返回结果初始化
        Map result = new ConcurrentHashMap();
        //查询键值对
        List<Map> maps =
                skuSaleAttrValueMapper.selectSkuSaleAttrValueInfo(spuId);
        //遍历填装shuju
        maps.stream().forEach(m ->{
            //获取sku的id
            Object skuId = m.get("sku_id");
            //获取值的组合
            Object valuesId = m.get("values_id");
            //保存
            result.put(valuesId, skuId);
        });
        return result;
    }

    @Resource
    private BaseTradeMarkMapper baseTradeMarkMapper;
    /**
     * 查询品牌的详情
     *
     * @param id
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        return baseTradeMarkMapper.selectById(id);
    }

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 查询指定sku的所有平台属性和值的列表
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
    }
}
