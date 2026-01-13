package com.example.admin.api;

import com.example.admin.app.UserService;
import com.example.admin.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {
  private final UserService userService;

  public UserManagementController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('users:read')")
  public List<UserSummary> list() {
    return userService.list().stream()
        .map(user -> new UserSummary(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getRoles().stream().map(role -> role.getName()).toList()))
        .toList();
  }

  @PostMapping
  @PreAuthorize("hasAuthority('users:write')")
  public UserSummary create(@Valid @RequestBody CreateUserRequest request) {
    User user = userService.create(request.email(), request.fullName(), request.password(), request.roles());
    return new UserSummary(
        user.getId(),
        user.getEmail(),
        user.getFullName(),
        user.getRoles().stream().map(role -> role.getName()).toList());
  }

  public record CreateUserRequest(
      @Email @NotBlank String email,
      @NotBlank String fullName,
      @NotBlank String password,
      @NotEmpty List<String> roles) {}

  public record UserSummary(Long id, String email, String fullName, List<String> roles) {}
}
