package com.binghe.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionHandler implements InvocationHandler {
    private final Object target; // 부가기능을 제공할 타깃 객체 (어떤 타입도 가능하다)
    private final PlatformTransactionManager transactionManager; // 트랜잭션 기능을 제공하는 데 필요한 매니저
    private final String pattern; // 트랜잭션을 적용할 메서드 이름 패턴

    public TransactionHandler(Object target,
        PlatformTransactionManager transactionManager, String pattern) {
        this.target = target;
        this.transactionManager = transactionManager;
        this.pattern = pattern;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 트랜잭션 적용 대상 메서드를 선별해서 트랜잭션 경계설정 기능을 부여해준다.
        if (method.getName().startsWith(pattern)) {
            return invokeInTransaction(method, args);
        }
        return method.invoke(method, args);
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 트랜잭션 시작.
            Object ret = method.invoke(target, args);
            this.transactionManager.commit(status);
            return ret;
        } catch (InvocationTargetException e) {
            // 예외 발생시 롤백
            this.transactionManager.rollback(status);
            throw e.getTargetException();
        }
    }
}
