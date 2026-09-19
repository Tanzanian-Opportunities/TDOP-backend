package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tdop.repository.UserRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class HealthController {

    private final UserRepository userRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "UP");
        body.put("timestamp", Instant.now().toString());
        try {
            userRepository.count();
            body.put("database", "UP");
        } catch (Exception e) {
            body.put("status", "DOWN");
            body.put("database", "DOWN");
            body.put("databaseError", e.getMessage());
            return ResponseEntity.status(503).body(body);
        }
        return ResponseEntity.ok(body);
    }
}
