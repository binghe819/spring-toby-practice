package com.binghe.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.binghe.user.domain.User;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class UserDaoTest {

    @Test
    void addAndGet() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        User user = new User("mark", "binghe", "password");

        dao.add(user);
        assertThat(dao.getCount()).isEqualTo(1);

        User foundUser = dao.get(user.getId());
        assertAll(
            () -> assertThat(foundUser.getId()).isEqualTo(user.getId()),
            () -> assertThat(foundUser.getName()).isEqualTo(user.getName()),
            () -> assertThat(foundUser.getPassword()).isEqualTo(user.getPassword())
        );
    }
}
