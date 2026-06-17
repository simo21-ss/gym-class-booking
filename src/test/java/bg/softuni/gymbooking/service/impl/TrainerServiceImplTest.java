package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.dto.TrainerRequest;
import bg.softuni.gymbooking.entity.Trainer;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.exception.TrainerInUseException;
import bg.softuni.gymbooking.repository.FitnessClassRepository;
import bg.softuni.gymbooking.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private TrainerRequest request() {
        TrainerRequest request = new TrainerRequest();
        request.setName("Anna Petrova");
        request.setSpecialty("Yoga");
        request.setBio("Certified instructor");
        return request;
    }

    @Test
    void create_savesTrainerWithRequestValues() {
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer result = trainerService.create(request());

        assertThat(result.getName()).isEqualTo("Anna Petrova");
        assertThat(result.getSpecialty()).isEqualTo("Yoga");
        assertThat(result.getBio()).isEqualTo("Certified instructor");
    }

    @Test
    void update_appliesNewValues() {
        UUID id = UUID.randomUUID();
        Trainer existing = new Trainer();
        existing.setId(id);
        existing.setName("Old Name");
        when(trainerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer result = trainerService.update(id, request());

        assertThat(result.getName()).isEqualTo("Anna Petrova");
        assertThat(result.getSpecialty()).isEqualTo("Yoga");
    }

    @Test
    void delete_whenTrainerHasNoClasses_removesTrainer() {
        UUID id = UUID.randomUUID();
        Trainer trainer = new Trainer();
        trainer.setId(id);
        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));
        when(fitnessClassRepository.existsByTrainerId(id)).thenReturn(false);

        trainerService.delete(id);

        verify(trainerRepository).delete(trainer);
    }

    @Test
    void delete_whenTrainerHasClasses_throwsAndKeepsTrainer() {
        UUID id = UUID.randomUUID();
        Trainer trainer = new Trainer();
        trainer.setId(id);
        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));
        when(fitnessClassRepository.existsByTrainerId(id)).thenReturn(true);

        assertThatThrownBy(() -> trainerService.delete(id))
                .isInstanceOf(TrainerInUseException.class);
        verify(trainerRepository, never()).delete(any());
    }

    @Test
    void getById_whenMissing_throws() {
        UUID id = UUID.randomUUID();
        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
