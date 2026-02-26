package booking.exception.booking;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class BookingStatusAlterationException extends ApplicationException {
    public BookingStatusAlterationException(String message, HttpStatus status) {
        super(3004,message,status, ErrorType.EXTERNAL);
    }
}
