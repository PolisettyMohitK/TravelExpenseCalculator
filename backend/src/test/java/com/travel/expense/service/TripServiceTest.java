package com.travel.expense.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TripServiceTest {

    @Test
    void totalCostIsSumOfAllExpenses() {
        BigDecimal total = TripService.totalCost(
                new BigDecimal("500"), new BigDecimal("1200.50"), new BigDecimal("300.25"));

        assertThat(total).isEqualByComparingTo("2000.75");
    }

    @Test
    void costPerTravellerDividesTotalEvenly() {
        assertThat(TripService.costPerTraveller(new BigDecimal("900.00"), 3))
                .isEqualByComparingTo("300.00");
    }

    @Test
    void costPerTravellerRoundsToTwoDecimals() {
        assertThat(TripService.costPerTraveller(new BigDecimal("100.00"), 3))
                .isEqualByComparingTo("33.33");
    }

    @Test
    void zeroExpensesGiveZeroTotal() {
        BigDecimal total = TripService.totalCost(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(total).isEqualByComparingTo("0");
        assertThat(TripService.costPerTraveller(total, 4)).isEqualByComparingTo("0");
    }
}
