package bg.softuni.gymbooking.service;

import bg.softuni.gymbooking.dto.FitnessClassRequest;
import bg.softuni.gymbooking.entity.FitnessClass;

import java.util.List;
import java.util.UUID;

public interface FitnessClassService {

    List<FitnessClass> findAll();

    List<FitnessClass> findPublished();

    FitnessClass getById(UUID id);

    FitnessClass create(FitnessClassRequest request);

    FitnessClass update(UUID id, FitnessClassRequest request);

    void delete(UUID id);

    FitnessClass toggleStatus(UUID id);
}
