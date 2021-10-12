package com.skai.template.dataaccess.dao;

import com.skai.template.Application;
import com.skai.template.dataaccess.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class UserDaoIntegrationTest {

    @Autowired
    private UserDao userDao;

    @Test
    void verifyUserCreation() {
        long userId = createUser(UUID.randomUUID().toString());
        assertThat(userId, greaterThan(0L));
    }

    @Test
    void verifyUserFetchingByName() {
        String name = UUID.randomUUID().toString();
        createUsers(name, UUID.randomUUID().toString());

        Optional<User> result = userDao.findByName(name);
        result.ifPresentOrElse(
                persistedUser -> assertEquals(persistedUser.getName(), name),
                () -> fail("User not found by name : " + name));
    }

    @Test
    void verifyUserFetchingByNameWhenNotExists() {
        createUser(UUID.randomUUID().toString());
        Optional<User> result = userDao.findByName("user_3");
        assertTrue(result.isEmpty(), "User found in DB when it does not suppose to exist");
    }

    private void createUsers(String... names) {
        Arrays.stream(names).forEach(this::createUser);
    }

    private long createUser(String name) {
        return userDao.create(User.builder().name(name).build());
    }
}
