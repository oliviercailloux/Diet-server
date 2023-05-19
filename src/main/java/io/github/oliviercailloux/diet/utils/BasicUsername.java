package io.github.oliviercailloux.diet.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BasicUsernameValidator.class)
public @interface BasicUsername {
	String message() default "Username should contain no colon for Basic authentication";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}