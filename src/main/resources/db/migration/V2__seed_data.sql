-- TDOP Backend - Seed Data
-- Tanzania Opportunities Platform

-- ============================================
-- ADMIN USERS (3)
-- BCrypt hashes for: admin123, superadmin, admin2024
-- ============================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, verified, created_at, updated_at) VALUES
(1, 'admin@tdop.go.tz', '$2a$10$oqYgDBDf6lY.nDHFXNnpIuulAmkyfkBF9DqC0bJ68/5kjbh102F2.', 'Admin User One', '+255710000001', 'ADMIN', TRUE, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(2, 'superadmin@tdop.go.tz', '$2a$10$kRm5CtrRyhG17I6mdAhXaeeYDFr8etfIiQN8G8vvTW9kRPNLnkDPy', 'Super Administrator', '+255710000002', 'ADMIN', TRUE, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(3, 'admin2024@tdop.go.tz', '$2a$10$.UtimmXWMgfcrawHm/1wLux3Bx4r6/ZCnfcDvThA40I2Rfvu0Ezx.', 'Admin User Two', '+255710000003', 'ADMIN', TRUE, TRUE, '2024-01-15 09:00:00', '2024-01-15 09:00:00');

-- ============================================
-- SEEKER USERS (5)
-- ============================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, verified, created_at, updated_at) VALUES
(4, 'john.mwangi@email.com', '$2a$10$aQmaRCK9jUBl/Oapalut7elmtnQwsyUwQvKfdzowitjjrUfSLft.K', 'John Mwangi', '+255754000011', 'SEEKER', TRUE, TRUE, '2024-02-01 10:00:00', '2024-02-01 10:00:00'),
(5, 'amina.hassan@email.com', '$2a$10$aQmaRCK9jUBl/Oapalut7elmtnQwsyUwQvKfdzowitjjrUfSLft.K', 'Amina Hassan', '+255754000012', 'SEEKER', TRUE, TRUE, '2024-02-05 11:00:00', '2024-02-05 11:00:00'),
(6, 'peter.okech@email.com', '$2a$10$aQmaRCK9jUBl/Oapalut7elmtnQwsyUwQvKfdzowitjjrUfSLft.K', 'Peter Okech', '+255754000013', 'SEEKER', TRUE, FALSE, '2024-02-10 12:00:00', '2024-02-10 12:00:00'),
(7, 'fatima.bakari@email.com', '$2a$10$aQmaRCK9jUBl/Oapalut7elmtnQwsyUwQvKfdzowitjjrUfSLft.K', 'Fatima Bakari', '+255754000014', 'SEEKER', TRUE, TRUE, '2024-02-15 13:00:00', '2024-02-15 13:00:00'),
(8, 'david.mwinyi@email.com', '$2a$10$aQmaRCK9jUBl/Oapalut7elmtnQwsyUwQvKfdzowitjjrUfSLft.K', 'David Mwinyi', '+255754000015', 'SEEKER', TRUE, TRUE, '2024-02-20 14:00:00', '2024-02-20 14:00:00');

-- ============================================
-- ORGANIZATION USERS (3 - 1 verified, 2 pending)
-- ============================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, verified, created_at, updated_at) VALUES
(9, 'info@tanzgold.com', '$2a$10$zg3ZsOon2W.gyDbJLgcxj.Q0ChWXE09/9yBnBoG1uqCnbXVddZ4lC', 'Tanzania Gold Mining Ltd', '+255760000021', 'ORGANIZATION', TRUE, TRUE, '2024-01-20 09:00:00', '2024-01-20 09:00:00'),
(10, 'contact@safaricomTZ.com', '$2a$10$zg3ZsOon2W.gyDbJLgcxj.Q0ChWXE09/9yBnBoG1uqCnbXVddZ4lC', 'Safaricom Tanzania PLC', '+255760000022', 'ORGANIZATION', TRUE, FALSE, '2024-01-25 10:00:00', '2024-01-25 10:00:00'),
(11, 'hr@crdbbank.com', '$2a$10$zg3ZsOon2W.gyDbJLgcxj.Q0ChWXE09/9yBnBoG1uqCnbXVddZ4lC', 'CRDB Bank PLC', '+255760000023', 'ORGANIZATION', TRUE, FALSE, '2024-02-01 11:00:00', '2024-02-01 11:00:00');

