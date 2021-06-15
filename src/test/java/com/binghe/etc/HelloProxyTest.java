package com.binghe.etc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.binghe.etc.dynamic_proxy.UppercaseHandler;
import com.binghe.etc.proxy.HelloUppercase;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HelloProxyTest {

    private String name = "binghe";

    @DisplayName("프록시 테스트 - 타깃 클래스만 존재할 경우")
    @Test
    void simpleProxy() {
        // given
        Hello hello = new HelloTarget();

        // then
        assertThat(hello.sayHello(name)).isEqualTo("Hello " + name);
        assertThat(hello.sayHi(name)).isEqualTo("Hi " + name);
        assertThat(hello.sayThankYou(name)).isEqualTo("Thank You " + name);
    }

    @DisplayName("프록시 테스트 - 타깃 클래스 앞에 프록시 클래스(uppercase) 하나를 추가한 경우")
    @Test
    void upperProxy() {
        // given
        Hello hello = new HelloUppercase(new HelloTarget());

        // then
        assertThat(hello.sayHello(name)).isEqualTo("HELLO BINGHE");
        assertThat(hello.sayHi(name)).isEqualTo("HI BINGHE");
        assertThat(hello.sayThankYou(name)).isEqualTo("THANK YOU BINGHE");
    }

    @DisplayName("다이내믹 프록시 테스트 - 동적으로 다이내믹 프록시 객체를 생성한다.")
    @Test
    void dynamicProxy() {
        // given
        Hello dynamicProxy = (Hello) Proxy.newProxyInstance(
            getClass().getClassLoader(), // 클래스 로더
            new Class[] { Hello.class }, // 다이내믹 프록시가 구현해야 할 인터페이스
            new UppercaseHandler(new HelloTarget())); // 부가 기능과 위임 관련 코드를 담고 있는 InvocationHandler

        // then
        assertThat(dynamicProxy.sayHello(name)).isEqualTo("HELLO BINGHE");
        assertThat(dynamicProxy.sayHi(name)).isEqualTo("HI BINGHE");
        assertThat(dynamicProxy.sayThankYou(name)).isEqualTo("THANK YOU BINGHE");
    }
}
