package booking.exception.http;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PaymentServerException extends ApplicationException {
    public PaymentServerException(String message, HttpStatus status) {
        super(7002,message,status, ErrorType.INTERNAL);
    }
}
