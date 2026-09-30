-- V10: Security hardening — data integrity constraints, missing indexes, nullable fixes
-- Phase 2: Data Integrity

-- ============================================
-- 1. Add missing NOT NULL constraints
-- ============================================
ALTER TABLE applications ALTER COLUMN status SET DEFAULT 'PREPARING';
UPDATE applications SET status = 'PREPARING' WHERE status IS NULL;
ALTER TABLE applications ALTER COLUMN status SET NOT NULL;

ALTER TABLE reports ALTER COLUMN status SET DEFAULT 'PENDING';
UPDATE reports SET status = 'PENDING' WHERE status IS NULL;
ALTER TABLE reports ALTER COLUMN status SET NOT NULL;

ALTER TABLE reports ALTER COLUMN target_type SET NOT NULL;
ALTER TABLE reports ALTER COLUMN target_id SET NOT NULL;

ALTER TABLE verification_requests ALTER COLUMN status SET DEFAULT 'PENDING';
UPDATE verification_requests SET status = 'PENDING' WHERE status IS NULL;
ALTER TABLE verification_requests ALTER COLUMN status SET NOT NULL;

-- ============================================
-- 2. Add missing indexes for query performance
-- ============================================
CREATE INDEX IF NOT EXISTS idx_opportunities_status_deadline ON opportunities(status, deadline);
CREATE INDEX IF NOT EXISTS idx_applications_status ON applications(status);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp_entity ON audit_logs(timestamp, entity_type);
CREATE INDEX IF NOT EXISTS idx_reports_status_created ON reports(status, created_at);

-- ============================================
-- 3. Ensure users.email unique index exists (it does, but confirming)
-- ============================================
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_unique ON users(email);

-- ============================================
-- 4. Add index for saved_opportunities lookup pattern
-- ============================================
CREATE INDEX IF NOT EXISTS idx_saved_opportunities_user_saved ON saved_opportunities(user_id, saved_at DESC);

-- ============================================
-- 5. Add composite index for opportunity search
-- ============================================
CREATE INDEX IF NOT EXISTS idx_opportunities_category_status ON opportunities(category, status);
CREATE INDEX IF NOT EXISTS idx_opportunities_location_status ON opportunities(location, status);