-- ============================================
-- SEEKER PROFILES
-- ============================================
INSERT INTO seeker_profiles (id, user_id, bio, location, profile_picture, created_at) VALUES
(1, 4, 'Experienced software developer with 5 years in web development. Passionate about building solutions for East Africa.', 'Dar es Salaam', 'https://cdn.tdop.go.tz/profiles/0011.jpg', '2024-02-01 10:05:00'),
(2, 5, 'Data analyst specializing in agricultural data. Interested in improving food security through technology.', 'Arusha', 'https://cdn.tdop.go.tz/profiles/0012.jpg', '2024-02-05 11:05:00'),
(3, 6, 'Recent graduate looking for entry-level opportunities. Eager to learn and contribute.', 'Mwanza', NULL, '2024-02-10 12:05:00'),
(4, 7, 'Marketing professional with experience in digital campaigns and brand management.', 'Dodoma', 'https://cdn.tdop.go.tz/profiles/0014.jpg', '2024-02-15 13:05:00'),
(5, 8, 'Financial analyst with CFA certification. Skilled in risk assessment and investment analysis.', 'Dar es Salaam', 'https://cdn.tdop.go.tz/profiles/0015.jpg', '2024-02-20 14:05:00');

-- ============================================
-- ORGANIZATION PROFILES
-- ============================================
INSERT INTO organization_profiles (id, user_id, org_name, description, website, industry, size, logo, verified, verified_at, verification_document, created_at) VALUES
(1, 9, 'Tanzania Gold Mining Ltd', 'Leading gold mining company in Tanzania with over 20 years of experience in mineral extraction and processing.', 'https://www.tanzgold.com', 'Mining', '500+', 'https://cdn.tdop.go.tz/logos/tanzgold.png', TRUE, '2024-01-22 10:00:00', 'https://cdn.tdop.go.tz/docs/0021_verified.pdf', '2024-01-20 09:00:00'),
(2, 10, 'Safaricom Tanzania PLC', 'Premier telecommunications provider in East Africa. Offering innovative digital solutions across Tanzania.', 'https://www.safaricom.co.tz', 'Telecommunications', '1000+', 'https://cdn.tdop.go.tz/logos/safaricom.png', FALSE, NULL, NULL, '2024-01-25 10:00:00'),
(3, 11, 'CRDB Bank PLC', 'One of Tanzania''s largest banks providing comprehensive financial services including banking, insurance, and investment.', 'https://www.crdb.co.tz', 'Finance & Banking', '1000+', 'https://cdn.tdop.go.tz/logos/crdb.png', FALSE, NULL, NULL, '2024-02-01 11:00:00');

-- ============================================
-- SKILLS (10 sample skills)
-- ============================================
INSERT INTO skills (id, name, category, level, user_id, seeker_profile_id) VALUES
(1, 'Software Development', 'Technology', 'ADVANCED', 4, 1),
(2, 'Data Analysis', 'Technology', 'INTERMEDIATE', 4, 1),
(3, 'Data Analysis', 'Technology', 'ADVANCED', 5, 2),
(4, 'Marketing', 'Business', 'INTERMEDIATE', 5, 2),
(5, 'Project Management', 'Business', 'BEGINNER', 6, 3),
(6, 'Communication', 'Soft Skills', 'BEGINNER', 6, 3),
(7, 'Marketing', 'Business', 'ADVANCED', 7, 4),
(8, 'Financial Analysis', 'Finance', 'ADVANCED', 8, 5),
(9, 'Leadership', 'Soft Skills', 'INTERMEDIATE', 4, 1),
(10, 'Design', 'Creative', 'INTERMEDIATE', 8, 5);

-- ============================================
-- EDUCATION RECORDS
-- ============================================
INSERT INTO education (id, user_id, seeker_profile_id, institution, degree, field_of_study, start_date, end_date) VALUES
(1, 4, 1, 'University of Dar es Salaam', 'Master''s', 'Computer Science', '2019-09-01', '2022-06-30'),
(2, 4, 1, 'Dar es Salaam Institute of Technology', 'Bachelor''s', 'Software Engineering', '2015-09-01', '2019-06-30'),
(3, 5, 2, 'Sokoine University of Agriculture', 'Master''s', 'Agricultural Data Science', '2020-09-01', '2022-06-30'),
(4, 6, 3, 'Mzumbe University', 'Bachelor''s', 'Business Administration', '2020-09-01', '2024-06-30'),
(5, 7, 4, 'University of Dar es Salaam', 'Bachelor''s', 'Marketing', '2018-09-01', '2022-06-30'),
(6, 8, 5, 'University of Dar es Salaam', 'Master''s', 'Finance', '2019-09-01', '2021-06-30'),
(7, 8, 5, 'Institute of Finance Management', 'Bachelor''s', 'Banking and Finance', '2015-09-01', '2019-06-30');

