package booking.dto.request;

import booking.constant.enums.NotificationType;
import booking.dto.event.NotificationEvent;

public record NotificationRequestDto(
    NotificationReceiver receiver,
    NotificationType type,
    String text
) {
    public record NotificationReceiver (String email, String phone, String firebaseToken) {}

    public static NotificationRequestDto sms(String phone, String text) {
        var receiver = new NotificationReceiver(null, phone, null);

        return new NotificationRequestDto(receiver, NotificationType.SMS, text);
    }

    public static NotificationRequestDto email(String email, String text) {
        var receiver = new NotificationReceiver(email, null, null);

        return new NotificationRequestDto(receiver, NotificationType.EMAIL, text);
    }

    public static NotificationRequestDto push(String firebaseToken, String text) {
        var receiver = new NotificationReceiver(null, null, firebaseToken);

        return new NotificationRequestDto(receiver, NotificationType.PUSH, text);
    }

    public static NotificationRequestDto fromEvent(NotificationEvent event) {
        return switch (event.type()) {
            case SMS -> NotificationRequestDto.sms(event.receiver(), event.text());
            case EMAIL -> NotificationRequestDto.email(event.receiver(), event.text());
            case PUSH -> NotificationRequestDto.push(event.receiver(), event.text());
        };
    }

}
