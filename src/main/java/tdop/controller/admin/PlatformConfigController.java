package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.PlatformConfig;
import tdop.repository.UserRepository;
import tdop.service.PlatformConfigService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/config")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class PlatformConfigController {

    private final PlatformConfigService configService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<PlatformConfig>> getAll() {
        return ResponseEntity.ok(configService.getAllConfig());
    }

    @GetMapping("/{key}")
    public ResponseEntity<PlatformConfig> get(@PathVariable String key) {
        return ResponseEntity.ok(configService.getConfig(key));
    }

    @PutMapping("/{key}")
    public ResponseEntity<PlatformConfig> set(@PathVariable String key,
                                               @RequestParam String value,
                                               @RequestParam(required = false) String description,
                                               Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(configService.setConfig(key, value, description, userId));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        configService.deleteConfig(key);
        return ResponseEntity.ok().build();
    }

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(u -> u.getId()).orElse(null);
    }
}
