package com.travel.expense.controller;

import com.travel.expense.repository.TripRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsTripAndReturnsCalculatedCosts() throws Exception {
        postTrip(tripJson("Goa", 4, 3, "600", "1500", "900"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.destination").value("Goa"))
                .andExpect(jsonPath("$.totalCost").value(3000.00))
                .andExpect(jsonPath("$.costPerTraveller").value(1000.00));
    }

    @Test
    void listsSavedTrips() throws Exception {
        postTrip(tripJson("Paris", 5, 2, "800", "1000", "400"));

        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destination").value("Paris"))
                .andExpect(jsonPath("$[0].totalCost").value(2200.00));
    }

    @Test
    void rejectsZeroDays() throws Exception {
        postTrip(tripJson("Goa", 0, 2, "100", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.days").value("Days must be greater than 0"));
    }

    @Test
    void rejectsZeroTravellers() throws Exception {
        postTrip(tripJson("Goa", 2, 0, "100", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.travellers").value("Travellers must be greater than 0"));
    }

    @Test
    void rejectsNegativeExpense() throws Exception {
        postTrip(tripJson("Goa", 2, 2, "-1", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.travelCost").value("Travel cost cannot be negative"));
    }

    @Test
    void rejectsBlankDestination() throws Exception {
        postTrip(tripJson(" ", 2, 2, "100", "100", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.destination").value("Destination is required"));
    }

    @Test
    void deletingUnknownTripReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/trips/999"))
                .andExpect(status().isNotFound());
    }

    private ResultActions postTrip(String json) throws Exception {
        return mockMvc.perform(post("/api/trips")
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
