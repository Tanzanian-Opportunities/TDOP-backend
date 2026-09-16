-- ============================================
-- SAMPLE ORGANIZATIONS (V8)
-- Adds 2 more organizations so the Organizations
-- browse tab shows 5 samples total (3 seeded in
-- V2 + these 2).
-- IDENTITY: users 12-13, org_profiles 4-5
-- ============================================

-- helper: ensure sequence state consistent
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));

-- ============================================
-- ORGANIZATION USER ACCOUNTS (ids 12-13)
-- bcrypt hash reuse pattern from V2 seed
-- ============================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, verified, created_at, updated_at) VALUES
(12, 'careers@vodacom.co.tz', '$2a$10$zg3ZsOon2W.gyDbJLgcxj.Q0ChWXE09/9yBnBoG1uqCnbXVddZ4lC', 'Vodacom Tanzania PLC', '+255760000024', 'ORGANIZATION', TRUE, TRUE, '2024-03-05 09:00:00', '2024-03-05 09:00:00'),
(13, 'hr@nmbbank.com', '$2a$10$zg3ZsOon2W.gyDbJLgcxj.Q0ChWXE09/9yBnBoG1uqCnbXVddZ4lC', 'NMB Bank PLC', '+255760000025', 'ORGANIZATION', TRUE, FALSE, '2024-03-10 10:00:00', '2024-03-10 10:00:00');

-- ============================================
-- ORGANIZATION PROFILES (ids 4-5)
-- ============================================
INSERT INTO organization_profiles (id, user_id, org_name, description, website, industry, size, logo, verified, verified_at, verification_document, created_at) VALUES
(4, 12, 'Vodacom Tanzania PLC', 'Tanzania''s leading mobile telecommunications company delivering voice, data, and M-Pesa mobile money services to millions of customers nationwide.', 'https://www.vodacom.co.tz', 'Telecommunications', '1000+', 'https://cdn.tdop.go.tz/logos/vodacom.png', TRUE, '2024-03-06 09:00:00', 'https://cdn.tdop.go.tz/docs/0022_verified.pdf', '2024-03-05 09:00:00'),
(5, 13, 'NMB Bank PLC', 'One of Tanzania''s most trusted banks offering retail and corporate banking, digital financial services, and inclusive financing for SMEs and agriculture.', 'https://www.nmbbank.co.tz', 'Finance & Banking', '1000+', 'https://cdn.tdop.go.tz/logos/nmb.png', FALSE, NULL, NULL, '2024-03-10 10:00:00');

SELECT setval(pg_get_serial_sequence('organization_profiles', 'id'), (SELECT MAX(id) FROM organization_profiles));
