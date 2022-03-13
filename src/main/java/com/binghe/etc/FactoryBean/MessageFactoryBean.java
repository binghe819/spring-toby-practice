package com.binghe.etc.FactoryBean;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {
    private String text;

    // 객체를 생성할 때 필요한 정보를 팩토리 빈의 상태로 설정해서 대신 DI받는다.
    public MessageFactoryBean(String text) {
        this.text = text;
    }

    // 실제 빈으로 사용될 객체를 직접 생성한다. 보통 다이내믹 프록시는 여기서 생성된다.
    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(this.text);
    }

    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }

    // 싱글톤 여부. 다이내믹 프록시를 등록한다면 당연히 false로 하면 된다.
    @Override
    public boolean isSingleton() {
        return false;
    }
}
