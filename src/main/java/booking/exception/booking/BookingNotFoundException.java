package booking.exception.booking;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends ApplicationException {
    public BookingNotFoundException(String message, HttpStatus status) {
        super(3001, message, status, ErrorType.EXTERNAL);
    }
}
