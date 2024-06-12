package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * 用户登录的服务接口实现类
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    /**
     * 登录
     *  @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //定义请求的地址
        ServiceInstance choose =
                loadBalancerClient.choose("service-oauth");
        String url = choose.getUri().toString() + "/oauth/token";
//        String url = "http://localhost:9001/oauth/token";
        //定义请求头
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("Authorization", getHeadParam());
        //定义请求体
        MultiValueMap<String, String> body = new HttpHeaders();
        body.set("grant_type", "password");
        body.set("username", username);
        body.set("password", password);

        HttpEntity httpEntity = new HttpEntity(body, headers);
        //发送post请求
        /**
         * 1.请求的地址
         * 2.请求的方法的方式
         */
        ResponseEntity<Map> exchange =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        //获取结果
        Map<String, String> result = exchange.getBody();
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(result.get("access_token"));
        authToken.setRefreshToken(result.get("refresh_token"));
        authToken.setJti(result.get("jti"));
        //返回
        return authToken;
    }

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    /**
     * 拼接请求头的参数
     * @return
     */
    private String getHeadParam(){
        //拼接客户端id和客户端秘钥
        String param = clientId + ":" + clientSecret;
        //加密
        String s = new String(Base64.getEncoder().encode(param.getBytes()));
        return "Basic " + s;
    }
}
