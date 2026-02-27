package booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@Table(name = "rates")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    RoomEntity room;

    @Column(name = "price_per_night", nullable = false)
    BigDecimal pricePerNight;

    @Column(name = "valid_from", nullable = false)
    LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    LocalDate validTo;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}

