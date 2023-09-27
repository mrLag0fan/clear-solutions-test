package com.example.clearsolutionstest.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.clearsolutionstest.entity.User;
import com.example.clearsolutionstest.repository.UserRepository;
import com.example.clearsolutionstest.request.UserUpdateNonRequiredDataRequest;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @Test
  public void testCreateUser() {
    User user = new User();
    when(userRepository.save(user)).thenReturn(user);

    User createdUser = userService.create(user);

    assertNotNull(createdUser);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  public void testUpdateUser() {
    Long userId = 1L;
    User newUser = new User();
    newUser.setEmail("newemail@example.com");

    User existingUser = new User();
    existingUser.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(existingUser)).thenReturn(existingUser);

    User updatedUser = userService.update(userId, newUser);

    assertEquals(newUser.getEmail(), updatedUser.getEmail());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).save(existingUser);
  }

  @Test
  public void testUpdateUserNonRequiredData() {
    Long userId = 1L;
    UserUpdateNonRequiredDataRequest request = new UserUpdateNonRequiredDataRequest();
    request.setAddress("New Address");

    User existingUser = new User();
    existingUser.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(existingUser)).thenReturn(existingUser);

    User updatedUser = userService.updateUserNonRequiredData(userId, request);

    assertEquals(request.getAddress(), updatedUser.getAddress());
    assertNull(updatedUser.getPhoneNumber());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).save(existingUser);
  }

  @Test
  public void testDeleteUser() {
    Long userId = 1L;
    User userToDelete = new User();
    userToDelete.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

    assertDoesNotThrow(() -> userService.deleteUser(userId));
    verify(userRepository, times(1)).deleteById(userId);
  }

  @Test
  public void testFindUsersByBirthDateRange() {
    Date fromDate = new Date();
    Date toDate = new Date(fromDate.getTime() - 1000);

    assertThrows(IllegalArgumentException.class,
        () -> userService.findUsersByBirthDateRange(fromDate, toDate, null));
  }

  @Test
  public void testFindUsersByBirthDateRangeValid() {
    Date fromDate = new Date();
    Date toDate = new Date(fromDate.getTime() + 1000);

    userService.findUsersByBirthDateRange(fromDate, toDate, null);
  }

  @Test
  public void testDeleteUserNotFound() {
    Long userId = -1L;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> userService.deleteUser(userId));
    verify(userRepository, times(0)).deleteById(userId);
  }

  @Test
  public void testUpdateUserNonRequiredDataWithPhoneNumber() {
    Long userId = 1L;
    UserUpdateNonRequiredDataRequest request = new UserUpdateNonRequiredDataRequest();
    request.setPhoneNumber("123-456-7890");

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setAddress("Old Address");
    existingUser.setPhoneNumber("Old Phone Number");

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(existingUser);

    User updatedUser = userService.updateUserNonRequiredData(userId, request);

    assertNotNull(updatedUser);
    assertEquals(request.getPhoneNumber(), updatedUser.getPhoneNumber());
    assertEquals(existingUser.getAddress(), updatedUser.getAddress());
  }

  @Test
  public void testUpdateUserNonRequiredDataWithPhoneNumberNotFound() {
    Long userId = 1L;
    UserUpdateNonRequiredDataRequest request = new UserUpdateNonRequiredDataRequest();
    request.setPhoneNumber("123-456-7890");

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    NoSuchElementException exception = assertThrows(NoSuchElementException.class,
        () -> userService.updateUserNonRequiredData(userId, request));
    assertEquals("No user found", exception.getMessage());

    verify(userRepository, times(0)).save(any(User.class));
  }
}

