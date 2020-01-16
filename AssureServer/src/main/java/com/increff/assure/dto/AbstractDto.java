package com.increff.assure.dto;

import com.increff.assure.service.ApiException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
public class AbstractDto {

    public static <T> void checkValid(T form) throws ApiException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
        for (ConstraintViolation<T> violation : violations)
            throw new ApiException("Constraint Violation: " + violation.getPropertyPath() + " " + violation.getMessage());
    }

    public static <T> void checkValid(Collection<T> formList) throws ApiException {
        for (T form : formList)
            checkValid(form);
    }

    public void checkNull(Object object, String message) throws ApiException {
        if (Objects.nonNull(object))
            throw new ApiException(message);
    }

    public void checkNotNull(Object object, String message) throws ApiException {
        if (Objects.isNull(object))
            throw new ApiException(message);
    }
}
