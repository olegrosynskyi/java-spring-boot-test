package io.skai.template.dataaccess.entities;

import com.kenshoo.openplatform.apimodel.errors.FieldError;
import lombok.Getter;

import java.util.List;

@Getter
public class QueryFilterException extends RuntimeException {

    private final List<FieldError> fieldErrors;

    public QueryFilterException(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

}
