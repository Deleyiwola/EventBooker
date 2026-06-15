package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.entity.OrganizerRequest;
import dan.springframework.eventbooker.entity.RequestStatus;
import dan.springframework.eventbooker.entity.Role;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.*;
import dan.springframework.eventbooker.mapper.OrganizerRequestMapper;
import dan.springframework.eventbooker.model.CreateOrganizerRequest;
import dan.springframework.eventbooker.model.OrganizerRequestDTO;
import dan.springframework.eventbooker.model.UpdateOrganizerRequestStatus;
import dan.springframework.eventbooker.repository.OrganizerRequestRepository;
import dan.springframework.eventbooker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizerRequestServiceImpl implements OrganizerRequestService {

    private final OrganizerRequestRepository organizerRequestRepository;
    private final UserRepository userRepository;
    private final OrganizerRequestMapper organizerRequestMapper;


    @Override
    public OrganizerRequestDTO createRequest(
            CreateOrganizerRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not found"));

        if (user.getRole()== Role.ROLE_ADMIN||user.getRole()== Role.ROLE_ORGANIZER) {
            throw new UserAlreadyOrganizerException("You are already an Organizer");
        }

        if (organizerRequestRepository.existsByUserId(user.getId())) {
            throw new OrganizerRequestAlreadyExistsException(
                    "You have already submitted an organizer request"
            );
        }

        OrganizerRequest organizerRequest =
                OrganizerRequest.builder()
                        .user(user)
                        .organizationName(request.organizationName())
                        .description(request.description())
                        .status(RequestStatus.PENDING)
                        .requestDate(LocalDateTime.now())
                        .build();

        OrganizerRequest savedRequest = organizerRequestRepository.save(organizerRequest);

        return organizerRequestMapper.organizerRequestToOrganizerRequestDTO(savedRequest);
    }

    @Override
    public OrganizerRequestDTO updateRequestStatus(Long requestId, UpdateOrganizerRequestStatus status) {

        if (status.status() != RequestStatus.APPROVED
        && status.status() != RequestStatus.REJECTED) {
            throw new InvalidStatusException("Status must be APPROVED or REJECTED");
        }

        OrganizerRequest request =
                organizerRequestRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ProcessedRequestException("Request has already been processed");
        }


        request.setStatus(status.status());
        request.setAdminComment(status.adminComment());
        request.setReviewedDate(LocalDateTime.now());

        if (status.status() == RequestStatus.APPROVED) {
            User user = request.getUser();
            user.setRole(Role.ROLE_ORGANIZER);
            userRepository.save(user);
        }
        OrganizerRequest savedRequest = organizerRequestRepository.save(request);

        return organizerRequestMapper.organizerRequestToOrganizerRequestDTO(savedRequest);
    }

    @Override
    public List<OrganizerRequestDTO> getAllRequests() {
        return organizerRequestRepository
                .findAll()
                .stream()
                .map(organizerRequestMapper::organizerRequestToOrganizerRequestDTO)
                .toList();
    }

    @Override
    public List<OrganizerRequestDTO> getPendingRequests() {

        return organizerRequestRepository
                .findByStatus(RequestStatus.PENDING)
                .stream()
                .map(organizerRequestMapper::organizerRequestToOrganizerRequestDTO)
                .toList();
    }

    @Override
    public OrganizerRequestDTO getMyRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User not found"));

        OrganizerRequest request= organizerRequestRepository.findByUserId(user.getId())
                .orElseThrow(()-> new NotFoundException("No request found"));

        return organizerRequestMapper.organizerRequestToOrganizerRequestDTO(request);
    }
}
