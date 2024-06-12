package com.atguigu.gmall.list.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 搜索微服务商品同步相关的feign接口
 */
@FeignClient(name = "service-list", path = "/list/create", contextId = "listFeign")
public interface ListFeign {

    /**
     * 新增商品
     * @param skuId
     * @return
     */
    @GetMapping(value = "/add/{skuId}")
    public Result add(@PathVariable(value = "skuId") Long skuId);

    /**
     * 删除商品
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/delete/{goodsId}")
    public Result delete(@PathVariable(value = "goodsId") Long goodsId);
}
