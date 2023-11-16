/* (C) 2023 */
package com.workshop.api.user;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired private UserRepository userRepository;

  public UserModel createUser(UserModel user) {
    return userRepository.save(user);
  }

  public List<UserModel> getUsers(List<String> ids) {
    return ids.isEmpty() ? userRepository.findAll() : userRepository.findAllById(ids);
  }

  public Optional<UserModel> getUser(String id) {
    return userRepository.findById(id);
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }
}
