package booking.repository.specification;

import booking.constant.enums.MealPlanType;
import booking.constant.enums.RoomType;
import booking.entity.RoomAvailabilityEntity;
import booking.entity.RoomEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class RoomSpecification {

    public static Specification<RoomEntity> byCity(Long cityId) {
        return (root, query, cb) -> {
            if (cityId == null) return cb.conjunction();
            return cb.equal(root.get("hotel").get("city").get("id"), cityId);
        };
    }

    public static Specification<RoomEntity> byGuests(Integer guests) {
        return (root, query, cb) -> {
            if (guests == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("maxGuests"), guests);
        };
    }

    public static Specification<RoomEntity> byRoomType(RoomType type) {
        return (root, query, cb) -> {
            if (type == null) return cb.conjunction();
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<RoomEntity> byMealPlan(MealPlanType mealPlan) {
        return (root, query, cb) -> {
            if (mealPlan == null) return cb.conjunction();
            return cb.equal(root.get("mealPlan"), mealPlan);
        };
    }

    public static Specification<RoomEntity> byMinRating(Double rating) {
        return (root, query, cb) -> {
            if (rating == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(
                root.get("hotel").get("rating"), rating
            );
        };
    }

    public static Specification<RoomEntity> isAvailable(
        LocalDate checkIn,
        LocalDate checkOut
    ) {
        return (root, query, cb) -> {

            if (checkIn == null || checkOut == null) return cb.conjunction();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<RoomAvailabilityEntity> availability = subquery.from(RoomAvailabilityEntity.class);

            subquery.select(availability.get("room").get("id"));

            subquery.where(
                cb.and(
                    cb.equal(availability.get("room"), root),
                    cb.between(availability.get("date"), checkIn, checkOut),
                    cb.isFalse(availability.get("isAvailable"))
                )
            );

            return cb.not(cb.exists(subquery));
        };
    }
}