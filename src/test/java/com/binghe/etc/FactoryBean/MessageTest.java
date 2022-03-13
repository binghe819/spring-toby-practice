package com.binghe.etc.FactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.binghe.TestAppConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
class MessageTest {

    @Autowired
    private Message message;

    @DisplayName("팩토리 빈을 등록하면 생성하는 타입으로 빈이 등록된다.")
    @Test
    void beanFactory() {
        assertThat(message).isNotNull();
        assertThat(message).isInstanceOf(Message.class);
        assertThat(message.getText()).isEqualTo("Factory Bean - binghe");
    }
}
