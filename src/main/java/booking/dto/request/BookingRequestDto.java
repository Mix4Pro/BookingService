package booking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequestDto(
    @NotNull(message = "Price can't be null")
    @Min(value = 1, message = "The price must be greater than 1 USD")
    Double price,

    @NotNull(message = "Check in date can't be null")
    @FutureOrPresent(message = "Check in should start from either now or some date in the future")
    LocalDate checkIn,

    @NotNull(message = "Check out date can't be null")
    LocalDate checkOut,

    @NotNull(message = "Room id can not be null")
    Long roomId
) {
}
