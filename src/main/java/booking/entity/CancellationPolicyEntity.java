package booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "cancellation_policies")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CancellationPolicyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(name = "hours_before_check_in", nullable = false)
    Integer HoursBeforeCheckIn;

    @Column(name = "refund_percentage", nullable = false)
    BigDecimal RefundPercentage;
}
