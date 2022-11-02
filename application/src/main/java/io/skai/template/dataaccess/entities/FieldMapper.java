package io.skai.template.dataaccess.entities;

import lombok.Value;
import org.jooq.Record;
import org.jooq.TableField;

import java.util.function.BiFunction;

@Value
public class FieldMapper<T, BUILDER> {

    String name;
    TableField<Record, T> dbField;
    BiFunction<BUILDER, Record, BUILDER> valueApplier;

    public FieldMapper(String name, TableField<Record, T> dbField, BiFunction<BUILDER, T, BUILDER> valueApplier) {
        this.name = name;
        this.dbField = dbField;
        this.valueApplier = (builder, record) -> valueApplier.apply(builder, record.get(dbField));
    }

}
