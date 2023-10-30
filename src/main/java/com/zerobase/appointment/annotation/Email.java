package com.zerobase.appointment.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import org.springframework.util.StringUtils;

@Documented
@Constraint(validatedBy = Email.EmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Email {

  String message() default "이메일이 양식에 맞지 않습니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class EmailValidator implements ConstraintValidator<Email, String> {

    private final String REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public Pattern email = Pattern.compile(REGEX_EMAIL);

    @Override
    public boolean isValid(String s,
        ConstraintValidatorContext constraintValidatorContext) {
      if (StringUtils.isEmpty(s)) {
        return true;
      } else {
        return email.matcher(s).matches();
      }
    }
  }

}
