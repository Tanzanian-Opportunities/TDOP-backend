package tdop.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class RateLimitFilterTest {

    private final RateLimitFilter filter = new RateLimitFilter();

    @Test
    void testNonLoginEndpointPassesThrough() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/public/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, (req, res) -> {
            res.setStatus(200);
        });

        assertEquals(200, response.getStatus());
    }

    @Test
    void testLoginWithinRateLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, (req, res) -> {
            res.setStatus(200);
        });

        assertEquals(200, response.getStatus());
    }

    @Test
    void testLoginExceedingRateLimit() throws ServletException, IOException {
        MockHttpServletRequest response_request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        for (int i = 0; i < 22; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/v1/auth/login");
            req.setRemoteAddr("192.168.1.1");
            MockHttpServletResponse res = new MockHttpServletResponse();
            filter.doFilterInternal(req, res, (req2, res2) -> res2.setStatus(200));
            if (i == 21) {
                assertEquals(429, res.getStatus());
            }
        }
    }

    @Test
    void testDifferentIpsHaveSeparateWindows() throws ServletException, IOException {
        for (int i = 0; i < 20; i++) {
            MockHttpServletRequest req1 = new MockHttpServletRequest("POST", "/api/v1/auth/login");
            req1.setRemoteAddr("10.0.0.1");
            filter.doFilterInternal(req1, new MockHttpServletResponse(), (r, s) -> {});

            MockHttpServletRequest req2 = new MockHttpServletRequest("POST", "/api/v1/auth/login");
            req2.setRemoteAddr("10.0.0.2");
            filter.doFilterInternal(req2, new MockHttpServletResponse(), (r, s) -> {});
        }
        MockHttpServletRequest overLimit = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        overLimit.setRemoteAddr("10.0.0.2");
        MockHttpServletResponse res2 = new MockHttpServletResponse();
        filter.doFilterInternal(overLimit, res2, (r, s) -> {});
        assertEquals(429, res2.getStatus());
    }
}
