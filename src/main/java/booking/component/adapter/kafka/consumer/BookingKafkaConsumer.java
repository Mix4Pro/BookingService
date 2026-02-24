package booking.component.adapter.kafka.consumer;

import booking.dto.event.BookingKafkaEventDto;
import booking.service.bookingService.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingKafkaConsumer {
    private final BookingService bookingService;

    @KafkaListener(
        topics = "booking-created",
        groupId = "booking-created-listening",
        containerFactory = "kafkaListenerContainerFactory"
    )

    public void handleBookingCreatedEvent (
        @Payload BookingKafkaEventDto bookingKafkaEventDto,
        @Header (KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header (KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header (KafkaHeaders.OFFSET) long offset
    ) {
        log.info("CONSUMER RECEIVED data id=[{}] : topic=[{}], partition=[{}], offset=[{}], roomId=[{}]",
            bookingKafkaEventDto.bookingId(),topic,partition,offset,bookingKafkaEventDto.roomId()
        );
    }
}
