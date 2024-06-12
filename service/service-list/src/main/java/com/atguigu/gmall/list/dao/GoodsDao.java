package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * es使用的商品的dao层
 */
@Repository
public interface GoodsDao extends ElasticsearchRepository<Goods, Long> {
}
