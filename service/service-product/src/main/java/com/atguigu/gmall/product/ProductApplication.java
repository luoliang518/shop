package com.atguigu.gmall.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 商品管理微服务的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableFeignClients(basePackages = "com.atguigu.gmall.list.feign")
public class ProductApplication {

    /**
     *
     * @param args: jvm的参数 java -jar 123.jar -Xxm 512m
     */
    public static void main(String[] args) {
        /**
         * 1.创建了一个IOC容器对象
         * 2.加载启动类的字节码文件
         * 3.SpringBootConfiguration: 标识启动类为一个配置类
         * 4.ComponentScan: 扫描启动类所在的包和启动类所在包子包中所有类的注解
         * 5.EnableAutoConfiguration: 依据spring官方统计号的所有可能使用到的bean对象,逐个进行实例化,实例化成功的注入到容器中去
         */
        ApplicationContext applicationContext =
                SpringApplication.run(ProductApplication.class, args);
    }

    /**
     * RestTemplate初始化
     * @return\
     * spring.xml-----><bean name="restTemplate" class="org.springframework.web.client.RestTemplate"></bean>
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
