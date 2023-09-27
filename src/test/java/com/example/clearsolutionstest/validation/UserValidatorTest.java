package com.example.clearsolutionstest.validation;

import com.example.clearsolutionstest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

  private UserValidator userValidator;
  private Errors errors;
  private User user;

  @BeforeEach
  public void setUp() {
    userValidator = new UserValidator();
    user = new User();
    errors = new BeanPropertyBindingResult(user, "user");

  }

  @Test
  public void supports_ValidClass_ReturnsTrue() {
    assertTrue(userValidator.supports(User.class));
  }

  @Test
  public void supports_InvalidClass_ReturnsFalse() {
    assertFalse(userValidator.supports(String.class));
  }

  @Test
  public void validate_ValidUser_NoErrors() {
    user.setFirstname("John");
    user.setLastname("Doe");
    user.setEmail("john@example.com");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date birthDate = null;
    try {
      birthDate = dateFormat.parse("1990-01-01");
    } catch (ParseException e) {
      fail(e.getMessage());
    }
    user.setBirthDate(birthDate);

    userValidator.validate(user, errors);

    assertFalse(errors.hasErrors());
  }

  @Test
  public void validate_InvalidUser_ValidationErrors() {
    userValidator.validate(user, errors);

    assertTrue(errors.hasErrors());
    assertEquals(5, errors.getErrorCount());
  }
}
