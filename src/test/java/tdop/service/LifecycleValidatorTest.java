package tdop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.enums.OpportunityStatus;
import tdop.entity.enums.ReportStatus;
import tdop.exception.BadRequestException;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LifecycleValidatorTest {

    @Test
    void testValidOpportunityTransitions() {
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.DRAFT, OpportunityStatus.SUBMITTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.SUBMITTED, OpportunityStatus.UNDER_REVIEW));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.UNDER_REVIEW, OpportunityStatus.VERIFIED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.VERIFIED, OpportunityStatus.PUBLISHED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.PUBLISHED, OpportunityStatus.CLOSING_SOON));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.PUBLISHED, OpportunityStatus.EXPIRED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.SUSPENDED, OpportunityStatus.PUBLISHED));
        assertDoesNotThrow(() -> LifecycleValidator.validateOpportunityTransition(OpportunityStatus.DRAFT, OpportunityStatus.DRAFT));
    }

    @Test
    void testInvalidOpportunityTransitions() {
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateOpportunityTransition(OpportunityStatus.EXPIRED, OpportunityStatus.DRAFT));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateOpportunityTransition(OpportunityStatus.DRAFT, OpportunityStatus.PUBLISHED));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateOpportunityTransition(null, OpportunityStatus.DRAFT));
    }

    @Test
    void testValidApplicationTransitions() {
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.PREPARING, ApplicationStatus.APPLIED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.APPLIED, ApplicationStatus.UNDER_REVIEW));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.UNDER_REVIEW, ApplicationStatus.SHORTLISTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.SHORTLISTED, ApplicationStatus.INTERVIEW));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.INTERVIEW, ApplicationStatus.ACCEPTED));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.APPLIED, ApplicationStatus.WITHDRAWN));
        assertDoesNotThrow(() -> LifecycleValidator.validateApplicationTransition(ApplicationStatus.APPLIED, ApplicationStatus.APPLIED));
    }

    @Test
    void testInvalidApplicationTransitions() {
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateApplicationTransition(ApplicationStatus.REJECTED, ApplicationStatus.APPLIED));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateApplicationTransition(ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateApplicationTransition(null, ApplicationStatus.SHORTLISTED));
    }

    @Test
    void testValidReportTransitions() {
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition(ReportStatus.PENDING, ReportStatus.REVIEWED));
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition(ReportStatus.REVIEWED, ReportStatus.ACTIONED));
        assertDoesNotThrow(() -> LifecycleValidator.validateReportTransition(ReportStatus.PENDING, ReportStatus.PENDING));
    }

    @Test
    void testInvalidReportTransitions() {
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateReportTransition(ReportStatus.ACTIONED, ReportStatus.PENDING));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateReportTransition(ReportStatus.REVIEWED, ReportStatus.PENDING));
        assertThrows(BadRequestException.class, () ->
            LifecycleValidator.validateReportTransition(null, ReportStatus.PENDING));
    }
}