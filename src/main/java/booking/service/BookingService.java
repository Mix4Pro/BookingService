package booking.service;


import booking.dto.request.BookingRequestDto;
import booking.dto.response.bookingResponse.BookingResponseDto;

public interface BookingService {
    BookingResponseDto holdARoom (BookingRequestDto bookingRequestDto);
    BookingResponseDto confirmARoom (Long id);
}
