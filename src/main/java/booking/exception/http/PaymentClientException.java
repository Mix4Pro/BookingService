package booking.exception.http;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PaymentClientException extends ApplicationException {
    public PaymentClientException(String message, HttpStatus status) {
        super(7001,message,status, ErrorType.EXTERNAL);
    }
}
