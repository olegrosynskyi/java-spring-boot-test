package com.skai.template.dataaccess.dao;

import com.skai.template.dataaccess.entities.User;

import java.util.Optional;

public interface UserDao {

    long create(User user);

    Optional<User> findByName(String name);

}
