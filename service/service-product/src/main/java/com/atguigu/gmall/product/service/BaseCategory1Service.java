package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 一级分类的接口类
 */
public interface BaseCategory1Service {

    /**
     * 主键查询
     * @param id
     * @return
     */
    public BaseCategory1 getBaseCategory1(Long id);

    /**
     * 查询所有的数据
     * @return
     */
    public List<BaseCategory1> getBaseCategory1List();

    /**
     * 新增
     * @param baseCategory1
     */
    public void add(BaseCategory1 baseCategory1);

    /**
     * 修改
     * @param baseCategory1
     */
    public void update(BaseCategory1 baseCategory1);

    /**
     * 删除
     * @param id
     */
    public void delete(Long id);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage page(Integer page, Integer size);

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    public List<BaseCategory1> search(BaseCategory1 baseCategory1);

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    public IPage search(Integer page,
                        Integer size,
                        BaseCategory1 baseCategory1);
}
