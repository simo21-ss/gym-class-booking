package bg.softuni.gymbooking.exception;

public class BookingNotAllowedException extends RuntimeException {

    public BookingNotAllowedException(String message) {
        super(message);
    }
}
