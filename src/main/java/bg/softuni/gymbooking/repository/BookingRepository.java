package bg.softuni.gymbooking.repository;

import bg.softuni.gymbooking.entity.Booking;
import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.User;
import bg.softuni.gymbooking.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findAllByUserOrderByCreatedAtDesc(User user);

    boolean existsByUserAndFitnessClassAndStatus(User user, FitnessClass fitnessClass, BookingStatus status);

    long countByFitnessClassAndStatus(FitnessClass fitnessClass, BookingStatus status);
}
