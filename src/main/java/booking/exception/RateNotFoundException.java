package booking.exception;

import booking.constant.enums.ErrorType;
import org.springframework.http.HttpStatus;

public class RateNotFoundException extends ApplicationException {
    public RateNotFoundException(String message, HttpStatus status) {
        super(5001, message, status, ErrorType.EXTERNAL);
    }
}
