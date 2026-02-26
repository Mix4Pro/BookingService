package booking.component.kafka.consumer;

import booking.component.adapter.NotificationAdapter;
import booking.dto.event.NotificationEvent;
import booking.dto.request.NotificationRequestDto;
import booking.dto.response.NotificationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaConsumer {
    private final RestClient notificationRestClient;
    private final NotificationAdapter notificationAdapter;

    @KafkaListener(
        topics = "booking-confirmed",
        groupId = "booking-confirmed-group"
    )

    public void handleBookingConfirmedEvent (
        @Payload @Valid NotificationEvent event
    ) {
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.fromEvent(event);

        log.info("EVENT receiver = {}, type = {}",event.receiver(),event.type());

        NotificationResponseDto notificationResponseDto = notificationAdapter.sendNotification(notificationRequestDto);
    }

    @KafkaListener(
        topics = "booking-after-payment",
        groupId = "booking-after-payment-group"
    )

    public void handleBookingAfterPaymentEvent (
        @Payload @Valid NotificationEvent event
    ) {
        NotificationRequestDto notificationRequestDto = NotificationRequestDto.fromEvent(event);

        log.info("EVENT receiver = {}, type = {}",event.receiver(),event.type());

        NotificationResponseDto notificationResponseDto = notificationAdapter.sendNotification(notificationRequestDto);
    }
}
