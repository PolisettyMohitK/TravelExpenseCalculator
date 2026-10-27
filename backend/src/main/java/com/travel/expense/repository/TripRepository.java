package com.travel.expense.repository;

import com.travel.expense.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAllByOrderByCreatedAtDesc();
}
