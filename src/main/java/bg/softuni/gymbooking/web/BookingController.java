package bg.softuni.gymbooking.web;

import bg.softuni.gymbooking.entity.enums.Role;
import bg.softuni.gymbooking.security.CurrentUser;
import bg.softuni.gymbooking.security.RequireRole;
import bg.softuni.gymbooking.service.BookingService;
import bg.softuni.gymbooking.service.FitnessClassService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final FitnessClassService fitnessClassService;
    private final CurrentUser currentUser;

    public BookingController(BookingService bookingService,
                             FitnessClassService fitnessClassService,
                             CurrentUser currentUser) {
        this.bookingService = bookingService;
        this.fitnessClassService = fitnessClassService;
        this.currentUser = currentUser;
    }

    @GetMapping("/bookings")
    @RequireRole(Role.MEMBER)
    public String myBookings(Model model) {
        model.addAttribute("bookings", bookingService.findMyBookings(currentUser.getId()));
        model.addAttribute("publishedClasses", fitnessClassService.findPublished());
        return "bookings/list";
    }

    @PostMapping("/classes/{id}/book")
    @RequireRole(Role.MEMBER)
    public String book(@PathVariable("id") UUID classId, RedirectAttributes redirectAttributes) {
        bookingService.book(classId, currentUser.getId());
        redirectAttributes.addFlashAttribute("success", "Class booked successfully.");
        return "redirect:/bookings";
    }

    @PutMapping("/bookings/{id}/reschedule")
    @RequireRole(Role.MEMBER)
    public String reschedule(@PathVariable UUID id,
                             @RequestParam UUID newClassId,
                             RedirectAttributes redirectAttributes) {
        bookingService.reschedule(id, newClassId, currentUser.getId());
        redirectAttributes.addFlashAttribute("success", "Booking rescheduled.");
        return "redirect:/bookings";
    }

    @DeleteMapping("/bookings/{id}")
    @RequireRole(Role.MEMBER)
    public String cancel(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        bookingService.cancel(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("success", "Booking cancelled.");
        return "redirect:/bookings";
    }
}