-- ============================================
-- INTERESTS
-- ============================================
INSERT INTO interests (id, user_id, seeker_profile_id, category, description) VALUES
(1, 4, 1, 'Technology', 'Interested in AI, machine learning, and cloud computing solutions'),
(2, 4, 1, 'Entrepreneurship', 'Passionate about startups and tech innovation in Tanzania'),
(3, 5, 2, 'Agriculture', 'Sustainable agriculture and agri-tech innovation'),
(4, 5, 2, 'Data Science', 'Big data analytics and predictive modeling'),
(5, 7, 4, 'Digital Marketing', 'SEO, SEM, social media marketing strategies'),
(6, 8, 5, 'Investment', 'Stock market analysis and portfolio management');

-- ============================================
-- OPPORTUNITIES (10 sample opportunities)
-- ============================================
INSERT INTO opportunities (id, title, description, requirements, benefits, location, type, category, salary_range, deadline, status, created_by, tags, created_at, updated_at) VALUES
(1, 'Senior Software Engineer', 'We are seeking an experienced software engineer to join our development team in Dar es Salaam. You will be responsible for designing, implementing, and maintaining scalable web applications.', '5+ years of experience, Proficiency in Java/Python, Strong knowledge of databases, Leadership skills', 'Competitive salary, Health insurance, Professional development fund, Housing allowance', 'Dar es Salaam', 'FULL_TIME', 'Technology', 'TZS 3,000,000 - 5,000,000', '2024-04-30 23:59:59', 'PUBLISHED', 1, 'software, engineering, senior', '2024-03-01 09:00:00', '2024-03-01 09:00:00'),
(2, 'Data Analyst Intern', 'Join our data analytics team to help process and analyze agricultural data for sustainable farming initiatives across Tanzania.', 'Currently pursuing degree in Statistics/Data Science, Knowledge of Python/R, Strong analytical skills', 'Stipend, Practical experience, Certificate of completion, Mentorship', 'Arusha', 'INTERNSHIP', 'Data Science', 'TZS 500,000 - 800,000', '2024-04-15 23:59:59', 'PUBLISHED', 1, 'data, analytics, internship', '2024-03-05 10:00:00', '2024-03-05 10:00:00'),
(3, 'Marketing Manager', 'Lead our marketing team and develop strategic campaigns to expand our telecommunications services across East Africa.', '7+ years marketing experience, MBA preferred, Team leadership, Digital marketing expertise', 'Salary TZS 4,000,000-6,000,000, Company car, Medical aid, Performance bonuses', 'Dar es Salaam', 'FULL_TIME', 'Marketing', 'TZS 4,000,000 - 6,000,000', '2024-04-20 23:59:59', 'DRAFT', 2, 'marketing, management, telecom', '2024-03-10 11:00:00', '2024-03-10 11:00:00'),
(4, 'Financial Advisor', 'Provide financial advisory services to clients in our banking division. Help clients achieve their financial goals through expert guidance.', 'CFA or equivalent certification, 5+ years experience in banking, Strong knowledge of Tanzanian financial regulations', 'Commission-based, TZS 3,500,000+ base, Training budget, Loan facilities', 'Dar es Salaam', 'FULL_TIME', 'Finance', 'TZS 3,500,000 - 5,500,000', '2024-05-01 23:59:59', 'PUBLISHED', 3, 'finance, banking, advisory', '2024-03-12 12:00:00', '2024-03-12 12:00:00'),
(5, 'Civil Site Engineer', 'Oversee construction projects for mining infrastructure including roads, bridges, and facility construction in rural Tanzania.', 'Bachelor''s in Civil Engineering, 4+ years construction experience, Willing to work in remote areas', 'Field allowance, Accommodation provided, Safety equipment, Annual leave', 'Mining Sites', 'FULL_TIME', 'Engineering', 'TZS 2,500,000 - 4,000,000', '2024-04-25 23:59:59', 'PUBLISHED', 1, 'civil, construction, mining', '2024-03-15 14:00:00', '2024-03-15 14:00:00'),
(6, 'Content Creator', 'Create engaging digital content for our social media channels and website. Highlight stories of Tanzanian entrepreneurs and professionals.', '2+ years content creation experience, Proficiency in Adobe Creative Suite, Understanding of social media trends', 'TZS 1,500,000-2,500,000, Flexible working hours, Equipment provided, Creative environment', 'Dar es Salaam', 'PART_TIME', 'Media', 'TZS 1,500,000 - 2,500,000', '2024-04-10 23:59:59', 'PUBLISHED', 2, 'content, social media, creative', '2024-03-18 09:30:00', '2024-03-18 09:30:00'),
(7, 'HR Officer', 'Manage recruitment, employee relations, and HR operations for our growing organization in Tanzania.', 'Degree in HR Management, 3+ years HR experience, Knowledge of Tanzanian labor laws', 'TZS 2,500,000-3,500,000, Training opportunities, Pension scheme, Annual bonus', 'Dar es Salaam', 'FULL_TIME', 'Human Resources', 'TZS 2,500,000 - 3,500,000', '2024-05-10 23:59:59', 'DRAFT', 3, 'hr, recruitment, human resources', '2024-03-20 10:00:00', '2024-03-20 10:00:00'),
(8, 'Junior Data Scientist', 'Entry-level position for recent graduates passionate about data science and machine learning applied to agricultural development.', 'Degree in Computer Science/Statistics, Knowledge of Python, Machine learning fundamentals, Strong academic record', 'Training program, Mentorship, TZS 1,200,000-2,000,000, Conference attendance', 'Morogoro', 'FULL_TIME', 'Data Science', 'TZS 1,200,000 - 2,000,000', '2024-05-15 23:59:59', 'PUBLISHED', 1, 'data science, machine learning, junior', '2024-03-22 15:00:00', '2024-03-22 15:00:00'),
(9, 'Graphic Designer', 'Design marketing materials, website graphics, and branding elements for our banking division.', 'Portfolio of design work, Proficiency in Adobe Creative Suite, Understanding of brand identity', 'TZS 1,500,000-2,500,000, Creative workspace, Flexible hours, Design tools', 'Dar es Salaam', 'CONTRACT', 'Design', 'TZS 1,500,000 - 2,500,000', '2024-04-05 23:59:59', 'ARCHIVED', 3, 'design, graphic, creative', '2024-03-25 11:00:00', '2024-04-01 16:00:00'),
(10, 'Project Coordinator', 'Coordinate mining community development projects, liaise with local communities, and ensure project milestones are met.', 'Degree in Project Management or related field, 3+ years coordination experience, Excellent communication skills', 'TZS 2,800,000-4,000,000, Field allowance, Travel budget, Housing support', 'Mining Sites', 'FULL_TIME', 'Project Management', 'TZS 2,800,000 - 4,000,000', '2024-05-20 23:59:59', 'PUBLISHED', 1, 'project management, coordination, mining', '2024-03-28 09:00:00', '2024-03-28 09:00:00');

