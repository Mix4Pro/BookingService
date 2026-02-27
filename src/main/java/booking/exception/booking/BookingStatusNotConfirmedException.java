package booking.exception.booking;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class BookingStatusNotConfirmedException extends ApplicationException {
    public BookingStatusNotConfirmedException(String message, HttpStatus status) {
        super(3005, message, status, ErrorType.EXTERNAL);
    }
}
