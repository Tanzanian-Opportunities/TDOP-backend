package tdop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.service.OpportunityService;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(OpportunityController.class)
class OpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpportunityService opportunityService;

    @Autowired
    private ObjectMapper objectMapper;

    private OpportunityRequest opportunityRequest;
    private OpportunityResponse opportunityResponse;

    @BeforeEach
    void setUp() {
        opportunityRequest = new OpportunityRequest();
        opportunityRequest.setTitle("Software Engineer");
        opportunityRequest.setDescription("Full-time software engineering role");
        opportunityRequest.setLocation("Remote");
        opportunityRequest.setType("FULL_TIME");
        opportunityRequest.setCategory("Engineering");
        opportunityRequest.setSalaryRange("80000-120000");
        opportunityRequest.setTags("Java,Spring,React");
        opportunityRequest.setDeadline(LocalDateTime.now().plusDays(30));

        opportunityResponse = OpportunityResponse.builder()
            .id(1L)
            .title("Software Engineer")
            .description("Full-time software engineering role")
            .location("Remote")
            .type("FULL_TIME")
            .category("Engineering")
            .salaryRange("80000-120000")
            .status("PUBLISHED")
            .deadline(LocalDateTime.now().plusDays(30))
            .tags("Java,Spring,React")
            .createdAt(LocalDateTime.now())
            .build();
    }

    @AfterEach
    void tearDown() {
        opportunityRequest = null;
        opportunityResponse = null;
    }

    @Test
    void testBrowseOpportunities() throws Exception {
        when(opportunityService.getAllOpportunities()).thenReturn(List.of(opportunityResponse));

        mockMvc.perform(get("/api/v1/opportunities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Software Engineer"))
            .andExpect(jsonPath("$[0].status").value("PUBLISHED"));
    }

    @Test
    void testSearchOpportunities() throws Exception {
        when(opportunityService.searchOpportunities("engineer")).thenReturn(List.of(opportunityResponse));

        mockMvc.perform(get("/api/v1/opportunities/search?keyword=engineer"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Software Engineer"))
            .andExpect(jsonPath("$[0].location").value("Remote"));
    }

    @Test
    void testFilterByCategory() throws Exception {
        when(opportunityService.filterByCategory("Engineering")).thenReturn(List.of(opportunityResponse));

        mockMvc.perform(get("/api/v1/opportunities/filter/Engineering"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category").value("Engineering"))
            .andExpect(jsonPath("$[0].status").value("PUBLISHED"));
    }

    @Test
    void testCreateOpportunity() throws Exception {
        when(opportunityService.createOpportunity(any(OpportunityRequest.class), eq(1L))).thenReturn(opportunityResponse);

        mockMvc.perform(post("/api/v1/opportunities?orgId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opportunityRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Software Engineer"));
    }

    @Test
    void testUpdateOpportunity() throws Exception {
        when(opportunityService.updateOpportunity(eq(1L), any(OpportunityRequest.class))).thenReturn(opportunityResponse);

        mockMvc.perform(put("/api/v1/opportunities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opportunityRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Software Engineer"));
    }

    @Test
    void testDeleteOpportunity() throws Exception {
        doNothing().when(opportunityService).deleteOpportunity(1L);

        mockMvc.perform(delete("/api/v1/opportunities/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testPublishOpportunity() throws Exception {
        when(opportunityService.publishOpportunity(1L)).thenReturn(opportunityResponse);

        mockMvc.perform(post("/api/v1/opportunities/1/publish"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }
}
