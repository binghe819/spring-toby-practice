package com.binghe;

import com.binghe.dao.UserDao;
import com.binghe.dao.UserDaoJdbc;
import com.binghe.service.DummyMailSender;
import com.binghe.service.UserService;
import com.binghe.service.UserServiceImpl;
import com.binghe.service.UserServiceTx;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TestAppConfiguration {

    @Bean
    public UserDao userDao(){
        return new UserDaoJdbc(dataSource());
    }

    @Bean
    public UserService userService() {
        return new UserServiceTx(userServiceImpl(), platformTransactionManager());
    }

    @Bean
    public UserServiceImpl userServiceImpl() {
        return new UserServiceImpl(userDao(), mailSender());
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/toby");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
