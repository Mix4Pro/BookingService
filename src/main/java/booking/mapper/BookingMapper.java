package booking.mapper;

import booking.dto.event.NotificationEvent;
import booking.dto.response.bookingResponse.BookingResponseDto;
import booking.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "rateId", source = "rate.id")
    @Mapping(target = "cancellationPolicyId", source = "cancellationPolicy.id")
    BookingResponseDto toResponseFromEntity (BookingEntity bookingEntity);

}
