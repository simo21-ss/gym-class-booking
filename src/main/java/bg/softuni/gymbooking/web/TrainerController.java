package bg.softuni.gymbooking.web;

import bg.softuni.gymbooking.dto.TrainerRequest;
import bg.softuni.gymbooking.entity.Trainer;
import bg.softuni.gymbooking.entity.enums.Role;
import bg.softuni.gymbooking.security.RequireRole;
import bg.softuni.gymbooking.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    @RequireRole(Role.TRAINER)
    public String list(Model model) {
        model.addAttribute("trainers", trainerService.findAll());
        return "trainers/list";
    }

    @GetMapping("/new")
    @RequireRole(Role.TRAINER)
    public String createForm(Model model) {
        if (!model.containsAttribute("trainerRequest")) {
            model.addAttribute("trainerRequest", new TrainerRequest());
        }
        model.addAttribute("editing", false);
        return "trainers/form";
    }

    @PostMapping
    @RequireRole(Role.TRAINER)
    public String create(@Valid @ModelAttribute("trainerRequest") TrainerRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", false);
            return "trainers/form";
        }
        trainerService.create(request);
        return "redirect:/trainers";
    }

    @GetMapping("/{id}/edit")
    @RequireRole(Role.TRAINER)
    public String editForm(@PathVariable UUID id, Model model) {
        Trainer trainer = trainerService.getById(id);
        if (!model.containsAttribute("trainerRequest")) {
            model.addAttribute("trainerRequest", toRequest(trainer));
        }
        model.addAttribute("editing", true);
        model.addAttribute("trainerId", id);
        return "trainers/form";
    }

    @PutMapping("/{id}")
    @RequireRole(Role.TRAINER)
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("trainerRequest") TrainerRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", true);
            model.addAttribute("trainerId", id);
            return "trainers/form";
        }
        trainerService.update(id, request);
        return "redirect:/trainers";
    }

    @DeleteMapping("/{id}")
    @RequireRole(Role.TRAINER)
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        trainerService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Trainer deleted.");
        return "redirect:/trainers";
    }

    private TrainerRequest toRequest(Trainer trainer) {
        TrainerRequest request = new TrainerRequest();
        request.setName(trainer.getName());
        request.setSpecialty(trainer.getSpecialty());
        request.setBio(trainer.getBio());
        return request;
    }
}
