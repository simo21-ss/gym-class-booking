package bg.softuni.gymbooking.web;

import bg.softuni.gymbooking.dto.FitnessClassRequest;
import bg.softuni.gymbooking.entity.FitnessClass;
import bg.softuni.gymbooking.entity.enums.Role;
import bg.softuni.gymbooking.security.RequireRole;
import bg.softuni.gymbooking.service.FitnessClassService;
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

import java.util.UUID;

@Controller
@RequestMapping("/classes")
public class FitnessClassController {

    private final FitnessClassService fitnessClassService;
    private final TrainerService trainerService;

    public FitnessClassController(FitnessClassService fitnessClassService, TrainerService trainerService) {
        this.fitnessClassService = fitnessClassService;
        this.trainerService = trainerService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("classes", fitnessClassService.findAll());
        return "classes/list";
    }

    @GetMapping("/new")
    @RequireRole(Role.TRAINER)
    public String createForm(Model model) {
        if (!model.containsAttribute("fitnessClassRequest")) {
            model.addAttribute("fitnessClassRequest", new FitnessClassRequest());
        }
        model.addAttribute("trainers", trainerService.findAll());
        model.addAttribute("editing", false);
        return "classes/form";
    }

    @PostMapping
    @RequireRole(Role.TRAINER)
    public String create(@Valid @ModelAttribute("fitnessClassRequest") FitnessClassRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("trainers", trainerService.findAll());
            model.addAttribute("editing", false);
            return "classes/form";
        }
        FitnessClass created = fitnessClassService.create(request);
        return "redirect:/classes/" + created.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable UUID id, Model model) {
        model.addAttribute("fitnessClass", fitnessClassService.getById(id));
        return "classes/details";
    }

    @GetMapping("/{id}/edit")
    @RequireRole(Role.TRAINER)
    public String editForm(@PathVariable UUID id, Model model) {
        FitnessClass fitnessClass = fitnessClassService.getById(id);
        if (!model.containsAttribute("fitnessClassRequest")) {
            model.addAttribute("fitnessClassRequest", toRequest(fitnessClass));
        }
        model.addAttribute("trainers", trainerService.findAll());
        model.addAttribute("editing", true);
        model.addAttribute("classId", id);
        return "classes/form";
    }

    @PutMapping("/{id}")
    @RequireRole(Role.TRAINER)
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("fitnessClassRequest") FitnessClassRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("trainers", trainerService.findAll());
            model.addAttribute("editing", true);
            model.addAttribute("classId", id);
            return "classes/form";
        }
        fitnessClassService.update(id, request);
        return "redirect:/classes/" + id;
    }

    @DeleteMapping("/{id}")
    @RequireRole(Role.TRAINER)
    public String delete(@PathVariable UUID id) {
        fitnessClassService.delete(id);
        return "redirect:/classes";
    }

    @PutMapping("/{id}/status")
    @RequireRole(Role.TRAINER)
    public String toggleStatus(@PathVariable UUID id) {
        fitnessClassService.toggleStatus(id);
        return "redirect:/classes/" + id;
    }

    private FitnessClassRequest toRequest(FitnessClass fitnessClass) {
        FitnessClassRequest request = new FitnessClassRequest();
        request.setTitle(fitnessClass.getTitle());
        request.setDescription(fitnessClass.getDescription());
        request.setStartTime(fitnessClass.getStartTime());
        request.setDurationMinutes(fitnessClass.getDurationMinutes());
        request.setCapacity(fitnessClass.getCapacity());
        request.setTrainerId(fitnessClass.getTrainer().getId());
        return request;
    }
}
