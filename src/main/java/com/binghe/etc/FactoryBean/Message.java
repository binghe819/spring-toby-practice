package com.binghe.etc.FactoryBean;

/**
 * 팩토리 빈 학습 테스트용 도메인
 * -> 기본 생성자를 통해 객체를 만들 수 없다.
 */
public class Message {

    private String text;

    private Message(String text) {
        this.text = text;
    }

    public static Message newMessage(String text) {
        return new Message(text);
    }

    public String getText() {
        return text;
    }
}
