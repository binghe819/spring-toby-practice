package com.binghe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.binghe.TestAppConfiguration;
import com.binghe.dao.UserDao;
import com.binghe.domain.Level;
import com.binghe.domain.User;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 다이내믹 프록시를 적용한 UserService 트랜잭션 테스트 (InvocationHandler)
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
public class DynamicProxyUserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private PlatformTransactionManager transactionManager;

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

    @DisplayName("트랜잭션 테스트 - 비즈니스 로직과 트랜잭션 로직 통합 테스트")
    @Test
    void upgradeAllOrNothing() {
        // given
        UserService testUserService = new TestUserService(userDao, mailSender, users.get(3).getId());

        TransactionHandler txHandler = new TransactionHandler(testUserService, transactionManager, "upgradeLevels");

        // when (다이내믹 프록시 적용)
        UserService userServiceTx = (UserService) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] { UserService.class },
            txHandler
        );
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        // then
        try {
            assertThatThrownBy(() -> userServiceTx.upgradeLevels())
                .isInstanceOf(UserServiceTest.TestUserServiceException.class);
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
            if (user.getId().equals(this.id)) throw new UserServiceTest.TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {
    }
}
