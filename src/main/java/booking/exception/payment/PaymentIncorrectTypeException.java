package booking.exception.payment;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PaymentIncorrectTypeException extends ApplicationException {
    public PaymentIncorrectTypeException(String message, HttpStatus status) {
        super(8001,message,status, ErrorType.EXTERNAL);
    }
}
