package com.travel.expense.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TripResponse(
        Long id,
        String destination,
        int days,
        int travellers,
        BigDecimal travelCost,
        BigDecimal accommodationCost,
        BigDecimal foodCost,
        BigDecimal totalCost,
        BigDecimal costPerTraveller,
        Instant createdAt
) {
}
