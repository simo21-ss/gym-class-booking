package bg.softuni.gymbooking.repository;

import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.enums.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FitnessClassRepository extends JpaRepository<FitnessClass, UUID> {

    List<FitnessClass> findAllByOrderByStartTimeAsc();

    List<FitnessClass> findAllByStatusOrderByStartTimeAsc(ClassStatus status);
}
