package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品搜索的接口类的实现类
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchData) {
        try{
            //构建查询条件
            SearchRequest searchRequest = buildQueryParam(searchData);
            //执行查询
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果
            return getSearchResult(searchResponse);
        }catch (Exception e){
            e.printStackTrace();
        }
        //返回结果
        return null;
    }

    /**
     * 构建查询的条件
     * @param searchData
     * @return
     */
    private SearchRequest buildQueryParam(Map<String, String> searchData) {
        //构建查询条件
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        //声明条件构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建组合查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //关键字查询
        String keywords = searchData.get("keywords");
        if(!StringUtils.isEmpty(keywords)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", keywords));
        }
        //品牌查询条件:  1:华为
        String tradeMark = searchData.get("tradeMark");
        if(!StringUtils.isEmpty(tradeMark)){
            String[] split = tradeMark.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //遍历所有的查询参数
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //查询的参数
            String key = entry.getKey();
            //判断是否以attr_开头
            if(key.startsWith("attr_")){
                //23:红色
                String value = entry.getValue();
                //切分
                String[] split = value.split(":");
                //nested类型的组合查询
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                //必须平台属性的id等于用户传递来的值
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                //必须平台属性的值,等于用户传递来的值
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                //设置本次平台属性的查询条件
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None));
            }
        }
        //获取价格查询的条件: 500-1000元  3000元以上
        String price = searchData.get("price");
        if(!StringUtils.isEmpty(price)){
            //500<= <1000  3000
            price = price.replace("元", "").replace("以上", "");
            //切分
            String[] split = price.split("-");
            //价格大于等于起始值
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            if(split.length > 1){
                //价格小于第二个值
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //设置bool全部的查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        //设置排序
        String sortRule = searchData.get("sortRule");
        String sortField = searchData.get("sortField");
        if(!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            //指定排序
            searchSourceBuilder.sort(sortField, SortOrder.valueOf(sortRule));
        }else{
            //默认排序: 新品排序
            searchSourceBuilder.sort("id", SortOrder.DESC);
        }
        /**
         * 1--->0-199
         * 2--->200-399
         * 3--->400-599
         */
        //设置分页
        int pageNum = getPage(searchData.get("pageNum"));
        searchSourceBuilder.from((pageNum - 1)* 200);
        //每页显示的数量
        searchSourceBuilder.size(200);
        //设置高亮的条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //设置聚合查询的条件(品牌):select tm_id as tmId from sku_info where sku_name like '%手机%' GROUP BY tm_id
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                    .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                    .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                .size(100)
        );
        //设置聚合的条件(平台属性)
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("aggAttrs", "attrs")//先对attrs这个nested类型聚合
                    .subAggregation(
                            AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                            .size(100)
                    )
        );
        //设置条件
        searchRequest.source(searchSourceBuilder);
        //返回查询的条件构造器
        return searchRequest;
    }

    /**
     * 转换页码
     * @param pageNum
     */
    private int getPage(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            return 1;
        }
    }

    /**
     * 解析搜索的结果
     * @param searchResponse
     * @return
     */
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        //最终返回结果初始化
        Map<String, Object> result = new HashMap<>();
        //获取命中的数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //商品列表初始化
        List<Goods> goodsList = new ArrayList<>();
        //遍历解析数据
        while (iterator.hasNext()){
            //获取每条数据对象
            SearchHit next = iterator.next();
            //获取字符串类型的原始数据
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods goods =
                    JSONObject.parseObject(sourceAsString, Goods.class);
            //获取高亮的数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if(highlightField != null){
                //获取所有的高亮数据
                Text[] fragments = highlightField.getFragments();
                if(fragments != null && fragments.length > 0){
                    String title = "";
                    //遍历解析
                    for (Text fragment : fragments) {
                        title += fragment;
                    }
                    //替换
                    goods.setTitle(title);
                }
            }
            //保存一个
            goodsList.add(goods);
        }
        //存储商品的数据
        result.put("goodsList", goodsList);
        //解析聚合查询的数据:获取所有的聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        //解析品牌的聚合结果
        List<SearchResponseTmVo> searchResponseTmVoList =
                getBaseTradeMarkAggResult(aggregations);
        result.put("searchResponseTmVoList", searchResponseTmVoList);
        //解析平台属性的聚合结果
        List<SearchResponseAttrVo> searchResponseAttrVoList =
                getAttrValueAggResult(aggregations);
        result.put("searchResponseAttrVoList", searchResponseAttrVoList);
        //获取总命中的数据量
        long totalHits = hits.getTotalHits();
        result.put("totalHits", totalHits);
        //返回
        return result;
    }

    /**
     * 解析平台属性的聚合结果
     * @param aggregations
     * @return
     */
    private List<SearchResponseAttrVo> getAttrValueAggResult(Aggregations aggregations) {
        //获取nested类型的聚合结果
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        //基于以上的聚合结果获取子聚合的结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        //遍历每个平台属性id的聚合结果,再去获取子聚合的结果
        return aggAttrId.getBuckets().stream().map(buck ->{
            //返回结果初始化
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取平台属性的id
            long attrId = ((Terms.Bucket) buck).getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取平台属性名字的聚合结果
            ParsedStringTerms aggAttrName =
                    ((Terms.Bucket) buck).getAggregations().get("aggAttrName");
            if(aggAttrName.getBuckets() != null && !aggAttrName.getBuckets().isEmpty()){
                //获取平台属性的名字
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取平台属性的值的列表
            ParsedStringTerms aggAttrValue = ((Terms.Bucket) buck).getAggregations().get("aggAttrValue");
            if(aggAttrValue.getBuckets() != null && !aggAttrValue.getBuckets().isEmpty()){
                //遍历这个平台属性的所有的值
                List<String> attrValueList =
                        aggAttrValue.getBuckets().stream().map(valueBuck -> {
                            //获取每个值
                            return valueBuck.getKeyAsString();
                        }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(attrValueList);
            }
            //返回
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取品牌的聚合结果
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getBaseTradeMarkAggResult(Aggregations aggregations) {
        //通过别名获取品牌id的聚合结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //遍历获取每个品牌的聚合结果
        return aggTmId.getBuckets().stream().map(buck ->{
            //返回结果初始化
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌的id
            long tmId = ((Terms.Bucket) buck).getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取品牌名字的聚合结果
            ParsedStringTerms aggTmName =
                    ((Terms.Bucket) buck).getAggregations().get("aggTmName");
            if(aggTmName.getBuckets() != null && !aggTmName.getBuckets().isEmpty()){
                //获取品牌的名字
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //获取品牌logourl的聚合结果
            ParsedStringTerms aggTmLogoUrl =
                    ((Terms.Bucket) buck).getAggregations().get("aggTmLogoUrl");
            if(aggTmLogoUrl.getBuckets() != null && !aggTmLogoUrl.getBuckets().isEmpty()){
                //品牌的logo地址
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            //返回
            return searchResponseTmVo;
        }).collect(Collectors.toList());
    }
}
