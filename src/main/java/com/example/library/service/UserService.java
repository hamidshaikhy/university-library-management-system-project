package com.example.library.service;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("کاربر پیدا نشد."));
    }

    public long count() {
        return userRepository.count();
    }
}
