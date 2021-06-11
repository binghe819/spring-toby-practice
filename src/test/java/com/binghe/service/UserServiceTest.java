package com.binghe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
public class UserServiceTest {

    @Autowired
    private UserService userService; // UserServiceImpl(비즈니스 로직)을 가지고 있는 UserService(트랜잭션 경계 설정) - add 테스트용
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

    @DisplayName("Mock을 이용한 단위 테스트 - 트랜잭션 기능을 뺀 비즈니스 로직만을 테스트한다.")
    @Test
    void upgradeLevels() {
        // 고립된 테스트에서는 테스트 대상 객체를 직접 생성하면 된다.
        UserServiceImpl userServiceImpl = new UserServiceImpl(userDao, mailSender);

        // 목 객체로 만든 UserDao를 직접 DI 해준다.
        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        // 목 객체로 만든 만든 MailSender를 직접 DI 해준다.
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        // 비즈니스 로직 실행
        userServiceImpl.upgradeLevels();

        // MockUserDao로부터 업데이트 결과를 가져온다.
        List<User> updated = mockUserDao.getUpdated();
        // 업데이트 횟수와 정보를 확인한다.
        assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "jj", Level.SILVER);
        checkUserAndLevel(updated.get(1), "mm", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size()).isEqualTo(2);
        assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

    @DisplayName("트랜잭션 테스트 - 비즈니스 로직과 트랜잭션 로직 통합 테스트")
    @Test
    void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(userDao, mailSender, users.get(3).getId());

        UserServiceTx userServiceTx = new UserServiceTx(testUserService, transactionManager);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

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
