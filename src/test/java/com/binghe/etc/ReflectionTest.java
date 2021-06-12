package com.binghe.etc;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReflectionTest {

    @DisplayName("리플렉션 - 클래스의 메서드 정보를 가져오고 실행할 수 있다.")
    @Test
    void invokeMethod()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        String name = "binghe";

        // when
        Method lengthMethod = String.class.getMethod("length"); // length
        Method charAtMethod = String.class.getMethod("charAt", int.class); // charAt

        // then
        assertThat(lengthMethod.getName()).isEqualTo("length"); // 메타데이터
        assertThat(charAtMethod.getName()).isEqualTo("charAt"); // 메타데이터
        assertThat(lengthMethod.invoke(name)).isEqualTo(name.length()); // 실행
        assertThat(charAtMethod.invoke(name, 0)).isEqualTo(name.charAt(0)); // 실행
    }
}
