package booking.dto;

import booking.constant.enums.ErrorType;
import lombok.Builder;

import java.util.List;

@Builder
public record ErrorDto(
    int code,
    String message,
    ErrorType errorType,
    List<String> validationErrors
) {}
