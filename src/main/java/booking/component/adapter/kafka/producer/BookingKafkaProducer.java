//package booking.component.adapter.kafka.producer;
//
//import booking.dto.event.BookingKafkaEventDto;
//import booking.entity.BookingEntity;
//import booking.mapper.BookingMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.CompletableFuture;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class BookingKafkaProducer {
//
//    private static final String BOOKING_TOPIC = "booking-created";
//
//    private final KafkaTemplate<String,Object> kafkaTemplate;
//
//    private final BookingMapper bookingMapper;
//
//    public void sendBookingCreatedEvent (BookingEntity booking) {
//        BookingKafkaEventDto event = bookingMapper.toKafkaEventFromEntity(booking);
//
//        CompletableFuture<SendResult<String,Object>> future = kafkaTemplate.send(
//            BOOKING_TOPIC,
//            String.valueOf(booking.getId()),
//            event
//        );
//
//        future.whenComplete((result,ex)->{
//           if(ex != null) {
//               log.error("Error happened while sending to kafka , id : {} \n Message : {}",
//                   booking.getId(),
//                   ex.getMessage()
//               );
//           }
//
//           log.info("Sent to kafka broker, id : {}", booking.getId());
//        });
//    }
//}
