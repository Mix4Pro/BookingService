package booking.exception.roomException;

import booking.constant.enums.ErrorType;
import booking.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class RoomNumberExistsException extends ApplicationException {
    public RoomNumberExistsException(String message, HttpStatus status){
        super(1001,message, status,ErrorType.EXTERNAL);
    }
}
