package com.increff.assure.dto;

import com.increff.assure.service.ApiException;
import model.form.BinSkuForm;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;

@Service
public class AbstractDto {

    public static <T> void validate(T form) throws ApiException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
        for (ConstraintViolation<T> violation : violations)
            throw new ApiException("Constraint Violation: " + violation.getPropertyPath() + violation.getMessage());
    }

    public static <T> void validate(Collection<T> formList) throws ApiException {
        for (T form : formList)
            validate(form);
    }
}
