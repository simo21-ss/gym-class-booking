package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.dto.TrainerRequest;
import bg.softuni.gymbooking.entity.Trainer;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.exception.TrainerInUseException;
import bg.softuni.gymbooking.repository.FitnessClassRepository;
import bg.softuni.gymbooking.repository.TrainerRepository;
import bg.softuni.gymbooking.service.TrainerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final FitnessClassRepository fitnessClassRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository, FitnessClassRepository fitnessClassRepository) {
        this.trainerRepository = trainerRepository;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }

    @Override
    public Trainer getById(UUID id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
    }

    @Override
    public Trainer create(TrainerRequest request) {
        Trainer trainer = new Trainer();
        applyRequest(trainer, request);
        return trainerRepository.save(trainer);
    }

    @Override
    public Trainer update(UUID id, TrainerRequest request) {
        Trainer trainer = getById(id);
        applyRequest(trainer, request);
        return trainerRepository.save(trainer);
    }

    @Override
    public void delete(UUID id) {
        Trainer trainer = getById(id);
        if (fitnessClassRepository.existsByTrainerId(id)) {
            throw new TrainerInUseException("Cannot delete a trainer who still has classes assigned");
        }
        trainerRepository.delete(trainer);
    }

    private void applyRequest(Trainer trainer, TrainerRequest request) {
        trainer.setName(request.getName());
        trainer.setSpecialty(request.getSpecialty());
        trainer.setBio(request.getBio());
    }
}
