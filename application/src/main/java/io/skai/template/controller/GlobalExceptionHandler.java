package io.skai.template.controller;

import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import com.kenshoo.openplatform.apimodel.enums.StatusResponse;
import io.skai.template.dataaccess.entities.FieldValidationException;
import io.skai.template.dataaccess.entities.QueryFilterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({FieldValidationException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ApiResponse<WriteResponseDto<Long>> idException(FieldValidationException e) {
        final WriteResponseDto<Long> dto = new WriteResponseDto.Builder<Long>()
                .withErrors(e.getFieldErrors())
                .withId(e.getEntityId())
                .build();

        return new ApiResponse.Builder<WriteResponseDto<Long>>()
                .withStatus(StatusResponse.FAILED)
                .withEntities(List.of(dto))
                .build();
    }

    @ExceptionHandler({QueryFilterException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ApiResponse<?> invalidQueryFilterException(QueryFilterException e) {
        final WriteResponseDto<?> dto = new WriteResponseDto.Builder<>()
                .withErrors(e.getFieldErrors())
                .build();

        return new ApiResponse.Builder<>()
                .withStatus(StatusResponse.FAILED)
                .withEntities(List.of(dto))
                .build();
    }

}
