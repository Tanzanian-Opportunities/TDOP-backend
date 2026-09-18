-- Applications seeded with the old ApplicationStatus values (REVIEWED/PENDING)
-- are not valid in the current ApplicationStatus enum (APPLIED / UNDER_REVIEW ...).
-- Normalise existing rows so the deadline engine and JPA enum mapping work.
UPDATE applications SET status = 'UNDER_REVIEW' WHERE status = 'REVIEWED';
UPDATE applications SET status = 'APPLIED' WHERE status = 'PENDING';