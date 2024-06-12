package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.Java1227Cache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页使用的接口的控制层
 */
@RestController
@RequestMapping(value = "/admin/prodct")
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 获取首页需要的分类的信息
     * @return
     */
    @GetMapping(value = "/getIndexCategory")
    @Java1227Cache(prefix = "getIndexCategory:")
    public Result getIndexCategory(){
        return Result.ok(indexService.getIndexCategory());
    }
}
