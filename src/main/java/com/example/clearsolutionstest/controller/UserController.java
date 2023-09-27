package com.example.clearsolutionstest.controller;

import com.example.clearsolutionstest.entity.User;
import com.example.clearsolutionstest.request.UserUpdateNonRequiredDataRequest;
import com.example.clearsolutionstest.service.UserService;
import com.example.clearsolutionstest.validation.UserValidator;
import java.util.Date;
import java.util.NoSuchElementException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserValidator validator;

  @PostMapping
  public ResponseEntity<?> createUser(@RequestBody @Valid User user, BindingResult bindingResult) {
    validator.validate(user, bindingResult);
    if (bindingResult.hasErrors()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
    }
    User createdUser = userService.create(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }


  @PatchMapping("/{userId}")
  public ResponseEntity<?> updateUserNonRequiredData(@PathVariable Long userId,
      @RequestBody UserUpdateNonRequiredDataRequest request) {
    try {
      User updatedUser = userService.updateUserNonRequiredData(userId, request);
      if (updatedUser != null) {
        return ResponseEntity.ok(updatedUser);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{userId}")
  public ResponseEntity<?> updateAllUserFields(@PathVariable Long userId,
      @RequestBody @Valid User updatedUserData, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
    }

    try {
      User updatedUser = userService.update(userId, updatedUserData);
      if (updatedUser != null) {
        return ResponseEntity.ok(updatedUser);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchUsersByBirthDateRange(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
      @PageableDefault(size = 10, page = 0) Pageable pageable) {
    try {
      Page<User> users = userService.findUsersByBirthDateRange(from, to, pageable);
      return ResponseEntity.ok(users);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}
