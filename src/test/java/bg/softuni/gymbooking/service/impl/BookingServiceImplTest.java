package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.entity.Booking;
import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.User;
import bg.softuni.gymbooking.entity.enums.BookingStatus;
import bg.softuni.gymbooking.entity.enums.ClassStatus;
import bg.softuni.gymbooking.entity.enums.Role;
import bg.softuni.gymbooking.exception.BookingNotAllowedException;
import bg.softuni.gymbooking.exception.ClassFullException;
import bg.softuni.gymbooking.exception.DuplicateBookingException;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.repository.BookingRepository;
import bg.softuni.gymbooking.service.FitnessClassService;
import bg.softuni.gymbooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FitnessClassService fitnessClassService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user(UUID id) {
        User user = new User();
        user.setId(id);
        user.setUsername("member");
        user.setRole(Role.MEMBER);
        return user;
    }

    private FitnessClass publishedClass(UUID id, int capacity, LocalDateTime startTime) {
        FitnessClass fitnessClass = new FitnessClass();
        fitnessClass.setId(id);
        fitnessClass.setStatus(ClassStatus.PUBLISHED);
        fitnessClass.setCapacity(capacity);
        fitnessClass.setStartTime(startTime);
        return fitnessClass;
    }

    @Test
    void book_withAvailableSpot_createsActiveBooking() {
        UUID uid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        User user = user(uid);
        FitnessClass fitnessClass = publishedClass(cid, 10, LocalDateTime.now().plusDays(1));
        when(userService.getById(uid)).thenReturn(user);
        when(fitnessClassService.getById(cid)).thenReturn(fitnessClass);
        when(bookingRepository.existsByUserAndFitnessClassAndStatus(user, fitnessClass, BookingStatus.ACTIVE))
                .thenReturn(false);
        when(bookingRepository.countByFitnessClassAndStatus(fitnessClass, BookingStatus.ACTIVE)).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking booking = bookingService.book(cid, uid);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(booking.getUser()).isSameAs(user);
        assertThat(booking.getFitnessClass()).isSameAs(fitnessClass);
        assertThat(booking.getCreatedAt()).isNotNull();
    }

    @Test
    void book_whenClassNotPublished_throws() {
        UUID uid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        FitnessClass fitnessClass = publishedClass(cid, 10, LocalDateTime.now().plusDays(1));
        fitnessClass.setStatus(ClassStatus.CANCELLED);
        when(userService.getById(uid)).thenReturn(user(uid));
        when(fitnessClassService.getById(cid)).thenReturn(fitnessClass);

        assertThatThrownBy(() -> bookingService.book(cid, uid))
                .isInstanceOf(BookingNotAllowedException.class);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void book_whenClassInPast_throws() {
        UUID uid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        FitnessClass fitnessClass = publishedClass(cid, 10, LocalDateTime.now().minusDays(1));
        when(userService.getById(uid)).thenReturn(user(uid));
        when(fitnessClassService.getById(cid)).thenReturn(fitnessClass);

        assertThatThrownBy(() -> bookingService.book(cid, uid))
                .isInstanceOf(BookingNotAllowedException.class);
    }

    @Test
    void book_whenAlreadyBooked_throws() {
        UUID uid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        User user = user(uid);
        FitnessClass fitnessClass = publishedClass(cid, 10, LocalDateTime.now().plusDays(1));
        when(userService.getById(uid)).thenReturn(user);
        when(fitnessClassService.getById(cid)).thenReturn(fitnessClass);
        when(bookingRepository.existsByUserAndFitnessClassAndStatus(user, fitnessClass, BookingStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> bookingService.book(cid, uid))
                .isInstanceOf(DuplicateBookingException.class);
    }

    @Test
    void book_whenClassFull_throws() {
        UUID uid = UUID.randomUUID();
        UUID cid = UUID.randomUUID();
        User user = user(uid);
        FitnessClass fitnessClass = publishedClass(cid, 5, LocalDateTime.now().plusDays(1));
        when(userService.getById(uid)).thenReturn(user);
        when(fitnessClassService.getById(cid)).thenReturn(fitnessClass);
        when(bookingRepository.existsByUserAndFitnessClassAndStatus(user, fitnessClass, BookingStatus.ACTIVE))
                .thenReturn(false);
        when(bookingRepository.countByFitnessClassAndStatus(fitnessClass, BookingStatus.ACTIVE)).thenReturn(5L);

        assertThatThrownBy(() -> bookingService.book(cid, uid))
                .isInstanceOf(ClassFullException.class);
    }

    @Test
    void cancel_setsStatusCancelled() {
        UUID uid = UUID.randomUUID();
        UUID bid = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bid);
        booking.setUser(user(uid));
        booking.setStatus(BookingStatus.ACTIVE);
        when(bookingRepository.findById(bid)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        bookingService.cancel(bid, uid);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancel_whenBookingBelongsToAnotherUser_throws() {
        UUID uid = UUID.randomUUID();
        UUID bid = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bid);
        booking.setUser(user(UUID.randomUUID()));
        booking.setStatus(BookingStatus.ACTIVE);
        when(bookingRepository.findById(bid)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancel(bid, uid))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void reschedule_movesBookingToNewClass() {
        UUID uid = UUID.randomUUID();
        UUID bid = UUID.randomUUID();
        UUID newCid = UUID.randomUUID();
        User user = user(uid);
        Booking booking = new Booking();
        booking.setId(bid);
        booking.setUser(user);
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setFitnessClass(publishedClass(UUID.randomUUID(), 10, LocalDateTime.now().plusDays(1)));
        FitnessClass newClass = publishedClass(newCid, 10, LocalDateTime.now().plusDays(2));
        when(bookingRepository.findById(bid)).thenReturn(Optional.of(booking));
        when(fitnessClassService.getById(newCid)).thenReturn(newClass);
        when(bookingRepository.existsByUserAndFitnessClassAndStatus(user, newClass, BookingStatus.ACTIVE))
                .thenReturn(false);
        when(bookingRepository.countByFitnessClassAndStatus(newClass, BookingStatus.ACTIVE)).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.reschedule(bid, newCid, uid);

        assertThat(result.getFitnessClass()).isSameAs(newClass);
    }

    @Test
    void reschedule_toSameClass_throws() {
        UUID uid = UUID.randomUUID();
        UUID bid = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bid);
        booking.setUser(user(uid));
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setFitnessClass(publishedClass(classId, 10, LocalDateTime.now().plusDays(1)));
        when(bookingRepository.findById(bid)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.reschedule(bid, classId, uid))
                .isInstanceOf(BookingNotAllowedException.class);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void findMyBookings_returnsUserBookings() {
        UUID uid = UUID.randomUUID();
        User user = user(uid);
        List<Booking> bookings = List.of(new Booking());
        when(userService.getById(uid)).thenReturn(user);
        when(bookingRepository.findAllByUserOrderByCreatedAtDesc(user)).thenReturn(bookings);

        assertThat(bookingService.findMyBookings(uid)).isEqualTo(bookings);
    }
}
