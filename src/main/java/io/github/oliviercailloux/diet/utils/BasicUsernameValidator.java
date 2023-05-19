package io.github.oliviercailloux.diet.utils;

import io.github.oliviercailloux.diet.user.Login;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BasicUsernameValidator implements ConstraintValidator<BasicUsername, Login> {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicUsernameValidator.class);

	@Override
	public boolean isValid(Login value, ConstraintValidatorContext context) {
		LOGGER.info("Validating {}.", value);
		return !value.getUsername().contains(":");
	}

}
