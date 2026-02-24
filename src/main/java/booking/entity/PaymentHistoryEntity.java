package booking.entity;

import booking.constant.enums.PaymentHistoryPaymentStatus;
import booking.constant.enums.PaymentHistoryPaymentType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "payment_history")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "payment_history", nullable = false)
    BookingEntity booking;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentHistoryPaymentType type;

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentHistoryPaymentStatus status;

    @Column(name = "bank_transaction_id")
    String bankTransactionId;

    @Column(name = "created_at")
    LocalDateTime createdAt;


}
