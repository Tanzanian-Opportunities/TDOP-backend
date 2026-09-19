-- Escalations table
CREATE TABLE escalations (
    id BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    escalated_by BIGINT REFERENCES users(id),
    assigned_to BIGINT REFERENCES users(id),
    resolution TEXT,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Appeals table
CREATE TABLE appeals (
    id BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    appellant_id BIGINT REFERENCES users(id),
    reviewed_by BIGINT REFERENCES users(id),
    review_notes TEXT,
    resolution TEXT,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_escalations_status ON escalations(status);
CREATE INDEX idx_escalations_target ON escalations(target_type, target_id);
CREATE INDEX idx_appeals_status ON appeals(status);
CREATE INDEX idx_appeals_target ON appeals(target_type, target_id);
