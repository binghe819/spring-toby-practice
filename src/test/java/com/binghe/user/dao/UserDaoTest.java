package com.binghe.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.binghe.user.domain.User;
import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

class UserDaoTest {

    @Test
    void addAndGet() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        User user1 = new User("mark", "binghe", "password");
        User user2 = new User("pika", "jiwoo", "password");

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        User foundUser1 = dao.get(user1.getId());
        User foundUser2 = dao.get(user2.getId());

        assertAll(
            () -> assertThat(foundUser1.getId()).isEqualTo(user1.getId()),
            () -> assertThat(foundUser1.getName()).isEqualTo(user1.getName()),
            () -> assertThat(foundUser1.getPassword()).isEqualTo(user1.getPassword()),
            () -> assertThat(foundUser2.getId()).isEqualTo(user2.getId()),
            () -> assertThat(foundUser2.getName()).isEqualTo(user2.getName()),
            () -> assertThat(foundUser2.getPassword()).isEqualTo(user2.getPassword())
        );
    }

    @DisplayName("get 실패 테스트")
    @Test
    void get_negative() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao userDao = context.getBean("userDao", UserDao.class);

        userDao.deleteAll();
        assertThat(userDao.getCount()).isEqualTo(0);

        assertThatThrownBy(() -> userDao.get("0"))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
