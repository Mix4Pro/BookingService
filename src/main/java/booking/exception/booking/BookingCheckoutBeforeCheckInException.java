package booking.exception.booking;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class BookingCheckoutBeforeCheckInException extends ApplicationException {
    public BookingCheckoutBeforeCheckInException(String message, HttpStatus status) {
        super(3003, message, status, ErrorType.VALIDATION);
    }
}
