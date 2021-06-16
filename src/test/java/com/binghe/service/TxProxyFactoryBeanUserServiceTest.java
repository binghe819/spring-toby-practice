package com.binghe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.binghe.TestAppConfiguration;
import com.binghe.dao.UserDao;
import com.binghe.domain.Level;
import com.binghe.domain.User;
import com.binghe.service.DynamicProxyUserServiceTest.TestUserServiceException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 다이내믹 프록시를 팩토리 빈으로 등록하여 UserService 테스트하는 코드
 */

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
public class TxProxyFactoryBeanUserServiceTest {

    @Autowired
    private TxProxyFactoryBean txProxyFactoryBean;
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

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(userDao, mailSender, users.get(3).getId());

        txProxyFactoryBean.setTarget(testUserService);
        UserService userServiceTx = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        // then
        try {
            assertThatThrownBy(() -> userServiceTx.upgradeLevels())
                .isInstanceOf(TestUserServiceException.class);
        }
        catch(TestUserServiceException e) {
        }

        checkLevelUpgraded(users.get(1), false);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
        }
        else {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
    }

    static class TestUserService extends UserServiceImpl {
        private String id;

        public TestUserService(UserDao userDao, MailSender mailSender, String id) {
            super(userDao, mailSender);
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {
    }
}
