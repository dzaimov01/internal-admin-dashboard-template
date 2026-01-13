package com.example.admin;

import com.example.admin.domain.Role;
import com.example.admin.domain.User;
import com.example.admin.infra.AuditLogRepository;
import com.example.admin.infra.RoleRepository;
import com.example.admin.infra.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IntegrationSecurityTest {
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  AuditLogRepository auditLogRepository;

  @BeforeEach
  void setUp() {
    if (userRepository.findByEmail("viewer@acme.test").isEmpty()) {
      Role viewer = roleRepository.findByName("Viewer").orElseThrow();
      User user = new User("viewer@acme.test", "Viewer User", passwordEncoder.encode("viewer123!"));
      user.addRole(viewer);
      userRepository.save(user);
    }
  }

  @Test
  void viewerCannotCreateCustomer() throws Exception {
    String viewerLogin = "{\"email\":\"viewer@acme.test\",\"password\":\"viewer123!\"}";
    String token = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(viewerLogin))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String accessToken = token.split("\\\"accessToken\\\":\\\"")[1].split("\\\"")[0];

    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accessToken)
            .content("{\"name\":\"Test\",\"email\":\"test@example.com\",\"status\":\"ACTIVE\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminCreatesCustomerAndAuditIsLogged() throws Exception {
    String adminLogin = "{\"email\":\"admin@acme.test\",\"password\":\"admin123!\"}";
    String token = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(adminLogin))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String accessToken = token.split("\\\"accessToken\\\":\\\"")[1].split("\\\"")[0];

    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accessToken)
            .content("{\"name\":\"Audit Corp\",\"email\":\"audit@example.com\",\"status\":\"ACTIVE\"}"))
        .andExpect(status().isOk())
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(containsString("Audit Corp")));

    assertFalse(auditLogRepository.findAll().isEmpty());
  }
}
