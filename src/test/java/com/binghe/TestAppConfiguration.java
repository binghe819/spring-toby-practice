package com.binghe;

import com.binghe.dao.UserDao;
import com.binghe.dao.UserDaoJdbc;
import com.binghe.etc.FactoryBean.Message;
import com.binghe.etc.FactoryBean.MessageFactoryBean;
import com.binghe.service.DummyMailSender;
import com.binghe.service.TransactionAdvice;
import com.binghe.service.TxProxyFactoryBean;
import com.binghe.service.UserService;
import com.binghe.service.UserServiceImpl;
import com.binghe.service.UserServiceTx;
import javax.sql.DataSource;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TestAppConfiguration {

    @Bean
    public Message message() throws Exception {
        return new MessageFactoryBean("Factory Bean - binghe").getObject();
    }

    @Bean
    public UserDao userDao(){
        return new UserDaoJdbc(dataSource());
    }

    @Bean
    public TxProxyFactoryBean txProxyFactoryBean() {
        return new TxProxyFactoryBean(
            userServiceImpl(),
            platformTransactionManager(),
            "upgradeLevels",
            UserService.class);
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
    public TransactionAdvice transactionAdvice() {
        return new TransactionAdvice(platformTransactionManager());
    }

    @Bean
    public NameMatchMethodPointcut transactionPointcut() {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transcationAdvisor() {
        return new DefaultPointcutAdvisor(transactionPointcut(), transactionAdvice());
    }

    @Bean
    public ProxyFactoryBean proxyFactoryBeanUserService() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(userServiceImpl());
        pfBean.addAdvisor(transcationAdvisor());
        return pfBean;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/Desktop/Code/h2");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
