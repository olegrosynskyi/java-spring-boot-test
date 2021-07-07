package com.skai.template.dataaccess.dao.impl;

import com.skai.template.dataaccess.dao.UserDao;
import com.skai.template.dataaccess.entities.User;
import com.skai.template.dataaccess.table.UserTable;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final DSLContext dslContext;

    @Override
    public long create(User user) {
        return dslContext.insertInto(UserTable.TABLE, UserTable.TABLE.name)
                .values(user.getName())
                .execute();
    }

    @Override
    public Optional<User> findByName(String name) {
        User user = dslContext.select(UserTable.TABLE.id, UserTable.TABLE.name)
                .from(UserTable.TABLE)
                .where(UserTable.TABLE.name.equal(name))
                .fetchOne(userRec -> User.builder().id(userRec.value1()).name(userRec.value2()).build());
        return Optional.ofNullable(user);
    }
}
