package com.management.water.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.auth.dto.UserCreateRequest;
import com.management.water.auth.entity.Role;
import com.management.water.auth.entity.User;
import com.management.water.auth.jwt.JwtService;
import com.management.water.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("resident1");
        testUser.setPassword("hashedpassword");
        testUser.setRole(Role.RESIDENT);
        testUser.setApartmentId(10L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateResident_Success() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("resident1");
        request.setPassword("pass123");
        request.setApartmentId(10L);

        when(userRepository.existsByUsername("resident1")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("resident1"))
                .andExpect(jsonPath("$.role").value("RESIDENT"))
                .andExpect(jsonPath("$.apartmentId").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateResident_Conflict() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("resident1");
        request.setPassword("pass123");
        request.setApartmentId(10L);

        when(userRepository.existsByUsername("resident1")).thenReturn(true);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("resident1"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllUsers_ForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "resident1")
    public void testGetUserByUsername_Success() throws Exception {
        when(userRepository.findByUsername("resident1")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/username/resident1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("resident1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("resident1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById_NotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }
}
