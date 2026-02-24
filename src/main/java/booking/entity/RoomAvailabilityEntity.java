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

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room_availability")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomAvailabilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    RoomEntity room;

    @Column(nullable = false)
    LocalDate date;

    @Column(name = "is_available", nullable = false)
    Boolean isAvailable = true;
}
