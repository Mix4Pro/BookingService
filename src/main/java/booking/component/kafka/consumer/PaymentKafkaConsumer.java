package booking.component.kafka.consumer;

import booking.component.adapter.PaymentAdapter;
import booking.component.kafka.producer.BookingKafkaProducer;
import booking.constant.enums.BookingStatus;
import booking.constant.enums.PaymentHistoryPaymentStatus;
import booking.constant.enums.PaymentStatus;
import booking.dto.event.PaymentEvent;
import booking.dto.response.PaymentResponseDto;
import booking.entity.BookingEntity;
import booking.entity.PaymentHistoryEntity;
import booking.exception.payment.PaymentFailedException;
import booking.exception.payment.PaymentIncorrectTypeException;
import booking.exception.booking.BookingNotFoundException;
import booking.exception.payment.PaymentNotFoundException;
import booking.repository.BookingRepository;
import booking.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaConsumer {
    private final PaymentAdapter paymentAdapter;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final BookingRepository bookingRepository;
    private final BookingKafkaProducer bookingKafkaProducer;

    private static final String PAYMENT_REQUEST_TOPIC = "payment-request";

    @KafkaListener(
        topics = PAYMENT_REQUEST_TOPIC,
        groupId = "payment group"
    )
    public void handlePaymentRequestEvent (PaymentEvent event) {
        log.info("Получен PaymentEvent: paymentId={}, type={}",
            event.paymentId(), event.type());

        PaymentHistoryEntity paymentHistory = paymentHistoryRepository
            .findById(event.paymentId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found",HttpStatus.NOT_FOUND));

        BookingEntity booking = bookingRepository.findById(event.bookingId())
            .orElseThrow(() -> new BookingNotFoundException("Booking not found", HttpStatus.NOT_FOUND));

        try {
            PaymentResponseDto paymentResponseDto = switch (event.type()) {
                case CHARGE -> paymentAdapter.charge(
                    event.amount(),
                    event.currency(),
                    event.senderName(),
                    event.senderToken()
                );
                case REFUND -> paymentAdapter.refund(
                    event.amount(),
                    event.currency(),
                    event.senderName(),
                    event.senderToken()
                );
                default -> throw new PaymentIncorrectTypeException("Incorrect type: " + event.type(),HttpStatus.BAD_REQUEST);
            };

            boolean success = paymentResponseDto.status() == PaymentStatus.COMPLETED;
            paymentHistory.setStatus(success
                ? PaymentHistoryPaymentStatus.SUCCESS
                : PaymentHistoryPaymentStatus.FAILED);
            paymentHistory.setBankTransactionId(paymentResponseDto.id());
            paymentHistoryRepository.save(paymentHistory);

            if (success) {
                booking.setStatus(switch (event.type()) {
                    case CHARGE -> BookingStatus.PAID;
                    case REFUND -> BookingStatus.CANCELLED;
                });
                bookingRepository.save(booking);
                bookingKafkaProducer.sendBookingNotification(booking);
            }

        } catch (PaymentFailedException ex) {
            log.error("Payment failed: paymentId={}, error={}",
                event.paymentId(), ex.getMessage());
            paymentHistory.setStatus(PaymentHistoryPaymentStatus.FAILED);
            paymentHistoryRepository.save(paymentHistory);
        }

    }
}
