package booking.exception;

import booking.constant.enums.ErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationException extends RuntimeException {
    int code;
    String message;
    HttpStatus httpStatus;
    ErrorType errorType;

    public ApplicationException(int code, String message, HttpStatus httpStatus, ErrorType errorType){
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorType = errorType;
    }

}
