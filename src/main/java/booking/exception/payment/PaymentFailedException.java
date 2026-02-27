package booking.exception.payment;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PaymentFailedException extends ApplicationException {
    public PaymentFailedException(String message, HttpStatus status) {
        super(8003, message, status, ErrorType.INTERNAL);
    }
}
