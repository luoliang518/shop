package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 一级分类的控制层
 */
@RestController
@RequestMapping(value = "/api/baseCategory1")
public class BaseCategory1Controller {

    @Autowired
    private BaseCategory1Service baseCategory1Service;

    /**
     * 主键查询
     * @param id
     * @return
     * localhost:8206/api/baseCategory1/getBaseCategory1?id=1
     * localhost:8206/api/baseCategory1/getBaseCategory1/1
     */
    @GetMapping(value = "/getBaseCategory1/{id}")
    public Result getBaseCategory1(@PathVariable(value = "id") Long id){
        return Result.ok(baseCategory1Service.getBaseCategory1(id));
    }

    /**
     * 查询全部
     * @return
     */
    @GetMapping(value = "/getBaseCategory1List")
    public Result getBaseCategory1List(){
        return Result.ok(baseCategory1Service.getBaseCategory1List());
    }

    /**
     * 新增
     * @param baseCategory1
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.add(baseCategory1);
        return Result.ok();
    }

    /**
     * 修改
     * @param baseCategory1
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.update(baseCategory1);
        return Result.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable(value = "id") Long id){
        baseCategory1Service.delete(id);
        return Result.ok();
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/page/{page}/{size}")
    public Result page(@PathVariable(value = "page") Integer page,
                       @PathVariable(value = "size") Integer size){
        return Result.ok(baseCategory1Service.page(page, size));
    }

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping(value = "/search")
    public Result search(@RequestBody BaseCategory1 baseCategory1){
        return Result.ok(baseCategory1Service.search(baseCategory1));
    }

    /**
     * 分页条件查询
     * @param baseCategory1
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result search(@RequestBody BaseCategory1 baseCategory1,
                         @PathVariable(value = "page") Integer page,
                         @PathVariable(value = "size") Integer size){
        return Result.ok(baseCategory1Service.search(page, size, baseCategory1));
    }

}
