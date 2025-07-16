package com.jupiter.genai.aop;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @desc: Aspect to set a proxy for the HttpClientBuilder.
 * @author: Jupiter.Lin
 * @date: 2025/7/16
 */
@Aspect
@Component
public class ApiClientProxyAspect {
    @Around("execution(* org.apache.http.impl.client.HttpClientBuilder.build(..))")
    public Object addProxyToHttpClient(ProceedingJoinPoint pjp) throws Throwable {
        HttpClientBuilder builder = (HttpClientBuilder)pjp.getThis();
        builder.setProxy(new HttpHost("localhost", 8086));
        pjp.proceed();
        System.out.println("设置代理成功: localhost:8086");
        return null;
    }
}
