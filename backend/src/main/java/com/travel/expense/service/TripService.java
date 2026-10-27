package com.travel.expense.service;

import com.travel.expense.dto.TripRequest;
import com.travel.expense.dto.TripResponse;
import com.travel.expense.exception.TripNotFoundException;
import com.travel.expense.model.Trip;
import com.travel.expense.repository.TripRepository;
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

    public TripResponse create(TripRequest request) {
        Trip trip = new Trip(
                request.destination().trim(),
                request.days(),
                request.travellers(),
                request.travelCost(),
                request.accommodationCost(),
                request.foodCost()
        );
        return toResponse(repository.save(trip));
    }

    public List<TripResponse> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(TripService::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TripNotFoundException(id);
        }
        repository.deleteById(id);
    }

    static BigDecimal totalCost(BigDecimal travel, BigDecimal accommodation, BigDecimal food) {
        return travel.add(accommodation).add(food).setScale(2, RoundingMode.HALF_UP);
    }

    static BigDecimal costPerTraveller(BigDecimal total, int travellers) {
        return total.divide(BigDecimal.valueOf(travellers), 2, RoundingMode.HALF_UP);
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
