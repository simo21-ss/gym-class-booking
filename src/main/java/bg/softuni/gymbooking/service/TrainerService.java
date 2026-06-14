package bg.softuni.gymbooking.service;

import bg.softuni.gymbooking.entity.Trainer;

import java.util.List;
import java.util.UUID;

public interface TrainerService {

    List<Trainer> findAll();

    Trainer getById(UUID id);
}
