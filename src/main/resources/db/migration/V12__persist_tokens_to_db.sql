-- V12: Persist password reset tokens and revoked tokens to DB

-- Password reset tokens (replaces in-memory ConcurrentHashMap)
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_prt_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_prt_email ON password_reset_tokens(email);

-- Revoked tokens (replaces in-memory ConcurrentHashMap)
CREATE TABLE IF NOT EXISTS revoked_tokens (
    id BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_rt_hash ON revoked_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_rt_expires ON revoked_tokens(expires_at);
