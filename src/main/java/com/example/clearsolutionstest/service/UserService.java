package com.example.clearsolutionstest.service;

import com.example.clearsolutionstest.entity.User;
import com.example.clearsolutionstest.repository.UserRepository;
import com.example.clearsolutionstest.request.UserUpdateNonRequiredDataRequest;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository repository;

  public User create(User user) {
    return repository.save(user);
  }

  public User update(Long userId, User newUser) {
    User existingUser = repository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("No user found"));

    existingUser.setEmail(newUser.getEmail());
    existingUser.setFirstname(newUser.getFirstname());
    existingUser.setLastname(newUser.getLastname());
    existingUser.setBirthDate(newUser.getBirthDate());
    existingUser.setAddress(newUser.getAddress());
    existingUser.setPhoneNumber(newUser.getPhoneNumber());

    return repository.save(existingUser);
  }

  public User updateUserNonRequiredData(Long userId, UserUpdateNonRequiredDataRequest request) {
    Optional<User> optionalUser = repository.findById(userId);

    if (optionalUser.isPresent()) {
      User user = optionalUser.get();

      if (request.getAddress() != null) {
        user.setAddress(request.getAddress());
      }

      if (request.getPhoneNumber() != null) {
        user.setPhoneNumber(request.getPhoneNumber());
      }

      return repository.save(user);
    } else {
      throw new NoSuchElementException("No user found");
    }
  }

  public void deleteUser(Long userId) {
    Optional<User> optionalUser = repository.findById(userId);

    if (optionalUser.isPresent()) {
      repository.deleteById(userId);
    } else {
      throw new NoSuchElementException("No user found");
    }
  }

  public Page<User> findUsersByBirthDateRange(Date fromDate, Date toDate, Pageable pageable) {
    if (fromDate.after(toDate)) {
      throw new IllegalArgumentException("The 'From' date must be less than the 'To' date.");
    }
    return repository.findUsersByBirthDateBetween(fromDate, toDate, pageable);
  }


}
