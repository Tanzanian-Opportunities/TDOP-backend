package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.service.UserService;
import tdop.entity.enums.UserRole;
import tdop.dto.response.UserResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<UserResponse> suspend(@PathVariable Long id) {
        return ResponseEntity.ok(userService.suspendUser(id));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long id, @RequestParam String role) {
        return ResponseEntity.ok(userService.updateRole(id, UserRole.valueOf(role.toUpperCase())));
    }
}
