package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * redis修改值测试
     */
    @Override
    public void setRedis() {
        //生成随机数
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //获取锁
        Boolean lock =
                redisTemplate.opsForValue().setIfAbsent("lock", uuid, 10, TimeUnit.SECONDS);
        if(lock){
            //从redis中获取一个key的值
            Integer java1227 =
                    (Integer)redisTemplate.opsForValue().get("java1227");
            //若这个值不为空则做+1操作
            if(java1227 != null){
                java1227++;
                //修改这个值
                redisTemplate.opsForValue().set("java1227", java1227);
            }
            //声明脚本
            DefaultRedisScript script = new DefaultRedisScript();
            //设置脚本的返回结果类型
            script.setResultType(Long.class);
            //设置脚本
            script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            //解决锁误删-----通过原子性删除--Lua脚本
            redisTemplate.execute(script, Arrays.asList("lock"), uuid);
        }else{
            try {
                Thread.sleep(100);
                //递归
                setRedis();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private RedissonClient redissonClient;
    /**
     * 通过redission加锁释放锁
     */
    @Override
    public void redssionRedis() {
        //获取锁
        RLock lock = redissonClient.getLock("lock");
        try {
            //加锁: setnx
            if(lock.tryLock(100,100, TimeUnit.SECONDS)){
                try {
                    //从redis中获取一个key的值
                    Integer java1227 =
                            (Integer)redisTemplate.opsForValue().get("java1227");
                    //若这个值不为空则做+1操作
                    if(java1227 != null){
                        java1227++;
                        //修改这个值
                        redisTemplate.opsForValue().set("java1227", java1227);
                    }
                }catch (Exception e){
                    System.out.println("加锁成功,但是逻辑处理出现异常");
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }
        }catch (Exception e){
            System.out.println("加锁失败,加锁出现异常");
        }
    }
}
