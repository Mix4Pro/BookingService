package booking.exception.room;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class RoomNotFoundException extends ApplicationException {
    public RoomNotFoundException(String message, HttpStatus status){
        super(1003,message,status, ErrorType.EXTERNAL);
    }
}
