-- V3: RBAC, Organization Teams, Opportunity Lifecycle, Moderation, Enhanced Reports
-- Developer 02 Features

-- ============================================
-- 1. RBAC: permissions table
-- ============================================
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    module VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_permissions_module ON permissions(module);
CREATE INDEX idx_permissions_name ON permissions(name);

-- ============================================
-- 2. RBAC: roles table (platform-defined roles)
-- ============================================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_roles_name ON roles(name);

-- ============================================
-- 3. RBAC: role_permissions junction table
-- ============================================
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions(permission_id);

-- ============================================
-- 4. RBAC: user_roles junction table (many-to-many)
-- ============================================
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    granted_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE(user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

-- ============================================
-- 5. Organization members (team management)
-- ============================================
CREATE TABLE organization_members (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL REFERENCES organization_profiles(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    invited_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(organization_id, user_id)
);

CREATE INDEX idx_org_members_org ON organization_members(organization_id);
CREATE INDEX idx_org_members_user ON organization_members(user_id);
CREATE INDEX idx_org_members_status ON organization_members(status);

-- ============================================
-- 6. Organization invitations
-- ============================================
CREATE TABLE organization_invitations (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL REFERENCES organization_profiles(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    token VARCHAR(255) UNIQUE NOT NULL,
    invited_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_org_invitations_org ON organization_invitations(organization_id);
CREATE INDEX idx_org_invitations_email ON organization_invitations(email);
CREATE INDEX idx_org_invitations_token ON organization_invitations(token);
CREATE INDEX idx_org_invitations_status ON organization_invitations(status);

-- ============================================
-- 7. Extend opportunities with full lifecycle fields
-- ============================================
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS source_url VARCHAR(500);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS application_url VARCHAR(500);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS work_mode VARCHAR(50) DEFAULT 'ONSITE';
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS education_level VARCHAR(100);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS experience_level VARCHAR(100);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS funding_info VARCHAR(255);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS eligibility TEXT;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS required_documents TEXT;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS verified_at TIMESTAMP;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS verified_by VARCHAR(255);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS moderated BOOLEAN DEFAULT FALSE;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS moderated_at TIMESTAMP;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS moderated_by VARCHAR(255);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS published_at TIMESTAMP;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS closing_soon_notified BOOLEAN DEFAULT FALSE;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS risk_score VARCHAR(20) DEFAULT 'LOW';
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS view_count BIGINT DEFAULT 0;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS save_count BIGINT DEFAULT 0;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS application_count BIGINT DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_opportunities_verified ON opportunities(verified);
CREATE INDEX IF NOT EXISTS idx_opportunities_moderated ON opportunities(moderated);
CREATE INDEX IF NOT EXISTS idx_opportunities_risk_score ON opportunities(risk_score);
CREATE INDEX IF NOT EXISTS idx_opportunities_work_mode ON opportunities(work_mode);

-- ============================================
-- 8. Opportunity status history (audit trail)
-- ============================================
CREATE TABLE opportunity_status_history (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_opp_status_history_opp ON opportunity_status_history(opportunity_id);
CREATE INDEX idx_opp_status_history_status ON opportunity_status_history(new_status);

-- ============================================
-- 9. Application status history
-- ============================================
CREATE TABLE application_status_history (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_app_status_history_app ON application_status_history(application_id);

-- ============================================
-- 10. Extend applications with candidate management fields
-- ============================================
ALTER TABLE applications ADD COLUMN IF NOT EXISTS shortlisted BOOLEAN DEFAULT FALSE;
ALTER TABLE applications ADD COLUMN IF NOT EXISTS shortlisted_at TIMESTAMP;
ALTER TABLE applications ADD COLUMN IF NOT EXISTS internal_notes TEXT;
ALTER TABLE applications ADD COLUMN IF NOT EXISTS reviewed_by BIGINT REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE applications ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP;

-- ============================================
-- 11. Moderation queue
-- ============================================
CREATE TABLE moderation_actions (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    moderator_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50) NOT NULL,
    reason TEXT,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_moderation_actions_opp ON moderation_actions(opportunity_id);
CREATE INDEX idx_moderation_actions_moderator ON moderation_actions(moderator_id);
CREATE INDEX idx_moderation_actions_action ON moderation_actions(action);

-- ============================================
-- 12. Extend reports with investigation workflow
-- ============================================
ALTER TABLE reports ADD COLUMN IF NOT EXISTS assigned_to BIGINT REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE reports ADD COLUMN IF NOT EXISTS investigation_notes TEXT;
ALTER TABLE reports ADD COLUMN IF NOT EXISTS resolution VARCHAR(255);
ALTER TABLE reports ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP;
ALTER TABLE reports ADD COLUMN IF NOT EXISTS actioned_at TIMESTAMP;

-- ============================================
-- 13. Verification documents (multiple documents per request)
-- ============================================
CREATE TABLE verification_documents (
    id BIGSERIAL PRIMARY KEY,
    verification_request_id BIGINT NOT NULL REFERENCES verification_requests(id) ON DELETE CASCADE,
    document_url VARCHAR(500) NOT NULL,
    document_type VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_verification_docs_req ON verification_documents(verification_request_id);

-- ============================================
-- 14. Deadline reminders
-- ============================================
CREATE TABLE deadline_reminders (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    reminder_days INT NOT NULL,
    sent BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(opportunity_id, reminder_days)
);

CREATE INDEX idx_deadline_reminders_opp ON deadline_reminders(opportunity_id);
CREATE INDEX idx_deadline_reminders_sent ON deadline_reminders(sent);

-- ============================================
-- 15. Risk signals (anti-fraud)
-- ============================================
CREATE TABLE risk_signals (
    id BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    signal_type VARCHAR(100) NOT NULL,
    risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW',
    description TEXT,
    auto_detected BOOLEAN NOT NULL DEFAULT FALSE,
    reviewed BOOLEAN NOT NULL DEFAULT FALSE,
    reviewed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_risk_signals_target ON risk_signals(target_type, target_id);
CREATE INDEX idx_risk_signals_level ON risk_signals(risk_level);
CREATE INDEX idx_risk_signals_type ON risk_signals(signal_type);
CREATE INDEX idx_risk_signals_reviewed ON risk_signals(reviewed);

-- ============================================
-- 16. Platform configuration (super admin)
-- ============================================
CREATE TABLE platform_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description VARCHAR(255),
    updated_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_platform_config_key ON platform_config(config_key);

-- ============================================
-- 17. Extend verification_requests with more fields
-- ============================================
ALTER TABLE verification_requests ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE verification_requests ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE verification_requests ADD COLUMN IF NOT EXISTS requested_information TEXT;

-- ============================================
-- Seed: Platform roles
-- ============================================
INSERT INTO roles (name, description, is_system) VALUES
    ('SUPER_ADMIN', 'Full platform oversight and control', TRUE),
    ('ADMIN', 'Platform administrator', TRUE),
    ('MODERATOR', 'Content moderation', TRUE),
    ('VERIFICATION_OFFICER', 'Organization verification review', TRUE),
    ('ORGANIZATION_OWNER', 'Organization owner', TRUE),
    ('ORGANIZATION_ADMIN', 'Organization administrator', TRUE),
    ('ORGANIZATION_MEMBER', 'Organization team member', TRUE),
    ('SEEKER', 'Job/opportunity seeker', TRUE)
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- Seed: Platform permissions
-- ============================================
INSERT INTO permissions (name, description, module) VALUES
    -- User management
    ('user.read', 'View user profiles', 'users'),
    ('user.write', 'Edit user profiles', 'users'),
    ('user.suspend', 'Suspend user accounts', 'users'),
    ('user.delete', 'Delete user accounts', 'users'),
    ('user.role.change', 'Change user roles', 'users'),

    -- Organization management
    ('org.read', 'View organizations', 'organizations'),
    ('org.write', 'Edit organization profiles', 'organizations'),
    ('org.verify.submit', 'Submit verification request', 'organizations'),
    ('org.verify.review', 'Review verification requests', 'organizations'),
    ('org.team.invite', 'Invite team members', 'organizations'),
    ('org.team.manage', 'Manage team members', 'organizations'),

    -- Opportunity management
    ('opp.create', 'Create opportunities', 'opportunities'),
    ('opp.read', 'View opportunities', 'opportunities'),
    ('opp.update', 'Update opportunities', 'opportunities'),
    ('opp.delete', 'Delete opportunities', 'opportunities'),
    ('opp.publish', 'Publish opportunities', 'opportunities'),
    ('opp.verify', 'Verify opportunities', 'opportunities'),
    ('opp.moderate', 'Moderate opportunities', 'opportunities'),

    -- Application management
    ('application.read', 'View applications', 'applications'),
    ('application.update', 'Update application status', 'applications'),
    ('application.shortlist', 'Shortlist applicants', 'applications'),

    -- Moderation
    ('moderation.read', 'View moderation queue', 'moderation'),
    ('moderation.action', 'Take moderation actions', 'moderation'),

    -- Reports
    ('report.create', 'Create reports', 'reports'),
    ('report.read', 'View reports', 'reports'),
    ('report.investigate', 'Investigate reports', 'reports'),

    -- Analytics
    ('analytics.read', 'View analytics', 'analytics'),
    ('analytics.platform', 'View platform analytics', 'analytics'),

    -- Audit
    ('audit.read', 'View audit logs', 'audit'),

    -- Platform config
    ('config.read', 'View platform configuration', 'platform'),
    ('config.write', 'Update platform configuration', 'platform')
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- Seed: Assign permissions to roles
-- ============================================
-- SUPER_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- ADMIN gets most permissions except config.write
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name NOT IN ('config.write')
ON CONFLICT DO NOTHING;

-- MODERATOR gets moderation and report permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'MODERATOR' AND p.name IN (
    'opp.read', 'opp.moderate', 'moderation.read', 'moderation.action',
    'report.read', 'report.investigate', 'audit.read'
)
ON CONFLICT DO NOTHING;

-- VERIFICATION_OFFICER gets verification permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VERIFICATION_OFFICER' AND p.name IN (
    'org.read', 'org.verify.review', 'audit.read'
)
ON CONFLICT DO NOTHING;

-- ORGANIZATION_OWNER gets org and opportunity management
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ORGANIZATION_OWNER' AND p.name IN (
    'org.read', 'org.write', 'org.verify.submit', 'org.team.invite', 'org.team.manage',
    'opp.create', 'opp.read', 'opp.update', 'opp.delete', 'opp.publish',
    'application.read', 'application.update', 'application.shortlist',
    'analytics.read', 'audit.read'
)
ON CONFLICT DO NOTHING;

-- ORGANIZATION_ADMIN gets most org permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ORGANIZATION_ADMIN' AND p.name IN (
    'org.read', 'org.write', 'org.team.invite', 'org.team.manage',
    'opp.create', 'opp.read', 'opp.update', 'opp.publish',
    'application.read', 'application.update', 'application.shortlist',
    'analytics.read'
)
ON CONFLICT DO NOTHING;

-- ORGANIZATION_MEMBER gets limited permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ORGANIZATION_MEMBER' AND p.name IN (
    'org.read', 'opp.read', 'opp.create', 'opp.update',
    'application.read'
)
ON CONFLICT DO NOTHING;

-- SEEKER gets basic permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SEEKER' AND p.name IN (
    'opp.read', 'report.create'
)
ON CONFLICT DO NOTHING;

-- ============================================
-- Seed: Default platform configuration
-- ============================================
INSERT INTO platform_config (config_key, config_value, description) VALUES
    ('deadline.reminder.days', '30,14,7,3,1', 'Days before deadline to send reminders'),
    ('opportunity.auto_expire', 'true', 'Auto-expire opportunities past deadline'),
    ('opportunity.closing_soon_days', '7', 'Days before deadline to mark as CLOSING_SOON'),
    ('verification.required_for_publish', 'false', 'Require verification before publishing opportunities'),
    ('max_applications_per_opportunity', '500', 'Maximum applications per opportunity'),
    ('risk.auto_flag_threshold', 'HIGH', 'Risk level that triggers automatic flagging')
ON CONFLICT (config_key) DO NOTHING;

-- ============================================
-- Comments
-- ============================================
COMMENT ON TABLE permissions IS 'Platform permissions for RBAC';
COMMENT ON TABLE roles IS 'Platform roles with associated permissions';
COMMENT ON TABLE role_permissions IS 'Many-to-many: roles to permissions';
COMMENT ON TABLE user_roles IS 'Many-to-many: users to roles';
COMMENT ON TABLE organization_members IS 'Organization team membership';
COMMENT ON TABLE organization_invitations IS 'Pending team invitations';
COMMENT ON TABLE opportunity_status_history IS 'Audit trail of opportunity status changes';
COMMENT ON TABLE application_status_history IS 'Audit trail of application status changes';
COMMENT ON TABLE moderation_actions IS 'Moderation actions on opportunities';
COMMENT ON TABLE verification_documents IS 'Documents submitted for org verification';
COMMENT ON TABLE deadline_reminders IS 'Scheduled deadline reminders for opportunities';
COMMENT ON TABLE risk_signals IS 'Anti-fraud risk signals detected on entities';
COMMENT ON TABLE platform_config IS 'Platform-wide configuration settings';
