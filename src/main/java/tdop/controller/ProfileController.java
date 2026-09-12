package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.service.UserService;
import tdop.dto.response.UserResponse;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> view() {
        return ResponseEntity.ok("Profile view");
    }

    @PutMapping
    public ResponseEntity<?> update() {
        return ResponseEntity.ok("Profile updated");
    }
}
