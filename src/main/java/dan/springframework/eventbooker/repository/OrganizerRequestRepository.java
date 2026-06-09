package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.OrganizerRequest;
import dan.springframework.eventbooker.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizerRequestRepository extends JpaRepository<OrganizerRequest, Long> {
    List<OrganizerRequest> findByStatus(RequestStatus status);


    Optional<OrganizerRequest> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndStatus(Long userId, RequestStatus status);
}

