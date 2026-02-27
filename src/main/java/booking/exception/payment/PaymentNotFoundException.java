package booking.exception.payment;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends ApplicationException {
    public PaymentNotFoundException(String message, HttpStatus status) {
        super(8002, message, status, ErrorType.EXTERNAL);
    }
}
