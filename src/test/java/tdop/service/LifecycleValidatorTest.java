package tdop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.enums.OpportunityStatus;
import tdop.exception.BadRequestException;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LifecycleValidatorTest {

    @Test
    void testValidOpportunityTransitions() {
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(null, OpportunityStatus.DRAFT));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.DRAFT, OpportunityStatus.SUBMITTED_FOR_REVIEW));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.SUBMITTED_FOR_REVIEW, OpportunityStatus.PUBLISHED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.SUBMITTED_FOR_REVIEW, OpportunityStatus.REJECTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.PUBLISHED, OpportunityStatus.CLOSED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.PUBLISHED, OpportunityStatus.EXPIRED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.CLOSED, OpportunityStatus.PUBLISHED));
    }

    @Test
    void testInvalidOpportunityTransitions() {
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateOpportunityTransition(OpportunityStatus.CLOSED, OpportunityStatus.DRAFT));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateOpportunityTransition(OpportunityStatus.EXPIRED, OpportunityStatus.DRAFT));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateOpportunityTransition(null, OpportunityStatus.CLOSED));
    }

    @Test
    void testValidApplicationTransitions() {
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(null, ApplicationStatus.SUBMITTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEWED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.REVIEWED, ApplicationStatus.SHORTLISTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.REVIEWED, ApplicationStatus.REJECTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.SHORTLISTED, ApplicationStatus.INTERVIEW));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.INTERVIEW, ApplicationStatus.ACCEPTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.SUBMITTED, ApplicationStatus.WITHDRAWN));
    }

    @Test
    void testInvalidApplicationTransitions() {
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateApplicationTransition(ApplicationStatus.REJECTED, ApplicationStatus.SUBMITTED));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateApplicationTransition(ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateApplicationTransition(null, ApplicationStatus.SHORTLISTED));
    }

    @Test
    void testValidReportTransitions() {
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition(null, "PENDING"));
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition("PENDING", "UNDER_REVIEW"));
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition("UNDER_REVIEW", "RESOLVED"));
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition("UNDER_REVIEW", "DISMISSED"));
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition("UNDER_REVIEW", "ESCALATED"));
    }

    @Test
    void testInvalidReportTransitions() {
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateReportTransition("RESOLVED", "PENDING"));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateReportTransition("DISMISSED", "UNDER_REVIEW"));
    }
}
