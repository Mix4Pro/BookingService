package booking.exception.booking;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class RoomStatusNotHandleException extends ApplicationException {
    public RoomStatusNotHandleException(String message, HttpStatus status) {
        super(3002,message,status,ErrorType.EXTERNAL);
    }
}
