package com.example.clearsolutionstest.validation;

import com.example.clearsolutionstest.entity.User;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

  @Value("${adultAge}")
  private int adultAge;

  @Override
  public boolean supports(Class<?> clazz) {
    return User.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    User user = (User) target;

    if (isBlank(user.getLastname())) {
      errors.rejectValue("lastname", "user.lastname.invalid", "Invalid lastname address.");
    }
    if (isBlank(user.getFirstname())) {
      errors.rejectValue("firstname", "user.firstname.invalid", "Invalid firstname address.");
    }

    if (!isValidEmail(user.getEmail())) {
      errors.rejectValue("email", "user.email.invalid", "Invalid e-mail address.");
    }

    if (!isValidBirthDate(user.getBirthDate())) {
      errors.rejectValue("birthDate", "user.birthDate.invalid",
          "Birth date is after current date.");
    }

    if (!isAdult(user.getBirthDate())) {
      errors.rejectValue("birthDate", "user.birthDate.invalid", "User is too young.");
    }

  }

  private boolean isAdult(Date birthDate) {
    if (birthDate != null) {
      LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

      LocalDate currentDate = LocalDate.now();

      Period period = Period.between(birthLocalDate, currentDate);

      return period.getYears() >= adultAge;
    }
    return false;
  }

  private boolean isValidBirthDate(Date birthDate) {
    Date currentDate = new Date();
    return birthDate != null && birthDate.before(currentDate);
  }

  private boolean isValidEmail(String email) {
    String emailPattern = "(?im)^(?<c1>\\\"?)\\w+(?:[\\W&&[^@]]?)\\w+\\k<c1>@(?:\\[?(?:\\d{3}\\.?){0,4}\\]?|(?:[\\w&&[^\\d]]+[\\.\\-]?)*(?<=\\.\\w{2,6}))$";
    return email != null && Pattern.compile(emailPattern).matcher(email).matches();
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
