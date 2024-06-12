package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池的配置类
 */
@Configuration
public class GmallDefault {

    /**
     * 自定义的线程池对象
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(
                50,//核心线程数
                500,//最大雄安成熟
                10,//空闲时间
                TimeUnit.SECONDS,//时间单位
                new ArrayBlockingQueue<>(10000));//阻塞队列
    }
    /**
     * 1.在线程池启动的时候,线程池中拥有 核心线程数:0  非核心线程数:0  阻塞队列的元素:0
     * 2.这时候来了一个任务,需要从线程池中获取一个线程执行任务,线程池会判断核心线程数是否达到上限,没有则创建核心线程
     *      核心线程数:1  非核心线程数:0  阻塞队列的元素:0
     * 3.同时又来了49个任务,创建核心线程执行任务
     *      核心线程数:50  非核心线程数:0  阻塞队列的元素:0
     * 4.又来了10000个任务,核心线程数已经达到了上限,所有的任务进入阻塞队列
     *      核心线程数:50  非核心线程数:0  阻塞队列的元素:10000
     * 5.又来了450个任务,核心线程满了,阻塞队列满了,创建非核心线程,执行任务
     *      核心线程数:50  非核心线程数:450  阻塞队列的元素:10000
     * 6.再来了一个任务,核心线程满了,阻塞队列满了线程达到最大上限,触发拒绝策略
     *     拒绝策略:
     *     1.不执行任务抛异常
     *     2.谁调用谁执行
     *     3.不执行任务什么都不做
     *     4.从阻塞队列中出列队列头的任务,直接丢掉,将新的任务入列/执行
     */
}