-- ============================================
-- APPLICATIONS
-- ============================================
INSERT INTO applications (id, opportunity_id, applicant_id, status, cover_letter, resume_url, applied_at, updated_at) VALUES
(1, 1, 4, 'REVIEWED', 'I am excited to apply for the Senior Software Engineer position. With 5 years of experience in web development and a Master''s in Computer Science from UDSM, I am confident I can contribute effectively to your team.', 'https://cdn.tdop.go.tz/resumes/0011_resume.pdf', '2024-03-02 09:00:00', '2024-03-05 14:00:00'),
(2, 1, 8, 'PENDING', 'As a financial analyst looking to transition into tech, I bring strong analytical and problem-solving skills that complement software engineering.', 'https://cdn.tdop.go.tz/resumes/0015_resume.pdf', '2024-03-03 10:00:00', '2024-03-03 10:00:00'),
(3, 8, 5, 'INTERVIEW', 'I am a recent graduate from Sokoine University with a Master''s in Agricultural Data Science. I am passionate about applying data science to solve agricultural challenges in Tanzania.', 'https://cdn.tdop.go.tz/resumes/0012_resume.pdf', '2024-03-23 11:00:00', '2024-03-28 16:00:00'),
(4, 5, 6, 'PENDING', 'I recently graduated with a Bachelor''s in Civil Engineering from Mzumbe University and am eager to gain field experience in mining infrastructure.', 'https://cdn.tdop.go.tz/resumes/0013_resume.pdf', '2024-03-16 09:00:00', '2024-03-16 09:00:00'),
(5, 5, 4, 'REJECTED', 'I have the required qualifications but I am currently committed to another project and cannot relocate to mining sites.', 'https://cdn.tdop.go.tz/resumes/0011_resume_alt.pdf', '2024-03-17 10:00:00', '2024-03-20 11:00:00'),
(6, 2, 5, 'REVIEWED', 'As a current Master''s student in Agricultural Data Science, I am a perfect fit for this internship to combine my academic knowledge with practical experience.', 'https://cdn.tdop.go.tz/resumes/0012_resume_intern.pdf', '2024-03-06 09:00:00', '2024-03-08 12:00:00');

