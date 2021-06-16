package com.binghe.service;

import java.lang.reflect.Proxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {
    // TransactionHandler를 사용할 때 필요한 의존성
    private Object target;
    private PlatformTransactionManager transactionManager;
    private String pattern;
    // 다이내믹 프록시를 생성할 때 필요한 의존성 (UserService외 인터페이스에도 적용이 가능하다.)
    private Class<?> serviceInterface;

    public TxProxyFactoryBean(Object target,
        PlatformTransactionManager transactionManager, String pattern,
        Class<?> serviceInterface) {
        this.target = target;
        this.transactionManager = transactionManager;
        this.pattern = pattern;
        this.serviceInterface = serviceInterface;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(
        PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler(target, transactionManager, pattern);
        return Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] { serviceInterface },
            txHandler
        );
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false; // 싱글톤이 아니라는 의미.
    }
}
