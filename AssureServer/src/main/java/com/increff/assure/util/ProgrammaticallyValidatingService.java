/*
package com.increff.assure.util;

import model.form.ConsumerForm;

import javax.validation.*;
import java.util.Set;

public class ProgrammaticallyValidatingService {
  
  static <T> void validateInput(T input) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<T>> violations = validator.validate(input);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  public static void main(String[] args) {
    ConsumerForm form = new ConsumerForm();
    form.setName("ABC");

    validateInput(form);
  }

}*/
