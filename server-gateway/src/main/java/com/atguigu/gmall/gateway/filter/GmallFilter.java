package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.gateway.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 过滤器的逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求对象request
        ServerHttpRequest request = exchange.getRequest();
        //获取响应体
        ServerHttpResponse response = exchange.getResponse();
        //从url中获取token
        String token = request.getQueryParams().getFirst("token");
        if(StringUtils.isEmpty(token)){
            //若url中没有,从head中获取token
            token = request.getHeaders().getFirst("token");
            //若三个地方都没,拒绝用户的请求
            if(StringUtils.isEmpty(token)){
                //若head中也没有,从cookie获取token
                MultiValueMap<String, HttpCookie> cookies =
                        request.getCookies();
                if(cookies != null && !cookies.isEmpty()){
                    HttpCookie httpCookie = cookies.getFirst("token");
                    if(httpCookie != null){
                        String name = httpCookie.getName();
                        token = httpCookie.getValue();
                    }
                }
            }
        }
        //判断token是否为空
        if(StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            return response.setComplete();
        }
        //获取本次请求的ip地址
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        String redisToken = stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        if(StringUtils.isEmpty(redisToken)){
            //这个ip地址没有登陆过,这个令牌要么是过期的要么是假的要么是盗用的
            response.setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            return response.setComplete();
        }
        //判断用户传递的token和redis中的token是否一致
        if(!redisToken.equals(token)){
            response.setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            return response.setComplete();
        }
        //测试
        redisTemplate.opsForValue().set("java1227", "123");
        //若token存在,则将token以固定的格式写入请求头
        request.mutate().header("Authorization", "bearer " + token);
        //放行
        return chain.filter(exchange);
    }

    /**
     * 过滤器的执行顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
