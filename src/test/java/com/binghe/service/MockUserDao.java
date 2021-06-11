package com.binghe.service;

import com.binghe.dao.UserDao;
import com.binghe.domain.User;
import java.util.ArrayList;
import java.util.List;

public class MockUserDao implements UserDao {

    // 레벨 업그레이드 후보 User 객체 목록
    private List<User> users;
    // 업그레이드 대상 객체를 저장해둔 목록
    private List<User> updated = new ArrayList();

    public MockUserDao(List<User> users) {
        this.users = users;
    }

    public List<User> getUpdated() {
        return this.updated;
    }

    public List<User> getAll() {
        return this.users;
    }

    public void update(User user) {
        updated.add(user);
    }

    public void add(User user) { throw new UnsupportedOperationException(); }
    public void deleteAll() { throw new UnsupportedOperationException(); }
    public User get(String id) { throw new UnsupportedOperationException(); }
    public int getCount() { throw new UnsupportedOperationException(); }
}
