package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.list.feign.SearchFeign;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 搜索页面的前端控制器
 */
@Controller
@RequestMapping(value = "/page/search")
public class SearchController {

    @Autowired
    private SearchFeign searchFeign;

    @Value("${item.url}")
    private String itemUrl;

    /**
     * 打开搜索页面的方法
     * @return
     */
    @GetMapping
    public String search(@RequestParam Map<String, String> searchData,
                         Model model){
        //远程调用搜索微服务获取商品搜索的结果
        Map<String, Object> result = searchFeign.search(searchData);
        //需要将这个搜索的结果存储到model中去
        model.addAllAttributes(result);
        //将查询的条件也存储到model中去,用于数据的回显
        model.addAttribute("searchData", searchData);
        //获取当前的url
        model.addAttribute("url", getUrl(searchData));
        //获取排序的url
        model.addAttribute("surl", getSortUrl(searchData));
        //分页初始化
        Page page =
                new Page<>(
                        Long.valueOf(result.get("totalHits").toString()), //总数量
                        getPage(searchData.get("pageNum")), //页码
                        200);//每页的size
        model.addAttribute("page", page);
        //存储商品详情的前缀域名
        model.addAttribute("itemUrl", itemUrl);
        //打开搜索页面
        return "list";
    }

    /**
     * 拼接当前的url
     * @param searchData
     * @return
     */
    private String getUrl(Map<String, String> searchData){
        //url初始化
        String url = "/page/search?";
        //遍历拼接
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取参数的名字
            String key = entry.getKey();
            //获取参数的值
            String value = entry.getValue();
            if(key.equals("pageNum")){
                continue;
            }
            //拼接
            url = url + key + "=" + value + "&";
        }
        //返回当前的url
        return url.substring(0, url.length() - 1);
    }

    /**
     * 获取排序的url
     * @param searchData
     * @return
     */
    private String getSortUrl(Map<String, String> searchData){
        //url初始化
        String url = "/page/search?";
        //遍历拼接
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取参数的名字
            String key = entry.getKey();
            //获取参数的值
            String value = entry.getValue();
            if(key.equals("sortField") || key.equals("sortRule") || key.equals("pageNum")){
                continue;
            }
            //拼接
            url = url + key + "=" + value + "&";
        }
        //返回当前的url
        return url.substring(0, url.length() - 1);
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
}
