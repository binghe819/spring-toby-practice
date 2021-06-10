package com.binghe.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MockMailSender implements MailSender {

    // UserService로부터 전송 요청을 받은 메일 주소 저장.
    private List<String> requests = new ArrayList<String>();

    public List<String> getRequests() {
        return requests;
    }

    public void send(SimpleMailMessage mailMessage) throws MailException {
        requests.add(mailMessage.getTo()[0]);
    }

    public void send(SimpleMailMessage[] mailMessage) throws MailException {
    }
}
