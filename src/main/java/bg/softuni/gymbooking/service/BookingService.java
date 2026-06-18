package bg.softuni.gymbooking.service;

import bg.softuni.gymbooking.entity.Booking;
import bg.softuni.gymbooking.entity.FitnessClass;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    Booking book(UUID classId, UUID userId);

    List<Booking> findMyBookings(UUID userId);

    Booking reschedule(UUID bookingId, UUID newClassId, UUID userId);

    void cancel(UUID bookingId, UUID userId);

    long countActiveBookings(FitnessClass fitnessClass);
}
