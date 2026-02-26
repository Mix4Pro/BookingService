package booking.entity;

import booking.constant.enums.BookingStatus;
import booking.constant.enums.PaymentPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "bookings")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    RoomEntity room;

    @ManyToOne
    @JoinColumn(name = "rate_id", nullable = false)
    RateEntity rate;

    @ManyToOne
    @JoinColumn(name = "cancellation_policy_id", nullable = false)
    CancellationPolicyEntity cancellationPolicy;

    @Column(name = "check_in_date", nullable = false)
    LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    LocalDate checkOutDate;

    @Column(name = "guests_count", nullable = false)
    Integer guestsCount;

    @Column(name = "total_amount", nullable = false)
    BigDecimal totalAmount;

    @Column(name = "prepayment_amount")
    BigDecimal prepaymentAmount;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    BookingStatus status;

    @Column(name = "payment_plan", nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentPlan paymentPlan;

    @Column(name = "hold_expires_at")
    LocalDateTime holdExpiresAt;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
