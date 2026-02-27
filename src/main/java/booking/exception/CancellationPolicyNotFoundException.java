package booking.exception;

import booking.constant.enums.ErrorType;
import org.springframework.http.HttpStatus;

public class CancellationPolicyNotFoundException extends ApplicationException {
    public CancellationPolicyNotFoundException(String message, HttpStatus status) {
        super(6001, message, status, ErrorType.EXTERNAL);
    }
}
