package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.entity.Trainer;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.repository.TrainerRepository;
import bg.softuni.gymbooking.service.TrainerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
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
}
