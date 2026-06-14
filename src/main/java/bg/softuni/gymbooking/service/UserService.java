package bg.softuni.gymbooking.service;

import bg.softuni.gymbooking.dto.LoginRequest;
import bg.softuni.gymbooking.dto.RegisterRequest;
import bg.softuni.gymbooking.entity.User;

import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);

    User authenticate(LoginRequest request);

    User getById(UUID id);
}
