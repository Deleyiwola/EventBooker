package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.model.CreateOrganizerRequest;
import dan.springframework.eventbooker.model.OrganizerRequestDTO;
import dan.springframework.eventbooker.model.UpdateOrganizerRequestStatus;
import dan.springframework.eventbooker.service.OrganizerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
//@RequestMapping("/bookingApi/v1/organizer-requests")
@RequiredArgsConstructor
public class OrganizerRequestController {
    public static final String ADMIN_ENDPOINT = "/bookingApi/v1/admin/organizer-requests";
    public static final String REQUEST_URI = "/bookingApi/v1/organizer-requests";
    public static final String MY_REQUEST_URI = REQUEST_URI + "/my-request";

    private final OrganizerRequestService organizerRequestService;

    @PostMapping(REQUEST_URI)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizerRequestDTO> createOrganizerRequest(
            @Valid @RequestBody CreateOrganizerRequest request, Authentication authentication) {

        OrganizerRequestDTO requestDTO =
                organizerRequestService.createRequest(request,
                        ((User)authentication.getPrincipal()).getEmail());

        return ResponseEntity
                .created(URI.create(
                        "/bookingApi/v1/organizer-requests/"
                +requestDTO.id()))
                .body(requestDTO);
    }

    @GetMapping(MY_REQUEST_URI)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizerRequestDTO> getMyRequest(Authentication authentication) {

        return ResponseEntity.ok(
                organizerRequestService
                        .getMyRequest(((User)authentication
                                .getPrincipal()).getEmail())
        );
    }

    @GetMapping(ADMIN_ENDPOINT + "/all-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrganizerRequestDTO>> getAllRequests() {

        return ResponseEntity.ok(
                organizerRequestService.getAllRequests()
        );
    }

    @GetMapping(ADMIN_ENDPOINT + "/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrganizerRequestDTO>> getPendingRequests() {

        return ResponseEntity.ok(
                organizerRequestService.getPendingRequests()
        );
    }

    @PatchMapping(ADMIN_ENDPOINT + "/{requestId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizerRequestDTO> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestBody UpdateOrganizerRequestStatus requestStatus){

        return ResponseEntity.ok(
                organizerRequestService
                        .updateRequestStatus(
                                requestId,
                                requestStatus
                        )
        );
    }

}
