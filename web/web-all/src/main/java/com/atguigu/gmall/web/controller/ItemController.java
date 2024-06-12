package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.item.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 商品详情页面的控制层
 */
@Controller
@RequestMapping(value = "/page/item")
public class ItemController {

    @Autowired
    private ItemFeign itemFeign;

    /**
     * 打开商品详情页
     * @param model
     * @return
     */
    @GetMapping("/{skuId}")
    public String item(Model model,
                       @PathVariable(value = "skuId") Long skuId){
        //远程调用商品详情微服务,获取商品详情页需要的数据
        Map<String, Object> itemInfo = itemFeign.getItemInfo(skuId);
        //将数据存储到model
        model.addAllAttributes(itemInfo);
        //打开页面
        return "itemThymeleaf";
    }


    @Autowired
    private TemplateEngine templateEngine;
    /**
     * 为指定的商品生成静态页面
     * @param skuId
     * @return
     */
    @GetMapping(value = "/createItemHtml/{skuId}")
    @ResponseBody
    public String createItemHtml(@PathVariable(value = "skuId") Long skuId) throws Exception{
        //声明文件对象
        File file = new File("D:\\", skuId + ".html");
        //声明输出流对象
        PrintWriter printWriter = new PrintWriter(file, "UTF-8");
        //构建存储数据的容器,相当于model
        Context context = new Context();
        //远程调用商品详情微服务,获取商品详情页需要的数据
        Map<String, Object> itemInfo = itemFeign.getItemInfo(skuId);
        context.setVariables(itemInfo);
        //创建静态页面
        /**
         * 1.模板是哪个
         * 2.存储数据的容器
         * 3.将页面写到哪里去
         */
        templateEngine.process("itemStatic", context, printWriter);
        //关闭资源
        printWriter.close();

        return "success";
    }
}
