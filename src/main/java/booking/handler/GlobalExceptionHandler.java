package booking.handler;

import booking.constant.enums.ErrorType;
import booking.dto.ErrorDto;
import booking.exception.bookingException.BookingCheckoutBeforeCheckInException;
import booking.exception.bookingException.BookingStatusAlterationException;
import booking.exception.bookingException.BookingNotFoundException;
import booking.exception.bookingException.RoomAlreadyBookedException;
import booking.exception.roomException.RoomNotFoundException;
import booking.exception.roomException.RoomNumberExistsException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ---------- ROOM EXCEPTIONS
    @ExceptionHandler(RoomNumberExistsException.class)
    public ResponseEntity<ErrorDto> handleRoomExistsException(RoomNumberExistsException ex) {
        log.error("Error : {}",ex.getMessage());
        var errorBody = ErrorDto
            .builder()
            .code(ex.getCode())
            .message(ex.getMessage())
            .errorType(ex.getErrorType())
            .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(errorBody);
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorDto> handleRoomNotFoundException (RoomNotFoundException ex) {
        ErrorDto error = ErrorDto
            .builder()
            .code(ex.getCode())
            .message(ex.getMessage())
            .errorType(ex.getErrorType())
            .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    // BOOKING EXCEPTIONS

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorDto> handleBookingNumberExistsException (BookingNotFoundException ex) {
        ErrorDto error = ErrorDto
            .builder()
            .code(ex.getCode())
            .message(ex.getMessage())
            .errorType(ex.getErrorType())
            .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ResponseEntity<ErrorDto> handleRoomAlreadyBookedException(RoomAlreadyBookedException ex) {
        ErrorDto error = ErrorDto
            .builder()
            .code(ex.getCode())
            .message(ex.getMessage())
            .errorType(ex.getErrorType())
            .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    @ExceptionHandler(BookingStatusAlterationException.class)
    public ResponseEntity<ErrorDto> handleBookingNotConfirmableException (BookingStatusAlterationException ex) {
        ErrorDto error = ErrorDto
            .builder()
            .code(ex.getCode())
            .message(ex.getMessage())
            .errorType(ex.getErrorType())
            .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    // VALIDATION EXCEPTIONS

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException (MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error->
                error.getField() + ": " + error.getDefaultMessage())
            .toList();

        ErrorDto error = ErrorDto.builder()
            .code(2001)
            .message("Validation failed")
            .errorType(ErrorType.VALIDATION)
            .validationErrors(errors)
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
            .stream()
            .map(error->
                error.getPropertyPath() + ": " + error.getMessage())
            .toList();

        ErrorDto error = ErrorDto
            .builder()
            .code(2002)
            .message("Validation error")
            .errorType(ErrorType.VALIDATION)
            .validationErrors(errors)
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();

        String message = ex.getMessage();

        if(requiredType == boolean.class) {
            message = "availability param should be either true of false";
        }


        ErrorDto error = ErrorDto
            .builder()
            .code(2003)
            .message(message)
            .errorType(ErrorType.VALIDATION)
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BookingCheckoutBeforeCheckInException.class)
    public ResponseEntity<ErrorDto> handleBookingCheckoutBeforeCheckInException (BookingCheckoutBeforeCheckInException ex) {
        ErrorDto error = ErrorDto
            .builder()
            .code(2004)
            .message(ex.getMessage())
            .errorType(ex.getErrorType())
            .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorDto> handleDateTimeParseException (DateTimeParseException ex) {
        ErrorDto error = ErrorDto.builder()
            .code(2005)
            .message(ex.getMessage())
            .errorType(ErrorType.EXTERNAL)
            .build();

        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleMissingServletRequestParameterException (MissingServletRequestParameterException ex) {
        ErrorDto error = ErrorDto.builder()
            .code(2006)
            .message(ex.getMessage())
            .errorType(ErrorType.EXTERNAL)
            .build();

        return ResponseEntity.status(400).body(error);
    }
}
