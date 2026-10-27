package com.travel.expense.service;

import com.travel.expense.dto.TripRequest;
import com.travel.expense.dto.TripResponse;
import com.travel.expense.exception.ApiException;
import com.travel.expense.model.Trip;
import com.travel.expense.model.User;
import com.travel.expense.repository.TripRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TripService {

    private final TripRepository repository;

    public TripService(TripRepository repository) {
        this.repository = repository;
    }

    public TripResponse create(User user, TripRequest request) {
        Trip trip = new Trip(user);
        applyRequest(trip, request);
        return toResponse(repository.save(trip));
    }

    public List<TripResponse> findAll(User user) {
        return repository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(TripService::toResponse)
                .toList();
    }

    public TripResponse findById(User user, Long id) {
        return toResponse(findOwnedTrip(user, id));
    }

    public TripResponse update(User user, Long id, TripRequest request) {
        Trip trip = findOwnedTrip(user, id);
        applyRequest(trip, request);
        return toResponse(repository.save(trip));
    }

    public void delete(User user, Long id) {
        repository.delete(findOwnedTrip(user, id));
    }

    static BigDecimal totalCost(BigDecimal travel, BigDecimal accommodation, BigDecimal food) {
        return travel.add(accommodation).add(food).setScale(2, RoundingMode.HALF_UP);
    }

    static BigDecimal costPerTraveller(BigDecimal total, int travellers) {
        return total.divide(BigDecimal.valueOf(travellers), 2, RoundingMode.HALF_UP);
    }

    // Users can only see and change their own trips
    private Trip findOwnedTrip(User user, Long id) {
        return repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Trip not found"));
    }

    private static void applyRequest(Trip trip, TripRequest request) {
        trip.update(
                request.destination().trim(),
                request.days(),
                request.travellers(),
                request.travelCost(),
                request.accommodationCost(),
                request.foodCost()
        );
    }

    private static TripResponse toResponse(Trip trip) {
        BigDecimal total = totalCost(trip.getTravelCost(), trip.getAccommodationCost(), trip.getFoodCost());
        return new TripResponse(
                trip.getId(),
                trip.getDestination(),
                trip.getDays(),
                trip.getTravellers(),
                trip.getTravelCost(),
                trip.getAccommodationCost(),
                trip.getFoodCost(),
                total,
                costPerTraveller(total, trip.getTravellers()),
                trip.getCreatedAt()
        );
    }
}
