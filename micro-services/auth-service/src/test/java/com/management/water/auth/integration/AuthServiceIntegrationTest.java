package com.management.water.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.auth.dto.LoginRequest;
import com.management.water.auth.dto.UserCreateRequest;
import com.management.water.auth.entity.Role;
import com.management.water.auth.entity.User;
import com.management.water.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:auth_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
public class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUserLifecycleIntegration() throws Exception {
        // 1. Create a resident user via admin endpoint
        UserCreateRequest registerRequest = new UserCreateRequest();
        registerRequest.setUsername("integration_user");
        registerRequest.setPassword("securePassword123");
        registerRequest.setApartmentId(50L);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.role").value("RESIDENT"))
                .andExpect(jsonPath("$.apartmentId").value(50L));

        // Verify it exists in database
        User savedUser = userRepository.findByUsername("integration_user").orElse(null);
        assertNotNull(savedUser);
        assertEquals(Role.RESIDENT, savedUser.getRole());

        // 2. Perform authentication/login to generate a real JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("integration_user");
        loginRequest.setPassword("securePassword123");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("RESIDENT"))
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, String> loginResponse = objectMapper.readValue(responseBody, Map.class);
        String jwtToken = loginResponse.get("token");
        assertNotNull(jwtToken);

        // 3. Retrieve user profile details using the returned JWT token (without WithMockUser)
        mockMvc.perform(get("/users/username/integration_user")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.role").value("RESIDENT"))
                .andExpect(jsonPath("$.apartmentId").value(50L));
    }
}
