package bg.softuni.gymbooking.web;

import bg.softuni.gymbooking.dto.LoginRequest;
import bg.softuni.gymbooking.dto.RegisterRequest;
import bg.softuni.gymbooking.entity.User;
import bg.softuni.gymbooking.exception.InvalidCredentialsException;
import bg.softuni.gymbooking.exception.UserAlreadyExistsException;
import bg.softuni.gymbooking.security.CurrentUser;
import bg.softuni.gymbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final CurrentUser currentUser;

    public AuthController(UserService userService, CurrentUser currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (request.getPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(request);
        } catch (UserAlreadyExistsException ex) {
            bindingResult.rejectValue("username", "username.exists", ex.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("success", "Registration successful. Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest request,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        try {
            User user = userService.authenticate(request);
            currentUser.login(user);
        } catch (InvalidCredentialsException ex) {
            bindingResult.reject("auth.failed", ex.getMessage());
            return "auth/login";
        }
        return "redirect:/classes";
    }

    @PostMapping("/logout")
    public String logout() {
        currentUser.logout();
        return "redirect:/";
    }
}
