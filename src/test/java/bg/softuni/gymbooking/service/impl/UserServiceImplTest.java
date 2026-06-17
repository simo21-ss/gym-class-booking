package bg.softuni.gymbooking.service.impl;

import bg.softuni.gymbooking.dto.LoginRequest;
import bg.softuni.gymbooking.dto.RegisterRequest;
import bg.softuni.gymbooking.entity.User;
import bg.softuni.gymbooking.entity.enums.Role;
import bg.softuni.gymbooking.exception.InvalidCredentialsException;
import bg.softuni.gymbooking.exception.ResourceNotFoundException;
import bg.softuni.gymbooking.exception.UserAlreadyExistsException;
import bg.softuni.gymbooking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("secret123");
        request.setConfirmPassword("secret123");
        request.setRole(Role.MEMBER);
        return request;
    }

    @Test
    void register_hashesPasswordAndSavesUser() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(registerRequest());

        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getPassword()).isEqualTo("HASHED");
        assertThat(result.getRole()).isEqualTo(Role.MEMBER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_whenUsernameTaken_throwsAndDoesNotSave() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest()))
                .isInstanceOf(UserAlreadyExistsException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_withValidCredentials_returnsUser() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("HASHED");
        LoginRequest login = new LoginRequest();
        login.setUsername("john");
        login.setPassword("secret123");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "HASHED")).thenReturn(true);

        assertThat(userService.authenticate(login)).isSameAs(user);
    }

    @Test
    void authenticate_withUnknownUsername_throws() {
        LoginRequest login = new LoginRequest();
        login.setUsername("ghost");
        login.setPassword("whatever");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticate(login))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticate_withWrongPassword_throws() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("HASHED");
        LoginRequest login = new LoginRequest();
        login.setUsername("john");
        login.setPassword("wrong");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASHED")).thenReturn(false);

        assertThatThrownBy(() -> userService.authenticate(login))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void getById_whenMissing_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
