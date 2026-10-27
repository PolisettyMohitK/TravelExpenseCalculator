package com.travel.expense.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private int days;

    @Column(nullable = false)
    private int travellers;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal travelCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal accommodationCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal foodCost;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Trip() {
        // Required by JPA
    }

    public Trip(String destination, int days, int travellers,
                BigDecimal travelCost, BigDecimal accommodationCost, BigDecimal foodCost) {
        this.destination = destination;
        this.days = days;
        this.travellers = travellers;
        this.travelCost = travelCost;
        this.accommodationCost = accommodationCost;
        this.foodCost = foodCost;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public int getDays() {
        return days;
    }

    public int getTravellers() {
        return travellers;
    }

    public BigDecimal getTravelCost() {
        return travelCost;
    }

    public BigDecimal getAccommodationCost() {
        return accommodationCost;
    }

    public BigDecimal getFoodCost() {
        return foodCost;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
