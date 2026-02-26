package booking.component.kafka.producer;

import booking.constant.enums.BookingStatus;
import booking.constant.enums.CurrencyType;
import booking.constant.enums.NotificationType;
import booking.constant.enums.PaymentStatus;
import booking.constant.enums.PaymentType;
import booking.dto.event.NotificationEvent;
import booking.dto.event.PaymentEvent;
import booking.entity.BookingEntity;
import booking.entity.PaymentHistoryEntity;
import booking.entity.UserEntity;
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
    private static final String BOOKING_CONFIRMED_TOPIC = "booking-confirmed";
    private static final String BOOKING_AFTER_PAYMENT_TOPIC = "booking-after-payment";

    private static final String PAYMENT_REQUEST_TOPIC = "payment-request";

    private final KafkaTemplate<String,Object> kafkaTemplate;

    private final BookingMapper bookingMapper;

    public void sendBookingNotification (BookingEntity booking) {
        UUID randomId = UUID.randomUUID();
        NotificationEvent event = new NotificationEvent(
            randomId,
            booking.getUser().getEmail(),
            NotificationType.EMAIL,
            "Booking with an id: " + booking.getId() + " is now " + booking.getStatus()
        );

        CompletableFuture<SendResult<String,Object>> future = kafkaTemplate.send(
            BOOKING_CONFIRMED_TOPIC,
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
        });
    }

    public void sendBookingPayment (
        PaymentHistoryEntity paymentHistory,
        BookingEntity booking,
        CurrencyType currency
    ) {
        UserEntity user = booking.getUser();
        PaymentEvent event = new PaymentEvent(
            paymentHistory.getId(),
            booking.getId(),
            user.getFirstName() + " " + user.getLastName(),
            user.getCardToken(),
            booking.getPrepaymentAmount(),
            currency,
            PaymentType.CHARGE
        );

        kafkaTemplate.send(PAYMENT_REQUEST_TOPIC, event);
        log.info("PaymentEvent отправлен в Kafka: paymentId={}", paymentHistory.getId());
    }

    public void sendBookingRefund(PaymentHistoryEntity refund, BookingEntity booking) {
        UserEntity user = booking.getUser();
        PaymentEvent event = new PaymentEvent(
            refund.getId(),
            booking.getId(),
            user.getFirstName() + " " + user.getLastName(),
            user.getCardToken(),
            refund.getAmount(),
            CurrencyType.USD,
            PaymentType.REFUND
        );

        kafkaTemplate.send(PAYMENT_REQUEST_TOPIC, event);
        log.info("RefundEvent отправлен в Kafka: refundId={}", refund.getId());
    }

}
