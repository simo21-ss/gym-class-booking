package bg.softuni.gymbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler({
            ClassFullException.class,
            DuplicateBookingException.class,
            BookingNotAllowedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessRule(RuntimeException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
