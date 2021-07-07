package com.skai.template.dataaccess.table;

import org.jooq.Name;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

public class UserTable extends TableImpl<Record> {

    public static final UserTable TABLE = new UserTable(DSL.name("user"));

    public UserTable(Name name) {
        super(name);
    }

    public final TableField<Record, Long> id = createField(DSL.name("id"), SQLDataType.BIGINT);
    public final TableField<Record, String> name = createField(DSL.name("name"), SQLDataType.VARCHAR.length(50));
}
