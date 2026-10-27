package com.travel.expense.controller;

import com.jayway.jsonpath.JsonPath;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        tripRepository.deleteAll();
        userRepository.deleteAll();
        token = registerUser("ravi@example.com");
    }

    @Test
    void createsTripAndReturnsCalculatedCosts() throws Exception {
        postTrip(token, tripJson("Goa", 4, 3, "600", "1500", "900"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.destination").value("Goa"))
                .andExpect(jsonPath("$.totalCost").value(3000.00))
                .andExpect(jsonPath("$.costPerTraveller").value(1000.00));
    }

    @Test
    void listsOnlyTheUsersOwnTrips() throws Exception {
        postTrip(token, tripJson("Paris", 5, 2, "800", "1000", "400"));
        String otherToken = registerUser("meera@example.com");
        postTrip(otherToken, tripJson("Tokyo", 7, 1, "900", "1400", "600"));

        mockMvc.perform(get("/api/trips").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destination").value("Paris"))
                .andExpect(jsonPath("$[0].totalCost").value(2200.00));
    }

    @Test
    void returnsTripDetails() throws Exception {
        long id = createTrip(token);

        mockMvc.perform(get("/api/trips/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Goa"));
    }

    @Test
    void updatesTripAndRecalculatesCosts() throws Exception {
        long id = createTrip(token);

        mockMvc.perform(put("/api/trips/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripJson("Goa", 5, 4, "1000", "2000", "1000")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days").value(5))
                .andExpect(jsonPath("$.totalCost").value(4000.00))
                .andExpect(jsonPath("$.costPerTraveller").value(1000.00));
    }

    @Test
    void deletesTrip() throws Exception {
        long id = createTrip(token);

        mockMvc.perform(delete("/api/trips/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/trips/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void cannotAccessAnotherUsersTrip() throws Exception {
        long id = createTrip(token);
        String otherToken = registerUser("meera@example.com");

        mockMvc.perform(get("/api/trips/" + id).header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/trips/" + id).header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsZeroDays() throws Exception {
        postTrip(token, tripJson("Goa", 0, 2, "100", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.days").value("Days must be greater than 0"));
    }

    @Test
    void rejectsZeroTravellers() throws Exception {
        postTrip(token, tripJson("Goa", 2, 0, "100", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.travellers").value("Travellers must be greater than 0"));
    }

    @Test
    void rejectsNegativeExpense() throws Exception {
        postTrip(token, tripJson("Goa", 2, 2, "-1", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.travelCost").value("Travel cost cannot be negative"));
    }

    @Test
    void rejectsBlankDestination() throws Exception {
        postTrip(token, tripJson(" ", 2, 2, "100", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.destination").value("Destination is required"));
    }

    private String registerUser(String email) throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test","email":"%s","password":"secret123"}
                                """.formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private long createTrip(String authToken) throws Exception {
        String body = postTrip(authToken, tripJson("Goa", 4, 3, "600", "1500", "900"))
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private ResultActions postTrip(String authToken, String json) throws Exception {
        return mockMvc.perform(post("/api/trips")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private static String tripJson(String destination, int days, int travellers,
                                   String travel, String accommodation, String food) {
        return """
                {"destination":"%s","days":%d,"travellers":%d,
                 "travelCost":%s,"accommodationCost":%s,"foodCost":%s}
                """.formatted(destination, days, travellers, travel, accommodation, food);
    }
}
