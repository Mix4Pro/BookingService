package booking.entity;

import booking.constant.enums.MealPlanType;
import booking.constant.enums.RoomType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    HotelEntity hotel;

    @Column(name = "room_number", nullable = false, length = 50)
    String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    RoomType type;

    @Column(name = "max_guests", nullable = false)
    Integer maxGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_plan", nullable = false, length = 20)
    MealPlanType mealPlan;

    @OneToMany(mappedBy = "room")
    List<RateEntity> rates;

    @OneToMany(mappedBy = "room")
    List<RoomAvailabilityEntity> roomAvailabilities;

    @OneToMany(mappedBy = "room")
    List<BookingEntity> bookings;
}
