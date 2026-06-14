package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.entity.Booking;
import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.User;
import bg.softuni.gymbooking.entity.enums.BookingStatus;
import bg.softuni.gymbooking.entity.enums.ClassStatus;
import bg.softuni.gymbooking.exception.BookingNotAllowedException;
import bg.softuni.gymbooking.exception.ClassFullException;
import bg.softuni.gymbooking.exception.DuplicateBookingException;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.repository.BookingRepository;
import bg.softuni.gymbooking.service.BookingService;
import bg.softuni.gymbooking.service.FitnessClassService;
import bg.softuni.gymbooking.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FitnessClassService fitnessClassService;
    private final UserService userService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              FitnessClassService fitnessClassService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.fitnessClassService = fitnessClassService;
        this.userService = userService;
    }

    @Override
    public Booking book(UUID classId, UUID userId) {
        User user = userService.getById(userId);
        FitnessClass fitnessClass = fitnessClassService.getById(classId);

        validateBookable(user, fitnessClass);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFitnessClass(fitnessClass);
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> findMyBookings(UUID userId) {
        User user = userService.getById(userId);
        return bookingRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Booking reschedule(UUID bookingId, UUID newClassId, UUID userId) {
        Booking booking = getOwnedBooking(bookingId, userId);
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new BookingNotAllowedException("Only active bookings can be rescheduled");
        }

        FitnessClass newClass = fitnessClassService.getById(newClassId);
        validateBookable(booking.getUser(), newClass);

        booking.setFitnessClass(newClass);
        return bookingRepository.save(booking);
    }

    @Override
    public void cancel(UUID bookingId, UUID userId) {
        Booking booking = getOwnedBooking(bookingId, userId);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private Booking getOwnedBooking(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Booking not found");
        }
        return booking;
    }

    private void validateBookable(User user, FitnessClass fitnessClass) {
        if (fitnessClass.getStatus() != ClassStatus.PUBLISHED) {
            throw new BookingNotAllowedException("This class is not open for booking");
        }
        if (fitnessClass.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BookingNotAllowedException("This class has already started");
        }
        if (bookingRepository.existsByUserAndFitnessClassAndStatus(user, fitnessClass, BookingStatus.ACTIVE)) {
            throw new DuplicateBookingException("You have already booked this class");
        }
        long activeBookings = bookingRepository.countByFitnessClassAndStatus(fitnessClass, BookingStatus.ACTIVE);
        if (activeBookings >= fitnessClass.getCapacity()) {
            throw new ClassFullException("This class is fully booked");
        }
    }
}
