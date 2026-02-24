package booking.mapper;

import booking.dto.event.BookingKafkaEventDto;
import booking.dto.request.BookingRequestDto;
import booking.dto.response.bookingResponse.BookingResponseDto;
import booking.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "room", ignore = true)
    BookingEntity toEntityFromRequest (BookingRequestDto bookingRequestDto);

    @Mapping(target = "roomId", source = "id")
    BookingResponseDto toResponseFromEntity (BookingEntity bookingEntity);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "roomId", source = "room.id")
    BookingKafkaEventDto toKafkaEventFromEntity (BookingEntity bookingEntity);
}
