package booking.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RoomRequestDto(
    @NotBlank(message = "Room number can't be null")
    String number,

    @Min(value = 0, message = "Price can't be less than 0")
    Double price
) {}
