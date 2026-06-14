package bg.softuni.gymbooking.config;

import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.Trainer;
import bg.softuni.gymbooking.entity.User;
import bg.softuni.gymbooking.entity.enums.ClassStatus;
import bg.softuni.gymbooking.entity.enums.Role;
import bg.softuni.gymbooking.repository.FitnessClassRepository;
import bg.softuni.gymbooking.repository.TrainerRepository;
import bg.softuni.gymbooking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds demo accounts, trainers, and classes on first startup so the
 * application is immediately demoable. Technical only — not counted as a
 * domain functionality.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final FitnessClassRepository fitnessClassRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           TrainerRepository trainerRepository,
                           FitnessClassRepository fitnessClassRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.fitnessClassRepository = fitnessClassRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        createUser("trainer", "trainer123", Role.TRAINER);
        createUser("member", "member123", Role.MEMBER);

        Trainer anna = createTrainer("Anna Petrova", "Yoga & Mobility",
                "Certified yoga instructor with 8 years of experience.");
        Trainer ivan = createTrainer("Ivan Dimitrov", "CrossFit & Strength",
                "Strength coach focused on functional training.");
        Trainer maria = createTrainer("Maria Georgieva", "Pilates",
                "Pilates specialist helping members build core stability.");

        createClass("Morning Vinyasa Flow", "An energising flow to start the day.",
                LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0),
                60, 15, anna);
        createClass("CrossFit WOD", "High-intensity workout of the day.",
                LocalDateTime.now().plusDays(2).withHour(18).withMinute(30).withSecond(0).withNano(0),
                45, 12, ivan);
        createClass("Core Pilates", "Mat Pilates focused on core strength.",
                LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0),
                50, 10, maria);
    }

    private void createUser(String username, String rawPassword, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        userRepository.save(user);
    }

    private Trainer createTrainer(String name, String specialty, String bio) {
        Trainer trainer = new Trainer();
        trainer.setName(name);
        trainer.setSpecialty(specialty);
        trainer.setBio(bio);
        return trainerRepository.save(trainer);
    }

    private void createClass(String title, String description, LocalDateTime startTime,
                             int durationMinutes, int capacity, Trainer trainer) {
        FitnessClass fitnessClass = new FitnessClass();
        fitnessClass.setTitle(title);
        fitnessClass.setDescription(description);
        fitnessClass.setStartTime(startTime);
        fitnessClass.setDurationMinutes(durationMinutes);
        fitnessClass.setCapacity(capacity);
        fitnessClass.setStatus(ClassStatus.PUBLISHED);
        fitnessClass.setTrainer(trainer);
        fitnessClassRepository.save(fitnessClass);
    }
}
