package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 切面类
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 增强方法
     * @param point: 切点
     * @return
     */
    @Around("@annotation(com.atguigu.gmall.common.cache.Java1227Cache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //初始化返回结果
        Object result = null;
        try {
            //获取方法的参数
            Object[] args = point.getArgs();
            //获取方法的签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获取这个方法的注解
            Java1227Cache java1227Cache =
                    signature.getMethod().getAnnotation(Java1227Cache.class);
            // 前缀-->prefix = getSkuInfo:
            String prefix = java1227Cache.prefix();
            //拼接数据的key=getSkuInfo:[1]
            String key = prefix + Arrays.asList(args).toString();
            //从redis中获取数据
            result = cacheHit(signature, key);
            //判断redis中是否有值
            if (result != null){
                // 缓存有数据
                return result;
            }
            //获取锁
            RLock lock = redissonClient.getLock(key + ":lock");
            //尝试加锁
            if (lock.tryLock(100, 100, TimeUnit.SECONDS)){
               try {
                   try {
                       //执行方法: 查询数据库
                       result = point.proceed(point.getArgs());
                       //防止缓存穿透
                       if (null == result){
                           //并把结果放入缓存,5分钟消失
                           Object o = new Object();
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o), 300, TimeUnit.SECONDS);
                           return null;
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }
                   //并把结果放入缓存: 24小时失效
                   this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result), 24*3600, TimeUnit.SECONDS);
                   return result;
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }

    /**
     * 从redis中获取数据
     * @param signature
     * @param key=getSkuInfo:[1]
     * @return
     */
    private Object cacheHit(MethodSignature signature, String key) {
        //查询缓存
        String cache = (String)redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回 SkuInfo.class
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSONObject.parseObject(cache, returnType);
        }
        //说明redis没有数据
        return null;
    }



}
