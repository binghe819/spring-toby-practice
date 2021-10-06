package com.binghe.etc.proxy_factory_bean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class UppercaseAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // InvocationHandler의 리플렉션 Method와 달리 메서드 실행 시 타깃 객체를 전달할 필요가 없다.
        // MethodInvocation은 메서드 정보와 함께 타깃 객체를 알고 있기 때문.
        String ret = (String) invocation.proceed();
        return ret.toUpperCase();
    }
}
