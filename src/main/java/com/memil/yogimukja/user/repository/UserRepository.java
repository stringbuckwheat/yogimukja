package com.memil.yogimukja.user.repository;

import com.memil.yogimukja.user.entity.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> findById(Long userId);
    Optional<User> findByUsername(String username);
    User save(User user);
    void deleteById(Long userId);
}
