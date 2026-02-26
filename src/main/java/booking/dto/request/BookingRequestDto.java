    package booking.dto.request;

    import jakarta.validation.constraints.FutureOrPresent;
    import jakarta.validation.constraints.Min;
    import jakarta.validation.constraints.NotNull;

    import java.time.LocalDate;

    public record BookingRequestDto(
        @NotNull(message = "userId can't be null")
        Long userId,

        @NotNull(message = "roomId can't be null")
        Long roomId,

        @NotNull(message = "rateId can't be null")
        Long rateId,

        @NotNull(message = "checkInDate can't be null")
        @FutureOrPresent(message = "checkInDate must be either present or future")
        LocalDate checkInDate,

        @NotNull(message = "checkOutDate can't be null")
        LocalDate checkOutDate,

        @NotNull
        @Min(value = 1, message = "The minimum value of guestsCount must be 1")
        Integer guestsCount,

        @NotNull(message = "cancellationPolicyId can't be null")
        Long cancellationPolicyId
    ) {
    }
