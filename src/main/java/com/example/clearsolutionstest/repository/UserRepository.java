package com.example.clearsolutionstest.repository;

import com.example.clearsolutionstest.entity.User;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Page<User> findUsersByBirthDateBetween(Date fromDate, Date toDate, Pageable pageable);
}
