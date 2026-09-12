-- TDOP Backend - Initial Schema
-- Tanzania Opportunities Platform

-- ============================================
-- TABLE: users
-- ============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_verified ON users(verified);
CREATE INDEX idx_users_created_at ON users(created_at);

-- ============================================
-- TABLE: seeker_profiles
-- ============================================
CREATE TABLE seeker_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bio VARCHAR(255),
    location VARCHAR(255),
    profile_picture VARCHAR(255),
    created_at TIMESTAMP
);

CREATE INDEX idx_seeker_profiles_user_id ON seeker_profiles(user_id);
CREATE INDEX idx_seeker_profiles_location ON seeker_profiles(location);

-- ============================================
-- TABLE: organization_profiles
-- ============================================
CREATE TABLE organization_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    org_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    website VARCHAR(255),
    industry VARCHAR(255),
    size VARCHAR(255),
    logo VARCHAR(255),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP,
    verification_document VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_organization_profiles_user_id ON organization_profiles(user_id);
CREATE INDEX idx_organization_profiles_org_name ON organization_profiles(org_name);
CREATE INDEX idx_organization_profiles_verified ON organization_profiles(verified);
CREATE INDEX idx_organization_profiles_industry ON organization_profiles(industry);

-- ============================================
-- TABLE: skills
-- ============================================
CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    category VARCHAR(255),
    level VARCHAR(255),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    seeker_profile_id BIGINT REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_skills_user_id ON skills(user_id);
CREATE INDEX idx_skills_seeker_profile_id ON skills(seeker_profile_id);
CREATE INDEX idx_skills_name ON skills(name);
CREATE INDEX idx_skills_category ON skills(category);
CREATE INDEX idx_skills_level ON skills(level);

-- ============================================
-- TABLE: education
-- ============================================
CREATE TABLE education (
    id BIGSERIAL PRIMARY KEY,
    institution VARCHAR(255),
    degree VARCHAR(255),
    field_of_study VARCHAR(255),
    start_date DATE,
    end_date DATE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    seeker_profile_id BIGINT REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_education_user_id ON education(user_id);
CREATE INDEX idx_education_seeker_profile_id ON education(seeker_profile_id);
CREATE INDEX idx_education_institution ON education(institution);
CREATE INDEX idx_education_field_of_study ON education(field_of_study);
CREATE INDEX idx_education_degree ON education(degree);

-- ============================================
-- TABLE: interests
-- ============================================
CREATE TABLE interests (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(255),
    description VARCHAR(255),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    seeker_profile_id BIGINT REFERENCES seeker_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_interests_user_id ON interests(user_id);
CREATE INDEX idx_interests_seeker_profile_id ON interests(seeker_profile_id);
CREATE INDEX idx_interests_category ON interests(category);

-- ============================================
-- TABLE: opportunities
-- ============================================
CREATE TABLE opportunities (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT,
    benefits TEXT,
    location VARCHAR(255),
    type VARCHAR(255),
    category VARCHAR(255),
    salary_range VARCHAR(255),
    tags VARCHAR(255),
    deadline TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_by BIGINT REFERENCES organization_profiles(id) ON DELETE SET NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_opportunities_status ON opportunities(status);
CREATE INDEX idx_opportunities_type ON opportunities(type);
CREATE INDEX idx_opportunities_category ON opportunities(category);
CREATE INDEX idx_opportunities_location ON opportunities(location);
CREATE INDEX idx_opportunities_created_by ON opportunities(created_by);
CREATE INDEX idx_opportunities_deadline ON opportunities(deadline);
CREATE INDEX idx_opportunities_title ON opportunities(title);
CREATE INDEX idx_opportunities_status_created_at ON opportunities(status, created_at);

-- ============================================
-- TABLE: applications
-- ============================================
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    applicant_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(255),
    cover_letter TEXT,
    resume_url VARCHAR(255),
    applied_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_applications_opportunity_id ON applications(opportunity_id);
CREATE INDEX idx_applications_applicant_id ON applications(applicant_id);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_applicant_opportunity ON applications(applicant_id, opportunity_id);
CREATE UNIQUE INDEX idx_applications_unique ON applications(applicant_id, opportunity_id);

-- ============================================
-- TABLE: saved_opportunities
-- ============================================
CREATE TABLE saved_opportunities (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    saved_at TIMESTAMP,
    UNIQUE(user_id, opportunity_id)
);

CREATE INDEX idx_saved_opportunities_user_id ON saved_opportunities(user_id);
CREATE INDEX idx_saved_opportunities_opportunity_id ON saved_opportunities(opportunity_id);

-- ============================================
-- TABLE: audit_logs
-- ============================================
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id BIGINT,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    old_value VARCHAR(255),
    new_value VARCHAR(255),
    timestamp TIMESTAMP,
    ip_address VARCHAR(255)
);

CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_timestamp_action ON audit_logs(timestamp, action);

-- ============================================
-- TABLE: notifications
-- ============================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255),
    message TEXT NOT NULL,
    type VARCHAR(255),
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(read);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

-- ============================================
-- TABLE: reports
-- ============================================
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    target_type VARCHAR(255),
    target_id BIGINT,
    reason VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP
);

CREATE INDEX idx_reports_reporter_id ON reports(reporter_id);
CREATE INDEX idx_reports_target_type ON reports(target_type);
CREATE INDEX idx_reports_target_id ON reports(target_id);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_created_at ON reports(created_at);

-- ============================================
-- TABLE: verification_requests
-- ============================================
CREATE TABLE verification_requests (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL REFERENCES organization_profiles(id) ON DELETE CASCADE,
    document VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    reviewed_by VARCHAR(255),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP
);

CREATE INDEX idx_verification_requests_org_id ON verification_requests(organization_id);
CREATE INDEX idx_verification_requests_status ON verification_requests(status);
CREATE INDEX idx_verification_requests_reviewed_by ON verification_requests(reviewed_by);

-- ============================================
-- updated_at trigger
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_opportunities_updated_at BEFORE UPDATE ON opportunities
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_applications_updated_at BEFORE UPDATE ON applications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_organization_profiles_updated_at BEFORE UPDATE ON organization_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- COMMENTS
-- ============================================
COMMENT ON TABLE users IS 'Platform users - seekers, organizations, and admins';
COMMENT ON TABLE opportunities IS 'Job and opportunity listings posted by organizations';
COMMENT ON TABLE applications IS 'Applications submitted by seekers to opportunities';
COMMENT ON TABLE organization_profiles IS 'Organization details and verification status';