-- ============================================
-- SAVED OPPORTUNITIES
-- ============================================
INSERT INTO saved_opportunities (id, user_id, opportunity_id, saved_at) VALUES
(1, 4, 1, '2024-03-01 09:05:00'),
(2, 4, 8, '2024-03-22 15:05:00'),
(3, 5, 2, '2024-03-05 10:05:00'),
(4, 5, 4, '2024-03-12 12:05:00'),
(5, 7, 6, '2024-03-18 09:35:00'),
(6, 8, 1, '2024-03-03 10:05:00'),
(7, 8, 4, '2024-03-12 12:05:00'),
(8, 6, 5, '2024-03-16 09:05:00');

-- ============================================
-- AUDIT LOGS
-- ============================================
INSERT INTO audit_logs (id, action, entity_type, entity_id, user_id, old_value, new_value, timestamp, ip_address) VALUES
(1, 'CREATE', 'opportunity', 1, 9, NULL, '{"title": "Senior Software Engineer", "status": "PUBLISHED"}', '2024-03-01 09:00:00', '197.156.50.1'),
(2, 'APPLICATION', 'application', 1, 4, NULL, '{"opportunity_id": 1, "status": "REVIEWED"}', '2024-03-02 09:00:00', '197.156.50.2'),
(3, 'VERIFY', 'organization', 1, 1, NULL, '{"verified": true, "verified_at": "2024-01-22T10:00:00"}', '2024-01-22 10:00:00', '197.156.50.3'),
(4, 'UPDATE', 'opportunity', 9, 11, '{"status": "PUBLISHED"}', '{"status": "ARCHIVED"}', '2024-04-01 16:00:00', '197.156.50.4'),
(5, 'APPLICATION', 'application', 3, 5, '{"status": "PENDING"}', '{"status": "INTERVIEW"}', '2024-03-28 16:00:00', '197.156.50.5'),
(6, 'CREATE', 'verification_request', 1, 10, NULL, '{"document": "company_registration.pdf", "status": "PENDING"}', '2024-01-25 10:05:00', '197.156.50.2'),
(7, 'LOGIN', 'session', NULL, 4, NULL, '{"ip": "197.156.50.2", "device": "Chrome"}', '2024-03-01 08:55:00', '197.156.50.2'),
(8, 'SAVE', 'saved_opportunity', 1, 4, NULL, '{"opportunity_id": 1}', '2024-03-01 09:05:00', '197.156.50.2'),
(9, 'CREATE', 'verification_request', 2, 11, NULL, '{"document": "financial_license.pdf", "status": "PENDING"}', '2024-02-01 11:05:00', '197.156.50.4'),
(10, 'ROLE_CHANGE', 'user', 10, 1, '{"role": "ADMIN"}', '{"role": "ORGANIZATION"}', '2024-02-01 09:00:00', '197.156.50.3');

