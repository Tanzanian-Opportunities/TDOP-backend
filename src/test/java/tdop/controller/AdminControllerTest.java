package tdop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tdop.controller.admin.UserController;
import tdop.controller.admin.AdminOpportunityController;
import tdop.dto.response.UserResponse;
import tdop.service.UserService;
import tdop.service.OpportunityService;
import tdop.service.AnalyticsService;
import java.util.List;
import java.util.Map;

@WebMvcTest({UserController.class, AdminOpportunityController.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private OpportunityService opportunityService;

    @MockBean
    private AnalyticsService analyticsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
            .id(1L)
            .email("admin@example.com")
            .fullName("Admin User")
            .phone("1234567890")
            .role("ADMIN")
            .enabled(true)
            .verified(true)
            .build();
    }

    @AfterEach
    void tearDown() {
        userResponse = null;
    }

    @Test
    void testListUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/v1/admin/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("admin@example.com"))
            .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void testSuspendUser() throws Exception {
        when(userService.suspendUser(1L)).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/admin/users/1/suspend"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void testUpdateRole() throws Exception {
        when(userService.updateRole(eq(1L), any())).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/admin/users/1/role?role=ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void testVerifyOpportunity() throws Exception {
        mockMvc.perform(put("/api/v1/admin/opportunities/1/verify"))
            .andExpect(status().isOk())
            .andExpect(content().string("Verified"));
    }

    @Test
    void testModerateOpportunity() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/opportunities/1/moderate"))
            .andExpect(status().isOk())
            .andExpect(content().string("Moderated"));
    }

    @Test
    void testAnalyticsDashboard() throws Exception {
        when(analyticsService.getDashboardStats()).thenReturn(Map.of("totalUsers", 100, "totalOpps", 50));

        mockMvc.perform(get("/api/v1/admin/opportunities/analytics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalUsers").value(100))
            .andExpect(jsonPath("$.totalOpps").value(50));
    }
}
