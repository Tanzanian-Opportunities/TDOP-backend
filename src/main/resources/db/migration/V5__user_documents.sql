-- TDOP Backend - User Documents Migration
-- Add user_documents table for seeker document management

CREATE TABLE user_documents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT,
    document_type VARCHAR(50) NOT NULL DEFAULT 'other',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_documents_user_id ON user_documents(user_id);
CREATE INDEX idx_user_documents_document_type ON user_documents(document_type);
CREATE INDEX idx_user_documents_user_id_doc_type ON user_documents(user_id, document_type);
