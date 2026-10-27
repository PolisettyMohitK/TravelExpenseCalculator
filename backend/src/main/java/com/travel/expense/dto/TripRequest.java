package com.travel.expense.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TripRequest(
        @NotBlank(message = "Destination is required")
        @Size(max = 100, message = "Destination must be at most 100 characters")
        String destination,

        @NotNull(message = "Number of days is required")
        @Min(value = 1, message = "Days must be greater than 0")
        Integer days,

        @NotNull(message = "Number of travellers is required")
        @Min(value = 1, message = "Travellers must be greater than 0")
        Integer travellers,

        @NotNull(message = "Travel cost is required")
        @PositiveOrZero(message = "Travel cost cannot be negative")
        BigDecimal travelCost,

        @NotNull(message = "Accommodation cost is required")
        @PositiveOrZero(message = "Accommodation cost cannot be negative")
        BigDecimal accommodationCost,

        @NotNull(message = "Food cost is required")
        @PositiveOrZero(message = "Food cost cannot be negative")
        BigDecimal foodCost
) {
}
