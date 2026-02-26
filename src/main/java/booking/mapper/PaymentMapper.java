package booking.mapper;

import booking.dto.response.PaymentHistoryResponseDto;
import booking.entity.PaymentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "bookingId", source = "booking.id")
    PaymentHistoryResponseDto toHistoryResponseFromHistoryEntity (PaymentHistoryEntity paymentHistory);

    @Mapping( target = "bookingId",source = "booking.id")
    PaymentHistoryResponseDto toResponse(PaymentHistoryEntity payment);
}
