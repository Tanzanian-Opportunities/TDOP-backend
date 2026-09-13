package tdop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import tdop.dto.request.ApplicationRequest;
import tdop.entity.enums.ApplicationStatus;
import tdop.service.ApplicationService;
import tdop.entity.Application;
import java.util.List;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ApplicationRequest applicationRequest;
    private Application application;

    @BeforeEach
    void setUp() {
        applicationRequest = new ApplicationRequest();
        applicationRequest.setCoverLetter("I am a great fit for this role.");
        applicationRequest.setResumeUrl("http://example.com/resume.pdf");

        application = new Application();
        application.setId(1L);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCoverLetter("I am a great fit for this role.");
        application.setResumeUrl("http://example.com/resume.pdf");
    }

    @AfterEach
    void tearDown() {
        applicationRequest = null;
        application = null;
    }

    @Test
    void testApplySuccess() throws Exception {
        when(applicationService.apply(eq(1L), eq(2L), any(String.class), any(String.class)))
            .thenReturn(application);

        mockMvc.perform(post("/api/v1/applications?oppId=1&applicantId=2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void testTrackApplications() throws Exception {
        when(applicationService.getMyApplications(2L)).thenReturn(List.of(application));

        mockMvc.perform(get("/api/v1/applications/me?userId=2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("APPLIED"));
    }

    @Test
    void testUpdateStatusSuccess() throws Exception {
        when(applicationService.updateStatus(eq(1L), eq(ApplicationStatus.INTERVIEW)))
            .thenReturn(application);

        mockMvc.perform(put("/api/v1/applications/1/status?status=INTERVIEW"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("INTERVIEW"));
    }

    @Test
    void testApplyWithEmptyCoverLetter() throws Exception {
        applicationRequest.setCoverLetter("");
        when(applicationService.apply(eq(1L), eq(2L), eq(""), any(String.class)))
            .thenReturn(application);

        mockMvc.perform(post("/api/v1/applications?oppId=1&applicantId=2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applicant").isNotEmpty());
    }
}
