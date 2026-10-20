package com.example.gameflixauth;

import com.example.gameflixauth.model.User;
import com.example.gameflixauth.repository.UserRepository;
import com.example.gameflixauth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties =
        "jwt.secret=test-secret-for-gameflix-tests-12345678901234567890")
@AutoConfigureMockMvc
class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void registerWorks() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Cody",
                                  "email": "cody@test.com",
                                  "password": "Password123!"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("cody@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void duplicateEmailReturns409() throws Exception {

        String user = """
                {
                  "username": "Cody",
                  "email": "cody@test.com",
                  "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isConflict());
    }

    @Test
    void loginWorks() throws Exception {

        User user = new User(
                "Cody",
                "cody@test.com",
                passwordEncoder.encode("Password123!")
        );

        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "cody@test.com",
                                  "password": "Password123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void wrongPasswordReturns401() throws Exception {

        User user = new User(
                "Cody",
                "cody@test.com",
                passwordEncoder.encode("Password123!")
        );

        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "cody@test.com",
                                  "password": "WrongPassword"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validTokenCanAccessMe() throws Exception {

        User user = new User(
                "Cody",
                "cody@test.com",
                passwordEncoder.encode("Password123!")
        );

        userRepository.save(user);

        String token =
                jwtService.generateToken("cody@test.com");

        mockMvc.perform(get("/api/users/me")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value("cody@test.com"));
    }

    @Test
    void badOrMissingTokenReturns401() throws Exception {

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/me")
                        .header(
                                "Authorization",
                                "Bearer fake-token"))
                .andExpect(status().isUnauthorized());
    }
}