-- ============================================
-- NOTIFICATIONS
-- ============================================
INSERT INTO notifications (id, user_id, title, message, type, read, created_at) VALUES
(1, 4, 'Application Reviewed', 'Your application for Senior Software Engineer has been reviewed. The employer would like to schedule an interview.', 'APPLICATION', FALSE, '2024-03-05 14:00:00'),
(2, 5, 'Application Accepted', 'Congratulations! Your application for Junior Data Scientist has moved forward in the process. Please check your email for next steps.', 'APPLICATION', FALSE, '2024-03-28 16:00:00'),
(3, 6, 'Application Rejected', 'We regret to inform you that your application for Civil Site Engineer was not successful. We encourage you to apply for other opportunities.', 'APPLICATION', TRUE, '2024-03-20 11:00:00'),
(4, 9, 'New Application Received', 'You have received a new application from John Mwangi for the Senior Software Engineer position.', 'APPLICATION', FALSE, '2024-03-02 09:00:00'),
(5, 9, 'Organization Verified', 'Your organization Tanzania Gold Mining Ltd has been successfully verified. You can now post unlimited opportunities.', 'VERIFICATION', TRUE, '2024-01-22 10:00:00'),
(6, 4, 'New Opportunity Available', 'A new Senior Software Engineer position has been posted by Tanzania Gold Mining Ltd.', 'APPLICATION', TRUE, '2024-03-01 09:05:00'),
(7, 10, 'Verification Request Submitted', 'Your verification request for Safaricom Tanzania PLC is pending review. Please provide additional documentation if requested.', 'VERIFICATION', FALSE, '2024-01-25 10:05:00'),
(8, 8, 'Application Status Update', 'Your application for Senior Software Engineer has been moved to REVIEWED status.', 'APPLICATION', TRUE, '2024-03-05 14:00:00');

-- ============================================
-- REPORTS
-- ============================================
INSERT INTO reports (id, reporter_id, target_type, target_id, reason, description, status, created_at) VALUES
(1, 6, 'OPPORTUNITY', 9, 'SPAM', 'This opportunity appears to be a duplicate listing. It was already posted and closed.', 'PENDING', '2024-03-29 14:00:00'),
(2, 4, 'USER', 6, 'INAPPROPRIATE_CONTENT', 'User profile contains misleading information about qualifications.', 'REVIEWED', '2024-03-25 10:00:00'),
(3, 7, 'OPPORTUNITY', 4, 'MISLEADING_SALARY', 'The salary range mentioned does not match what was discussed during the interview process.', 'PENDING', '2024-04-01 09:00:00'),
(4, 5, 'USER', 10, 'UNAUTHORIZED_POSTING', 'Organization account is posting opportunities without proper verification.', 'ACTIONED', '2024-03-20 11:00:00');

-- ============================================
-- VERIFICATION REQUESTS
-- ============================================
INSERT INTO verification_requests (id, organization_id, document, status, reviewed_by, reviewed_at, created_at) VALUES
(1, 1, 'company_registration_verified.pdf', 'APPROVED', '1', '2024-01-22 09:00:00', '2024-01-20 09:05:00'),
(2, 2, 'company_registration_pending.pdf', 'PENDING', NULL, NULL, '2024-01-25 10:05:00'),
(3, 3, 'financial_license_pending.pdf', 'PENDING', NULL, NULL, '2024-02-01 11:05:00');

-- ============================================
-- RESET SEQUENCES TO MATCH SEEDED IDS
-- ============================================
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('seeker_profiles', 'id'), (SELECT MAX(id) FROM seeker_profiles));
SELECT setval(pg_get_serial_sequence('organization_profiles', 'id'), (SELECT MAX(id) FROM organization_profiles));
SELECT setval(pg_get_serial_sequence('skills', 'id'), (SELECT MAX(id) FROM skills));
SELECT setval(pg_get_serial_sequence('education', 'id'), (SELECT MAX(id) FROM education));
SELECT setval(pg_get_serial_sequence('interests', 'id'), (SELECT MAX(id) FROM interests));
SELECT setval(pg_get_serial_sequence('opportunities', 'id'), (SELECT MAX(id) FROM opportunities));
SELECT setval(pg_get_serial_sequence('applications', 'id'), (SELECT MAX(id) FROM applications));
SELECT setval(pg_get_serial_sequence('saved_opportunities', 'id'), (SELECT MAX(id) FROM saved_opportunities));
SELECT setval(pg_get_serial_sequence('audit_logs', 'id'), (SELECT MAX(id) FROM audit_logs));
SELECT setval(pg_get_serial_sequence('notifications', 'id'), (SELECT MAX(id) FROM notifications));
SELECT setval(pg_get_serial_sequence('reports', 'id'), (SELECT MAX(id) FROM reports));
SELECT setval(pg_get_serial_sequence('verification_requests', 'id'), (SELECT MAX(id) FROM verification_requests));

-- ============================================
-- SEED DATA COMPLETE
-- ============================================