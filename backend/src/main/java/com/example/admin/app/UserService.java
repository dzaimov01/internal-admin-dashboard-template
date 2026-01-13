package com.example.admin.app;

import com.example.admin.domain.Role;
import com.example.admin.domain.User;
import com.example.admin.infra.RoleRepository;
import com.example.admin.infra.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<User> list() {
    return userRepository.findAll();
  }

  @Transactional
  public User create(String email, String fullName, String password, List<String> roleNames) {
    User user = new User(email, fullName, passwordEncoder.encode(password));
    for (String roleName : roleNames) {
      Role role = roleRepository.findByName(roleName).orElseThrow();
      user.addRole(role);
    }
    return userRepository.save(user);
  }
}
