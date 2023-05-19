package io.github.oliviercailloux.diet.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BasicUsernameValidator.class)
public @interface BasicUsername {
	String message() default "Username should contain no colon for Basic authentication";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}