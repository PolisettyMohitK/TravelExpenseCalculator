package com.travel.expense.controller;

import com.travel.expense.config.AuthInterceptor;
import com.travel.expense.dto.TripRequest;
import com.travel.expense.dto.TripResponse;
import com.travel.expense.model.User;
import com.travel.expense.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService service;

    public TripController(TripService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse create(@RequestAttribute(AuthInterceptor.CURRENT_USER) User user,
                               @Valid @RequestBody TripRequest request) {
        return service.create(user, request);
    }

    @GetMapping
    public List<TripResponse> findAll(@RequestAttribute(AuthInterceptor.CURRENT_USER) User user) {
        return service.findAll(user);
    }

    @GetMapping("/{id}")
    public TripResponse findById(@RequestAttribute(AuthInterceptor.CURRENT_USER) User user,
                                 @PathVariable Long id) {
        return service.findById(user, id);
    }

    @PutMapping("/{id}")
    public TripResponse update(@RequestAttribute(AuthInterceptor.CURRENT_USER) User user,
                               @PathVariable Long id,
                               @Valid @RequestBody TripRequest request) {
        return service.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestAttribute(AuthInterceptor.CURRENT_USER) User user,
                       @PathVariable Long id) {
        service.delete(user, id);
    }
}
