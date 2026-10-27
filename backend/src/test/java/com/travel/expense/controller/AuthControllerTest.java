package com.travel.expense.controller;

import com.travel.expense.repository.TripRepository;
import com.travel.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String ASHA = """
            {"name":"Asha","email":"asha@example.com","password":"secret123"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearDatabase() {
        tripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registersUserAndReturnsToken() throws Exception {
        postJson("/api/auth/register", ASHA)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.name").value("Asha"))
                .andExpect(jsonPath("$.email").value("asha@example.com"));
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        postJson("/api/auth/register", ASHA);

        postJson("/api/auth/register", ASHA)
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsShortPassword() throws Exception {
        postJson("/api/auth/register", """
                {"name":"Asha","email":"asha@example.com","password":"123"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void logsInWithCorrectPassword() throws Exception {
        postJson("/api/auth/register", ASHA);

        postJson("/api/auth/login", """
                {"email":"ASHA@example.com","password":"secret123"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void rejectsWrongPassword() throws Exception {
        postJson("/api/auth/register", ASHA);

        postJson("/api/auth/login", """
                {"email":"asha@example.com","password":"wrong-password"}
                """)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void tripsRequireLogin() throws Exception {
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/trips").header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
