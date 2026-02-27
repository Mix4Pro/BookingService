package booking.dto.event;

import booking.constant.enums.NotificationType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NotificationEvent(
        @NotNull
        UUID id,

        @NotNull
        String receiver,

        @NotNull
        NotificationType type,

        @NotNull
        String text
) {
}
