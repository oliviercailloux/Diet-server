package io.github.oliviercailloux.diet;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateUserRequest {

	@Email
	@NotBlank(message = "email may not be blank")
	private String email;

	@Size(min = 4, max = 15, message = "username should have size [{min},{max}]")
	@NotBlank(message = "username may not be blank")
	@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]+$", message = "\"username\" should start with a letter and should only accept letters and numbers")
	private String username;

	@NotBlank(message = "firstName may not be blank")
	private String firstName;

	@NotBlank(message = "lastName may not be blank")
	private String lastName;

	private boolean admin;

	@NotBlank(message = "hashedPassword may not be blank")
	private String hashedPassword;

	// [Getters and Setters]
}
