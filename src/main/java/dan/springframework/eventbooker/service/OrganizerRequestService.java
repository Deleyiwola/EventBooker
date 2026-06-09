package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.CreateOrganizerRequest;
import dan.springframework.eventbooker.model.OrganizerRequestDTO;
import dan.springframework.eventbooker.model.UpdateOrganizerRequestStatus;

import java.util.List;

public interface OrganizerRequestService {

    OrganizerRequestDTO createRequest(CreateOrganizerRequest request, String email);

    OrganizerRequestDTO updateRequestStatus(Long requestId, UpdateOrganizerRequestStatus status);

    List<OrganizerRequestDTO> getAllRequests();

    List<OrganizerRequestDTO> getPendingRequests();

    OrganizerRequestDTO getMyRequest(String email);

}
