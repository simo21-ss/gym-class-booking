package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.dto.FitnessClassRequest;
import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.Trainer;
import bg.softuni.gymbooking.entity.enums.ClassStatus;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.repository.FitnessClassRepository;
import bg.softuni.gymbooking.service.FitnessClassService;
import bg.softuni.gymbooking.service.TrainerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FitnessClassServiceImpl implements FitnessClassService {

    private final FitnessClassRepository fitnessClassRepository;
    private final TrainerService trainerService;

    public FitnessClassServiceImpl(FitnessClassRepository fitnessClassRepository, TrainerService trainerService) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.trainerService = trainerService;
    }

    @Override
    public List<FitnessClass> findAll() {
        return fitnessClassRepository.findAllByOrderByStartTimeAsc();
    }

    @Override
    public List<FitnessClass> findPublished() {
        return fitnessClassRepository.findAllByStatusOrderByStartTimeAsc(ClassStatus.PUBLISHED);
    }

    @Override
    public FitnessClass getById(UUID id) {
        return fitnessClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fitness class not found"));
    }

    @Override
    public FitnessClass create(FitnessClassRequest request) {
        FitnessClass fitnessClass = new FitnessClass();
        fitnessClass.setStatus(ClassStatus.PUBLISHED);
        applyRequest(fitnessClass, request);
        return fitnessClassRepository.save(fitnessClass);
    }

    @Override
    public FitnessClass update(UUID id, FitnessClassRequest request) {
        FitnessClass fitnessClass = getById(id);
        applyRequest(fitnessClass, request);
        return fitnessClassRepository.save(fitnessClass);
    }

    @Override
    public void delete(UUID id) {
        FitnessClass fitnessClass = getById(id);
        fitnessClassRepository.delete(fitnessClass);
    }

    @Override
    public FitnessClass toggleStatus(UUID id) {
        FitnessClass fitnessClass = getById(id);
        ClassStatus next = fitnessClass.getStatus() == ClassStatus.PUBLISHED
                ? ClassStatus.CANCELLED
                : ClassStatus.PUBLISHED;
        fitnessClass.setStatus(next);
        return fitnessClassRepository.save(fitnessClass);
    }

    private void applyRequest(FitnessClass fitnessClass, FitnessClassRequest request) {
        Trainer trainer = trainerService.getById(request.getTrainerId());
        fitnessClass.setTitle(request.getTitle());
        fitnessClass.setDescription(request.getDescription());
        fitnessClass.setStartTime(request.getStartTime());
        fitnessClass.setDurationMinutes(request.getDurationMinutes());
        fitnessClass.setCapacity(request.getCapacity());
        fitnessClass.setTrainer(trainer);
    }
}
