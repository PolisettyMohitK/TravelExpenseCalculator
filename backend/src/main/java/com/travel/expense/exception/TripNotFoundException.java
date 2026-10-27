package com.travel.expense.exception;

public class TripNotFoundException extends RuntimeException {

    public TripNotFoundException(Long id) {
        super("Trip " + id + " not found");
    }
}
