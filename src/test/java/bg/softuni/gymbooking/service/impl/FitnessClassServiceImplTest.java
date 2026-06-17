package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.dto.FitnessClassRequest;
import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.Trainer;
import bg.softuni.gymbooking.entity.enums.ClassStatus;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.repository.FitnessClassRepository;
import bg.softuni.gymbooking.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FitnessClassServiceImplTest {

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private FitnessClassServiceImpl fitnessClassService;

    private FitnessClassRequest request(UUID trainerId) {
        FitnessClassRequest request = new FitnessClassRequest();
        request.setTitle("Morning Yoga");
        request.setDescription("Relaxing flow");
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setDurationMinutes(60);
        request.setCapacity(10);
        request.setTrainerId(trainerId);
        return request;
    }

    @Test
    void create_setsStatusPublishedAndResolvesTrainer() {
        UUID trainerId = UUID.randomUUID();
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);
        when(trainerService.getById(trainerId)).thenReturn(trainer);
        when(fitnessClassRepository.save(any(FitnessClass.class))).thenAnswer(i -> i.getArgument(0));

        FitnessClass result = fitnessClassService.create(request(trainerId));

        assertThat(result.getStatus()).isEqualTo(ClassStatus.PUBLISHED);
        assertThat(result.getTrainer()).isSameAs(trainer);
        assertThat(result.getTitle()).isEqualTo("Morning Yoga");
        assertThat(result.getCapacity()).isEqualTo(10);
    }

    @Test
    void update_appliesNewValues() {
        UUID id = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        FitnessClass existing = new FitnessClass();
        existing.setId(id);
        existing.setStatus(ClassStatus.PUBLISHED);
        existing.setTitle("Old");
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);
        when(fitnessClassRepository.findById(id)).thenReturn(Optional.of(existing));
        when(trainerService.getById(trainerId)).thenReturn(trainer);
        when(fitnessClassRepository.save(any(FitnessClass.class))).thenAnswer(i -> i.getArgument(0));

        FitnessClass result = fitnessClassService.update(id, request(trainerId));

        assertThat(result.getTitle()).isEqualTo("Morning Yoga");
        assertThat(result.getStatus()).isEqualTo(ClassStatus.PUBLISHED);
    }

    @Test
    void toggleStatus_flipsBetweenPublishedAndCancelled() {
        UUID id = UUID.randomUUID();
        FitnessClass fitnessClass = new FitnessClass();
        fitnessClass.setId(id);
        fitnessClass.setStatus(ClassStatus.PUBLISHED);
        when(fitnessClassRepository.findById(id)).thenReturn(Optional.of(fitnessClass));
        when(fitnessClassRepository.save(any(FitnessClass.class))).thenAnswer(i -> i.getArgument(0));

        assertThat(fitnessClassService.toggleStatus(id).getStatus()).isEqualTo(ClassStatus.CANCELLED);
        assertThat(fitnessClassService.toggleStatus(id).getStatus()).isEqualTo(ClassStatus.PUBLISHED);
    }

    @Test
    void delete_removesClass() {
        UUID id = UUID.randomUUID();
        FitnessClass fitnessClass = new FitnessClass();
        fitnessClass.setId(id);
        when(fitnessClassRepository.findById(id)).thenReturn(Optional.of(fitnessClass));

        fitnessClassService.delete(id);

        verify(fitnessClassRepository).delete(fitnessClass);
    }

    @Test
    void getById_whenMissing_throws() {
        UUID id = UUID.randomUUID();
        when(fitnessClassRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fitnessClassService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
