package booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 100)
    String name;

    @Column(nullable = false, length = 100)
    String country;

    @OneToMany(mappedBy = "city")
    List<HotelEntity> hotels;
}
