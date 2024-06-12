package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 一级分类的接口类的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseCategory1ServiceImpl implements BaseCategory1Service {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;

    /**
     * 主键查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseCategory1 getBaseCategory1(Long id) {
        return baseCategory1Mapper.selectById(id);
    }

    /**
     * 查询所有的数据
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getBaseCategory1List() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 新增
     *
     * @param baseCategory1
     */
    @Override
    public void add(BaseCategory1 baseCategory1) {
        //参数校验
        if(baseCategory1 == null ||
                StringUtils.isEmpty(baseCategory1.getName())){
            return;
        }
        //新增
        int insert = baseCategory1Mapper.insert(baseCategory1);
        if(insert <= 0){
            return;
        }
    }

    /**
     * 修改
     *
     * @param baseCategory1
     */
    @Override
    public void update(BaseCategory1 baseCategory1) {
        //参数校验
        if(baseCategory1 == null ||
                baseCategory1.getId() == null ||
                StringUtils.isEmpty(baseCategory1.getName())){
            return;
        }
        //修改
        int update = baseCategory1Mapper.updateById(baseCategory1);
        if(update < 0){
            return;
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        if(id != null){
            int i = baseCategory1Mapper.deleteById(id);
            if(i < 0){
                return;
            }
        }
    }

    /**
     * 分页查询
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage page(Integer page, Integer size) {
        return baseCategory1Mapper.selectPage(new Page(page, size), null);
    }

    /**
     * 条件查询
     *
     * @param baseCategory1
     * @return
     */
    @Override
    public List<BaseCategory1> search(BaseCategory1 baseCategory1) {
        //参数校验
        if(baseCategory1 == null){
            return baseCategory1Mapper.selectList(null);
        }
        //构建查询条件
        LambdaQueryWrapper<BaseCategory1> wrapper = buildQueryParam(baseCategory1);
        //执行查询返回结果
        return baseCategory1Mapper.selectList(wrapper);
    }

    /**
     * 分页条件查询
     *
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    @Override
    public IPage search(Integer page,
                        Integer size,
                        BaseCategory1 baseCategory1) {
        //构建查询条件
        LambdaQueryWrapper<BaseCategory1> wrapper = buildQueryParam(baseCategory1);
        //查询返回结果
        return baseCategory1Mapper.selectPage(new Page(page, size), wrapper);
    }

    /**
     * 构建查询条件
     * @param baseCategory1
     * @return
     */
    private LambdaQueryWrapper<BaseCategory1> buildQueryParam(BaseCategory1 baseCategory1) {
        //声明条件构造器
        LambdaQueryWrapper<BaseCategory1> wrapper = new LambdaQueryWrapper<>();
        //判断id是否为空
        if(baseCategory1.getId() != null){
            wrapper.eq(BaseCategory1::getId, baseCategory1.getId());
        }
        //判断name是否为空
        if(!StringUtils.isEmpty(baseCategory1.getName())){
            wrapper.like(BaseCategory1::getName, baseCategory1.getName());
        }
        return wrapper;
    }
}
