package com.binghe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.binghe.TestAppConfiguration;
import com.binghe.dao.UserDao;
import com.binghe.domain.Level;
import com.binghe.domain.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
public class MockitoUserServiceTest {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MailSender mailSender;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
            new User("binghe", "빙허", "password", "binghe@test.com", Level.BASIC, 49, 0),
            new User("jj", "멍청이", "babo", "jj@test.com", Level.BASIC, 50, 0),
            new User("ee", "토비토비", "toby", "ee@test.com",Level.SILVER, 60, 29),
            new User("mm", "포비포비", "poby", "mm@test.com",Level.SILVER, 60, 30),
            new User("gg", "갓", "god", "gg@test.com",Level.GOLD, 100, 100)
        );
    }

    @DisplayName("Mock을 이용한 단위 테스트 - 트랜잭션 기능을 뺀 비즈니스 로직을 테스트한다.")
    @Test
    void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl(userDao, mailSender);

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        // Mockito를 이용해서 어떤 메서드가 몇 번 호출됐는지, 파라미터는 무엇인지 확인할 수 있다.
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        // 파라미터를 정밀하게 검사하기 위해 캡처할 수도 있다.
        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
    }
}
