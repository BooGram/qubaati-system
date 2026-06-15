package com.example.qubaatisystem.Service;


import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUser(Integer id, User updatedUser) {
//        User user = userRepository.findUserById(id);
//
//        user.setUsername(updatedUser.getUsername());
//        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
//        user.setName(updatedUser.getName());
//        user.setEmail(updatedUser.getEmail());
//        user.setRole(updatedUser.getRole());
//        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
//        User user = userRepository.findUserById(id);
//        userRepository.delete(user);
    }
}
