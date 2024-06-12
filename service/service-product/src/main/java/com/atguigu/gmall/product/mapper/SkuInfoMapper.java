package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * sku表的mapper映射
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 修改上下架的状态
     * @param skuId
     * @param status
     * @return
     */
    @Update("update sku_info set is_sale = #{status} where id = #{skuId}")
    public int updateSaleInfo(@Param("skuId") Long skuId,
                              @Param("status")  Short status);
}
