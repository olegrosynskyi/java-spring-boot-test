package com.skai.template.dataaccess.dao;

import com.skai.template.Application;
import com.skai.template.dataaccess.entities.User;
import com.skai.template.dataaccess.table.UserTable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@SpringBootTest(classes = Application.class)
class UserDaoIntegrationTest {

    @Autowired
    private UserDao userDao;
    @Autowired
    private DSLContext dslContext;

    private static final User USER_1 = User.builder().name("user_1").build();
    private static final User USER_2 = User.builder().name("user_2").build();

    @AfterEach
    public void cleanUp() {
        dslContext.truncate(UserTable.TABLE).execute();
    }

    @Test
    void verifyUserCreation() {
        assertEquals(1, userDao.create(USER_1));
        assertEquals(1, userDao.create(USER_2));
    }

    @Test
    void verifyUserFetchingByName() {
        userDao.create(USER_1);
        userDao.create(USER_2);

        Optional<User> result = userDao.findByName(USER_1.getName());
        result.ifPresentOrElse(
                user -> assertEquals(user.getName(), USER_1.getName()),
                () -> fail("User not found by name : " + USER_1.getName()));
    }

    @Test
    void verifyUserFetchingByNameWhenNotExists() {
        userDao.create(USER_1);
        Optional<User> result = userDao.findByName("user_3");
        assertTrue(result.isEmpty(), "User found in DB when it does not suppose to exist");
    }
}