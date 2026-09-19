package tdop.service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.enums.OpportunityStatus;
import tdop.entity.enums.ReportStatus;
import tdop.exception.BadRequestException;

public final class LifecycleValidator {

    private static final Map<OpportunityStatus, Set<OpportunityStatus>> OPPORTUNITY_TRANSITIONS = new EnumMap<>(OpportunityStatus.class);
    private static final Map<ApplicationStatus, Set<ApplicationStatus>> APPLICATION_TRANSITIONS = new EnumMap<>(ApplicationStatus.class);
    private static final Map<ReportStatus, Set<ReportStatus>> REPORT_TRANSITIONS = new EnumMap<>(ReportStatus.class);

    static {
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.DRAFT, EnumSet.of(
            OpportunityStatus.SUBMITTED, OpportunityStatus.APPROVED, OpportunityStatus.REJECTED, OpportunityStatus.ARCHIVED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.SUBMITTED, EnumSet.of(
            OpportunityStatus.UNDER_REVIEW, OpportunityStatus.REJECTED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.UNDER_REVIEW, EnumSet.of(
            OpportunityStatus.VERIFIED, OpportunityStatus.REJECTED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.VERIFIED, EnumSet.of(
            OpportunityStatus.MODERATION, OpportunityStatus.PUBLISHED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.MODERATION, EnumSet.of(
            OpportunityStatus.APPROVED, OpportunityStatus.REJECTED, OpportunityStatus.SUSPENDED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.APPROVED, EnumSet.of(
            OpportunityStatus.PUBLISHED, OpportunityStatus.ARCHIVED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.PUBLISHED, EnumSet.of(
            OpportunityStatus.CLOSING_SOON, OpportunityStatus.EXPIRED, OpportunityStatus.ARCHIVED, OpportunityStatus.SUSPENDED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.CLOSING_SOON, EnumSet.of(
            OpportunityStatus.EXPIRED, OpportunityStatus.ARCHIVED, OpportunityStatus.SUSPENDED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.EXPIRED, EnumSet.of(
            OpportunityStatus.ARCHIVED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.REJECTED, EnumSet.of(
            OpportunityStatus.DRAFT, OpportunityStatus.ARCHIVED));
        OPPORTUNITY_TRANSITIONS.put(OpportunityStatus.SUSPENDED, EnumSet.of(
            OpportunityStatus.PUBLISHED, OpportunityStatus.ARCHIVED));

        APPLICATION_TRANSITIONS.put(ApplicationStatus.PREPARING, EnumSet.of(
            ApplicationStatus.APPLIED, ApplicationStatus.WITHDRAWN));
        APPLICATION_TRANSITIONS.put(ApplicationStatus.APPLIED, EnumSet.of(
            ApplicationStatus.UNDER_REVIEW, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
        APPLICATION_TRANSITIONS.put(ApplicationStatus.UNDER_REVIEW, EnumSet.of(
            ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED));
        APPLICATION_TRANSITIONS.put(ApplicationStatus.SHORTLISTED, EnumSet.of(
            ApplicationStatus.INTERVIEW, ApplicationStatus.REJECTED));
        APPLICATION_TRANSITIONS.put(ApplicationStatus.INTERVIEW, EnumSet.of(
            ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED));

        REPORT_TRANSITIONS.put(ReportStatus.PENDING, EnumSet.of(
            ReportStatus.REVIEWED));
        REPORT_TRANSITIONS.put(ReportStatus.REVIEWED, EnumSet.of(
            ReportStatus.ACTIONED));
    }

    public static void validateOpportunityTransition(OpportunityStatus from, OpportunityStatus to) {
        if (from == to) return;
        Set<OpportunityStatus> allowed = OPPORTUNITY_TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new BadRequestException(
                "Invalid opportunity status transition: " + from + " -> " + to);
        }
    }

    public static void validateApplicationTransition(ApplicationStatus from, ApplicationStatus to) {
        if (from == to) return;
        Set<ApplicationStatus> allowed = APPLICATION_TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new BadRequestException(
                "Invalid application status transition: " + from + " -> " + to);
        }
    }

    public static void validateReportTransition(ReportStatus from, ReportStatus to) {
        if (from == to) return;
        Set<ReportStatus> allowed = REPORT_TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new BadRequestException(
                "Invalid report status transition: " + from + " -> " + to);
        }
    }

    private LifecycleValidator() {}
}
