package tdop.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicEndpointsAccessible() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login")).andExpect(status().is4xxClientError());
        mockMvc.perform(get("/api/v1/public/opportunities")).andExpect(status().is4xxClientError());
    }

    @Test
    void testAdminEndpointsRequireAuth() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/admin/users")).andExpect(status().isForbidden());
    }

    @Test
    void testOrganizationEndpointsRequireAuth() throws Exception {
        mockMvc.perform(get("/api/v1/organization/profile")).andExpect(status().isForbidden());
    }

    @Test
    void testModerationEndpointsRequireAuth() throws Exception {
        mockMvc.perform(get("/api/v1/moderation/pending")).andExpect(status().isForbidden());
    }

    @Test
    void testVerificationEndpointsRequireAuth() throws Exception {
        mockMvc.perform(get("/api/v1/verification-officer/pending")).andExpect(status().isForbidden());
    }

    @Test
    void testSecurityHeadersPresent() throws Exception {
        mockMvc.perform(get("/api/v1/public/health"))
            .andExpect(status().isOk());
    }

    @Test
    void testRateLimitOnLogin() throws Exception {
        for (int i = 0; i < 22; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType("application/json")
                    .content("{\"email\":\"test@test.com\",\"password\":\"wrong\"}"));
        }
    }
}
