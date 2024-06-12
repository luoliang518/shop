package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页使用的方法接口的接口类的实现类
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    /**
     * 查询首页的分类信息
     *
     * @return
     */
    @Override
    public List<JSONObject> getIndexCategory() {
        //查询整个视图的全部数据,得到所有的一级 二级 三级分类的信息
        List<BaseCategoryView> baseCategoryViewList1 =
                baseCategoryViewMapper.selectList(null);
        //基于以上的list数据,第一次分桶/分类,条件是一级分类的id,把所有一级分类id一样的数据分别进行存储
        Map<Long, List<BaseCategoryView>> category1Map =
                baseCategoryViewList1.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //对以及分类的map进行遍历
        return category1Map.entrySet().stream().map(category1Entry ->{
            //返回结果初始化
            JSONObject c1JsonObject = new JSONObject();
            //获取key: 一级分类的id
            Long category1Id = category1Entry.getKey();
            c1JsonObject.put("categoryId", category1Id);
            //获取value: 每个一级分类对应的全部的二级和三级分类的信息(二级分类重复)
            List<BaseCategoryView> baseCategoryViewList2 = category1Entry.getValue();
            //一级分类的名字
            String category1Name = baseCategoryViewList2.get(0).getCategory1Name();
            c1JsonObject.put("categoryName", category1Name);
            //根据二级分类的id再次分桶
            Map<Long, List<BaseCategoryView>> category2Map =
                    baseCategoryViewList2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //再次对这个二级分类的map进行遍历
            List<JSONObject> category2List = category2Map.entrySet().stream().map(category2Entry -> {
                //返回结果初始化
                JSONObject c2JsonObject = new JSONObject();
                //获取key: 二级分类的id
                Long category2Id = category2Entry.getKey();
                c2JsonObject.put("categoryId", category2Id);
                //获取value: 每个二级分类对应的三级分类的列表,这个三级分类不重复
                List<BaseCategoryView> baseCategoryViewList3 = category2Entry.getValue();
                //获取二级分类的名字
                String category2Name = baseCategoryViewList3.get(0).getCategory2Name();
                c2JsonObject.put("categoryName", category2Name);
                //遍历这个baseCategoryViewList3,获取每个三级分类的id和name
                List<JSONObject> category3List = baseCategoryViewList3.stream().map(baseCategoryView3 -> {
                    //返回结果初始化
                    JSONObject c3jsonObject = new JSONObject();
                    //获取三级分类的id
                    Long category3Id = baseCategoryView3.getCategory3Id();
                    c3jsonObject.put("categoryId", category3Id);
                    //获取三级分类的名字
                    String category3Name = baseCategoryView3.getCategory3Name();
                    c3jsonObject.put("categoryName", category3Name);
                    //保存返回
                    return c3jsonObject;
                }).collect(Collectors.toList());
                //保存每个二级分类对应的三级分类的关系
                c2JsonObject.put("childCategory", category3List);
                //返回这个二级分类的信息
                return c2JsonObject;
            }).collect(Collectors.toList());
            //保存这个一级分类以及对应所有二级分类的关联关系
            c1JsonObject.put("childCategory", category2List);
            //返回
            return c1JsonObject;
        }).collect(Collectors.toList());
    }
}
