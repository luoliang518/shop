package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.util.FastDfsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理的控制层
 */
@RestController
@RequestMapping(value = "/admin/product")
public class FileController {

    @Value("${fileServer.url}")
    private String url;


    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping(value = "/fileUpload")
    public Result fileUpload(@RequestParam MultipartFile file) throws Exception{
        //返回文件的地址: 组名 + 全量路径文件名, 结束
        return Result.ok(url + FastDfsUtil.fileUpload(file));
    }

}
