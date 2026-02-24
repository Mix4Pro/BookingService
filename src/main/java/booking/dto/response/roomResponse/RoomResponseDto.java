package booking.dto.response.roomResponse;

import booking.constant.enums.MealPlanType;
import booking.constant.enums.RoomType;

public record RoomResponseDto(
    Long id,
    Long hotelId,
    String roomNumber,
    RoomType type,
    Integer maxGuests,
    MealPlanType mealPlan
) {}
