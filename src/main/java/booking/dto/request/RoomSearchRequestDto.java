package booking.dto.request;

import booking.constant.enums.HotelType;
import booking.constant.enums.MealPlanType;
import booking.constant.enums.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RoomSearchRequestDto(
    Long cityId,

    LocalDate checkIn,
    LocalDate checkOut,

    Integer guests,

    BigDecimal minPrice,
    BigDecimal maxPrice,

    HotelType hotelType,
    RoomType roomType,

    MealPlanType mealPlan,

    Double minRating,

    List<Long>amenityIds,

    String brand

) {
}
