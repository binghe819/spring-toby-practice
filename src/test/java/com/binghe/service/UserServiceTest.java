package com.binghe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.binghe.AppConfiguration;
import com.binghe.dao.UserDao;
import com.binghe.domain.Level;
import com.binghe.domain.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfiguration.class)
class UserServiceTest {

    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
            new User("마크", "빙허", "password", Level.BASIC, 49, 0),
            new User("피카", "멍청이", "babo", Level.BASIC, 50, 0),
            new User("토비", "토비토비", "toby", Level.SILVER, 60, 29),
            new User("포비", "포비포비", "poby", Level.SILVER, 60, 30),
            new User("갓갓", "갓", "god", Level.GOLD, 100, 100)
        );
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Test
    void dependency() {
        assertThat(userService).isNotNull();
    }

    @Test
    void upgradeLevels() {
        userDao.deleteAll();

        for (User user : users) {
            userDao.add(user);
        }

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
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
}