package booking.exception;

import booking.constant.enums.ErrorType;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String message, HttpStatus status) {
        super(4001, message, status, ErrorType.EXTERNAL);
    }
}
