package com.binghe.etc.dynamic_proxy;

import com.binghe.etc.Hello;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;

public class UppercaseHandler implements InvocationHandler {
    private final Object target; // String에서 Object로 수정

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args); // 타깃으로 위임(실행).
        if (ret instanceof String && method.getName().startsWith("say")) {
            return ((String) ret).toUpperCase();
        }
        return ret;
    }
}
