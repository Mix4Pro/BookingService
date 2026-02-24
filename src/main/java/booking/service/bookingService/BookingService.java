//package booking.service.bookingService;
//
//import booking.constant.enums.BookingStatus;
//import booking.dto.request.BookingRequestDto;
//import booking.dto.response.bookingResponse.BookingPageResponseDto;
//import booking.dto.response.bookingResponse.BookingResponseDto;
//import org.springframework.data.domain.Pageable;
//
//
//public interface BookingService {
//    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto);
//    BookingResponseDto confirmBooking(long id);
//    BookingResponseDto cancelBooking(long id);
//    BookingPageResponseDto findBookingsByStatus(BookingStatus status, Pageable pageable);
//}
