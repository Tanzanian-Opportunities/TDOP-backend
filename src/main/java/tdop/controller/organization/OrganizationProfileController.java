package tdop.controller.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organization/profile")
@RequiredArgsConstructor
public class OrganizationProfileController {

    @GetMapping
    public ResponseEntity<?> view() {
        return ResponseEntity.ok("Organization Profile");
    }

    @PutMapping
    public ResponseEntity<?> update() {
        return ResponseEntity.ok("Organization Profile Updated");
    }
}
