package booking.component.kafka.producer;

import booking.constant.enums.NotificationType;
import booking.dto.event.NotificationEvent;
import booking.entity.BookingEntity;
import booking.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingKafkaProducer {

    private static final String BOOKING_TOPIC = "booking-confirmed";

    private final KafkaTemplate<String,Object> kafkaTemplate;

    private final BookingMapper bookingMapper;

    public void sendBookingConfirmedEvent (BookingEntity booking) {
        UUID randomId = UUID.randomUUID();
        NotificationEvent event = new NotificationEvent(
            randomId,
            booking.getUser().getEmail(),
            NotificationType.EMAIL,
            "Booking with an id: " + booking.getId() + " has been confirmed"
        );

        CompletableFuture<SendResult<String,Object>> future = kafkaTemplate.send(
            BOOKING_TOPIC,
            event
        );

        future.whenComplete((result,ex)->{
           if(ex != null) {
               log.error("Error happened while sending to kafka , id : {} \n Message : {}",
                   booking.getId(),
                   ex.getMessage()
               );
           }

           log.info("Sent to kafka broker, id : {}", booking.getId());

           return;
        });
    }
}
