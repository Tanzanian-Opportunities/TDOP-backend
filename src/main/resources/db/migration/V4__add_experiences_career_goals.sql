-- TDOP Backend - Phase 2 Migration
-- Add experiences, career_goals tables and update ApplicationStatus

-- ============================================
-- TABLE: experiences
-- ============================================
CREATE TABLE experiences (
    id BIGSERIAL PRIMARY KEY,
    company VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    seeker_profile_id BIGINT REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMP
);

CREATE INDEX idx_experiences_user_id ON experiences(user_id);
CREATE INDEX idx_experiences_seeker_profile_id ON experiences(seeker_profile_id);
CREATE INDEX idx_experiences_company ON experiences(company);
CREATE INDEX idx_experiences_title ON experiences(title);

-- ============================================
-- TABLE: career_goals
-- ============================================
CREATE TABLE career_goals (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    target_industry VARCHAR(255),
    target_role VARCHAR(255),
    timeline VARCHAR(255),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    seeker_profile_id BIGINT REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMP
);

CREATE INDEX idx_career_goals_user_id ON career_goals(user_id);
CREATE INDEX idx_career_goals_seeker_profile_id ON career_goals(seeker_profile_id);
