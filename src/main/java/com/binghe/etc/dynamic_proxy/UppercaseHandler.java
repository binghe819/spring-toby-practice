package com.binghe.etc.dynamic_proxy;

import com.binghe.etc.Hello;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;

public class UppercaseHandler implements InvocationHandler {
    private final Hello target;

    // 다이내믹 프록시로부터 전달받은 요청을 다시 타깃 객체에 위임해야 하기 때문에 타깃 객체를 주입받는다.
    public UppercaseHandler(Hello target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String ret = (String) method.invoke(target, args); // 타깃으로 위임(실행). 인터페이스의 메서드 호출에 모두 적용된다.
        return ret.toUpperCase(); // 부가기능 제공 (리턴값은 클라이언트에게 전해진다.)
    }
}
