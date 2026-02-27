package booking.repository;

import booking.entity.CancellationPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicyEntity, Long> {
}
