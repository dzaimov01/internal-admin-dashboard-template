package com.example.admin.config;

import com.example.admin.domain.Permission;
import com.example.admin.domain.Role;
import com.example.admin.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserDetails implements UserDetails {
  private final User user;
  private final List<GrantedAuthority> authorities;

  public AppUserDetails(User user) {
    this.user = user;
    this.authorities = buildAuthorities(user);
  }

  private List<GrantedAuthority> buildAuthorities(User user) {
    List<GrantedAuthority> roleAuthorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        .collect(Collectors.toList());

    List<GrantedAuthority> permissionAuthorities = user.getRoles().stream()
        .flatMap(role -> role.getPermissions().stream())
        .map(Permission::getName)
        .distinct()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    roleAuthorities.addAll(permissionAuthorities);
    return roleAuthorities;
  }

  public User getUser() {
    return user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getPasswordHash();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return user.getStatus() == com.example.admin.domain.UserStatus.ACTIVE;
  }

  public List<String> roleNames() {
    return user.getRoles().stream().map(Role::getName).toList();
  }

  public List<String> permissionNames() {
    return user.getRoles().stream()
        .flatMap(role -> role.getPermissions().stream())
        .map(Permission::getName)
        .distinct()
        .toList();
  }
}
