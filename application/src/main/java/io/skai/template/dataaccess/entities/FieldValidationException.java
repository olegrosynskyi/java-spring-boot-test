package io.skai.template.dataaccess.entities;

import com.kenshoo.openplatform.apimodel.errors.FieldError;
import lombok.Getter;

import java.util.List;

@Getter
public class FieldValidationException extends RuntimeException {

    private final Long entityId;
    private final List<FieldError> fieldErrors;

    public FieldValidationException(Long entityId, List<FieldError> fieldErrors) {
        this.entityId = entityId;
        this.fieldErrors = fieldErrors;
    }

}
