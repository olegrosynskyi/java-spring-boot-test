package io.skai.template.dataaccess.table;

import org.jooq.Name;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.time.LocalDateTime;

public class CampaignTable extends TableImpl<Record> {

    public static final CampaignTable TABLE = new CampaignTable(DSL.name("campaign"));

    public CampaignTable(Name name) {
        super(name);
    }

    public final TableField<Record, Long> id = createField(DSL.name("id"), SQLDataType.BIGINT);
    public final TableField<Record, String> name = createField(DSL.name("name"), SQLDataType.VARCHAR(255));
    public final TableField<Record, String> ksName = createField(DSL.name("ks_name"), SQLDataType.VARCHAR(255));
    public final TableField<Record, String> status = createField(DSL.name("status"), SQLDataType.VARCHAR(120));
    public final TableField<Record, LocalDateTime> createDate = createField(DSL.name("create_date"), SQLDataType.LOCALDATETIME(6));
    public final TableField<Record, LocalDateTime> lastUpdated = createField(DSL.name("last_updated"), SQLDataType.LOCALDATETIME(6));

}
