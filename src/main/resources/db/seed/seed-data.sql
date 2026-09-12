-- TDOP Backend - Seed Data (Backup)
-- Tanzania Opportunities Platform
-- This is a backup copy of V2__seed_data.sql

-- ============================================
-- ADMIN USERS (3)
-- BCrypt $2a$10$ hashes for: admin123, superadmin, admin2024
-- ============================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, verified, created_at, updated_at) VALUES
('10000000-0000-0000-0000-000000000001', 'admin@tdop.go.tz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User One', '+255710000001', 'ADMIN', TRUE, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
('10000000-0000-0000-0000-000000000002', 'superadmin@tdop.go.tz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Super Administrator', '+255710000002', 'ADMIN', TRUE, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
('10000000-0000-0000-0000-000000000003', 'admin2024@tdop.go.tz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User Two', '+255710000003', 'ADMIN', TRUE, TRUE, '2024-01-15 09:00:00', '2024-01-15 09:00:00');

-- ============================================
-- SEEKER USERS (5)
-- ============================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, verified, created_at, updated_at) VALUES
('20000000-0000-0000-0000-000000000011', 'john.mwangi@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John Mwangi', '+255754000011', 'SEEKER', TRUE, TRUE, '2024-02-01 10:00:00', '2024-02-01 10:00:00'),
('20000000-0000-0000-0000-000000000012', 'amina.hassan@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amina Hassan', '+255754000012', 'SEEKER', TRUE, TRUE, '2024-02-05 11:00:00', '2024-02-05 11:00:00'),
('20000000-0000-0000-0000-000000000013', 'peter.okech@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Peter Okech', '+255754000013', 'SEEKER', TRUE, FALSE, '2024-02-10 12:00:00', '2024-02-10 12:00:00'),
('20000000-0000-0000-0000-000000000014', 'fatima.bakari@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Fatima Bakari', '+255754000014', 'SEEKER', TRUE, TRUE, '2024-02-15 13:00:00', '2024-02-15 13:00:00'),
('20000000-0000-0000-0000-000000000015', 'david.mwinyi@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'David Mwinyi', '+255754000015', 'SEEKER', TRUE, TRUE, '2024-02-20 14:00:00', '2024-02-20 14:00:00');

-- ============================================
-- ORGANIZATION USERS (3 - 1 verified, 2 pending)
-- ============================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, verified, created_at, updated_at) VALUES
('30000000-0000-0000-0000-000000000021', 'info@tanzgold.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Tanzania Gold Mining Ltd', '+255760000021', 'ORGANIZATION', TRUE, TRUE, '2024-01-20 09:00:00', '2024-01-20 09:00:00'),
('30000000-0000-0000-0000-000000000022', 'contact@safaricomTZ.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Safaricom Tanzania PLC', '+255760000022', 'ORGANIZATION', TRUE, FALSE, '2024-01-25 10:00:00', '2024-01-25 10:00:00'),
('30000000-0000-0000-0000-000000000023', 'hr@crdbbank.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CRDB Bank PLC', '+255760000023', 'ORGANIZATION', TRUE, FALSE, '2024-02-01 11:00:00', '2024-02-01 11:00:00');

-- ============================================
-- SEEKER PROFILES
-- ============================================
INSERT INTO seeker_profiles (id, user_id, bio, location, profile_picture, created_at) VALUES
('40000000-0000-0000-0000-000000000031', '20000000-0000-0000-0000-000000000011', 'Experienced software developer with 5 years in web development. Passionate about building solutions for East Africa.', 'Dar es Salaam', 'https://cdn.tdop.go.tz/profiles/0011.jpg', '2024-02-01 10:05:00'),
('40000000-0000-0000-0000-000000000032', '20000000-0000-0000-0000-000000000012', 'Data analyst specializing in agricultural data. Interested in improving food security through technology.', 'Arusha', 'https://cdn.tdop.go.tz/profiles/0012.jpg', '2024-02-05 11:05:00'),
('40000000-0000-0000-0000-000000000033', '20000000-0000-0000-0000-000000000013', 'Recent graduate looking for entry-level opportunities. Eager to learn and contribute.', 'Mwanza', NULL, '2024-02-10 12:05:00'),
('40000000-0000-0000-0000-000000000034', '20000000-0000-0000-0000-000000000014', 'Marketing professional with experience in digital campaigns and brand management.', 'Dodoma', 'https://cdn.tdop.go.tz/profiles/0014.jpg', '2024-02-15 13:05:00'),
('40000000-0000-0000-0000-000000000035', '20000000-0000-0000-0000-000000000015', 'Financial analyst with CFA certification. Skilled in risk assessment and investment analysis.', 'Dar es Salaam', 'https://cdn.tdop.go.tz/profiles/0015.jpg', '2024-02-20 14:05:00');

-- ============================================
-- ORGANIZATION PROFILES
-- ============================================
INSERT INTO organization_profiles (id, user_id, org_name, description, website, industry, size, logo, verified, verified_at, verification_document, created_at) VALUES
('50000000-0000-0000-0000-000000000041', '30000000-0000-0000-0000-000000000021', 'Tanzania Gold Mining Ltd', 'Leading gold mining company in Tanzania with over 20 years of experience in mineral extraction and processing.', 'https://www.tanzgold.com', 'Mining', '500+', 'https://cdn.tdop.go.tz/logos/tanzgold.png', TRUE, '2024-01-22 10:00:00', 'https://cdn.tdop.go.tz/docs/0021_verified.pdf', '2024-01-20 09:00:00'),
('50000000-0000-0000-0000-000000000042', '30000000-0000-0000-0000-000000000022', 'Safaricom Tanzania PLC', 'Premier telecommunications provider in East Africa. Offering innovative digital solutions across Tanzania.', 'https://www.safaricom.co.tz', 'Telecommunications', '1000+', 'https://cdn.tdop.go.tz/logos/safaricom.png', FALSE, NULL, NULL, '2024-01-25 10:00:00'),
('50000000-0000-0000-0000-000000000043', '30000000-0000-0000-0000-000000000023', 'CRDB Bank PLC', 'One of Tanzania''s largest banks providing comprehensive financial services including banking, insurance, and investment.', 'https://www.crdb.co.tz', 'Finance & Banking', '1000+', 'https://cdn.tdop.go.tz/logos/crdb.png', FALSE, NULL, NULL, '2024-02-01 11:00:00');

-- ============================================
-- SKILLS (8 sample skills)
-- ============================================
INSERT INTO skills (id, user_id, name, category, level, created_at) VALUES
('60000000-0000-0000-0000-000000000051', '20000000-0000-0000-0000-000000000011', 'Software Development', 'Technology', 'Advanced', '2024-02-01 10:10:00'),
('60000000-0000-0000-0000-000000000052', '20000000-0000-0000-0000-000000000011', 'Data Analysis', 'Technology', 'Intermediate', '2024-02-01 10:10:00'),
('60000000-0000-0000-0000-000000000053', '20000000-0000-0000-0000-000000000012', 'Data Analysis', 'Technology', 'Advanced', '2024-02-05 11:10:00'),
('60000000-0000-0000-0000-000000000054', '20000000-0000-0000-0000-000000000012', 'Marketing', 'Business', 'Intermediate', '2024-02-05 11:10:00'),
('60000000-0000-0000-0000-000000000055', '20000000-0000-0000-0000-000000000013', 'Project Management', 'Business', 'Beginner', '2024-02-10 12:10:00'),
('60000000-0000-0000-0000-000000000056', '20000000-0000-0000-0000-000000000013', 'Communication', 'Soft Skills', 'Beginner', '2024-02-10 12:10:00'),
('60000000-0000-0000-0000-000000000057', '20000000-0000-0000-0000-000000000014', 'Marketing', 'Business', 'Advanced', '2024-02-15 13:10:00'),
('60000000-0000-0000-0000-000000000058', '20000000-0000-0000-0000-000000000015', 'Financial Analysis', 'Finance', 'Advanced', '2024-02-20 14:10:00'),
('60000000-0000-0000-0000-000000000059', '20000000-0000-0000-0000-000000000011', 'Leadership', 'Soft Skills', 'Intermediate', '2024-02-01 10:10:00'),
('60000000-0000-0000-0000-000000000060', '20000000-0000-0000-0000-000000000015', 'Design', 'Creative', 'Intermediate', '2024-02-20 14:10:00');

-- ============================================
-- EDUCATION RECORDS
-- ============================================
INSERT INTO education (id, user_id, institution, degree, field_of_study, start_date, end_date, created_at) VALUES
('70000000-0000-0000-0000-000000000061', '20000000-0000-0000-0000-000000000011', 'University of Dar es Salaam', 'Master''s', 'Computer Science', '2019-09-01', '2022-06-30', '2024-02-01 10:15:00'),
('70000000-0000-0000-0000-000000000062', '20000000-0000-0000-0000-000000000011', 'Dar es Salaam Institute of Technology', 'Bachelor''s', 'Software Engineering', '2015-09-01', '2019-06-30', '2024-02-01 10:15:00'),
('70000000-0000-0000-0000-000000000063', '20000000-0000-0000-0000-000000000012', 'Sokoine University of Agriculture', 'Master''s', 'Agricultural Data Science', '2020-09-01', '2022-06-30', '2024-02-05 11:15:00'),
('70000000-0000-0000-0000-000000000064', '20000000-0000-0000-0000-000000000013', 'Mzumbe University', 'Bachelor''s', 'Business Administration', '2020-09-01', '2024-06-30', '2024-02-10 12:15:00'),
('70000000-0000-0000-0000-000000000065', '20000000-0000-0000-0000-000000000014', 'University of Dar es Salaam', 'Bachelor''s', 'Marketing', '2018-09-01', '2022-06-30', '2024-02-15 13:15:00'),
('70000000-0000-0000-0000-000000000066', '20000000-0000-0000-0000-000000000015', 'University of Dar es Salaam', 'Master''s', 'Finance', '2019-09-01', '2021-06-30', '2024-02-20 14:15:00'),
('70000000-0000-0000-0000-000000000067', '20000000-0000-0000-0000-000000000015', 'Institute of Finance Management', 'Bachelor''s', 'Banking and Finance', '2015-09-01', '2019-06-30', '2024-02-20 14:15:00');

-- ============================================
-- INTERESTS
-- ============================================
INSERT INTO interests (id, user_id, category, description, created_at) VALUES
('80000000-0000-0000-0000-000000000071', '20000000-0000-0000-0000-000000000011', 'Technology', 'Interested in AI, machine learning, and cloud computing solutions', '2024-02-01 10:20:00'),
('80000000-0000-0000-0000-000000000072', '20000000-0000-0000-0000-000000000011', 'Entrepreneurship', 'Passionate about startups and tech innovation in Tanzania', '2024-02-01 10:20:00'),
('80000000-0000-0000-0000-000000000073', '20000000-0000-0000-0000-000000000012', 'Agriculture', 'Sustainable agriculture and agri-tech innovation', '2024-02-05 11:20:00'),
('80000000-0000-0000-0000-000000000074', '20000000-0000-0000-0000-000000000012', 'Data Science', 'Big data analytics and predictive modeling', '2024-02-05 11:20:00'),
('80000000-0000-0000-0000-000000000075', '20000000-0000-0000-0000-000000000014', 'Digital Marketing', 'SEO, SEM, social media marketing strategies', '2024-02-15 13:20:00'),
('80000000-0000-0000-0000-000000000076', '20000000-0000-0000-0000-000000000015', 'Investment', 'Stock market analysis and portfolio management', '2024-02-20 14:20:00');

-- ============================================
-- OPPORTUNITIES (10 sample opportunities)
-- ============================================
INSERT INTO opportunities (id, title, description, requirements, benefits, location, type, category, salary_range, deadline, status, created_by, tags, created_at, updated_at) VALUES
('90000000-0000-0000-0000-000000000081', 'Senior Software Engineer', 'We are seeking an experienced software engineer to join our development team in Dar es Salaam. You will be responsible for designing, implementing, and maintaining scalable web applications.', '5+ years of experience, Proficiency in Java/Python, Strong knowledge of databases, Leadership skills', 'Competitive salary, Health insurance, Professional development fund, Housing allowance', 'Dar es Salaam', 'FULL_TIME', 'Technology', 'TZS 3,000,000 - 5,000,000', '2024-04-30 23:59:59', 'PUBLISHED', '30000000-0000-0000-0000-000000000021', 'software, engineering, senior', '2024-03-01 09:00:00', '2024-03-01 09:00:00'),
('90000000-0000-0000-0000-000000000082', 'Data Analyst Intern', 'Join our data analytics team to help process and analyze agricultural data for sustainable farming initiatives across Tanzania.', 'Currently pursuing degree in Statistics/Data Science, Knowledge of Python/R, Strong analytical skills', 'Stipend, Practical experience, Certificate of completion, Mentorship', 'Arusha', 'INTERNSHIP', 'Data Science', 'TZS 500,000 - 800,000', '2024-04-15 23:59:59', 'PUBLISHED', '30000000-0000-0000-0000-000000000021', 'data, analytics, internship', '2024-03-05 10:00:00', '2024-03-05 10:00:00'),
('90000000-0000-0000-0000-000000000083', 'Marketing Manager', 'Lead our marketing team and develop strategic campaigns to expand our telecommunications services across East Africa.', '7+ years marketing experience, MBA preferred, Team leadership, Digital marketing expertise', 'Salary TZS 4,000,000-6,000,000, Company car, Medical aid, Performance bonuses', 'Dar es Salaam', 'FULL_TIME', 'Marketing', 'TZS 4,000,000 - 6,000,000', '2024-04-20 23:59:59', 'DRAFT', '30000000-0000-0000-0000-000000000022', 'marketing, management, telecom', '2024-03-10 11:00:00', '2024-03-10 11:00:00'),
('90000000-0000-0000-0000-000000000084', 'Financial Advisor', 'Provide financial advisory services to clients in our banking division. Help clients achieve their financial goals through expert guidance.', 'CFA or equivalent certification, 5+ years experience in banking, Strong knowledge of Tanzanian financial regulations', 'Commission-based, TZS 3,500,000+ base, Training budget, Loan facilities', 'Dar es Salaam', 'FULL_TIME', 'Finance', 'TZS 3,500,000 - 5,500,000', '2024-05-01 23:59:59', 'PUBLISHED', '30000000-0000-0000-0000-000000000023', 'finance, banking, advisory', '2024-03-12 12:00:00', '2024-03-12 12:00:00'),
('90000000-0000-0000-0000-000000000085', 'Civil Site Engineer', 'Oversee construction projects for mining infrastructure including roads, bridges, and facility construction in rural Tanzania.', 'Bachelor''s in Civil Engineering, 4+ years construction experience, Willing to work in remote areas', 'Field allowance, Accommodation provided, Safety equipment, Annual leave', 'Mining Sites', 'FULL_TIME', 'Engineering', 'TZS 2,500,000 - 4,000,000', '2024-04-25 23:59:59', 'PUBLISHED', '30000000-0000-0000-0000-000000000021', 'civil, construction, mining', '2024-03-15 14:00:00', '2024-03-15 14:00:00'),
('90000000-0000-0000-0000-000000000086', 'Content Creator', 'Create engaging digital content for our social media channels and website. Highlight stories of Tanzanian entrepreneurs and professionals.', '2+ years content creation experience, Proficiency in Adobe Creative Suite, Understanding of social media trends', 'TZS 1,500,000-2,500,000, Flexible working hours, Equipment provided, Creative environment', 'Dar es Salaam', 'PART_TIME', 'Media', 'TZS 1,500,000 - 2,500,000', '2024-04-10 23:59:59', 'PUBLISHED', '30000000-0000-0000-0000-000000000022', 'content, social media, creative', '2024-03-18 09:30:00', '2024-03-18 09:30:00'),
('90000000-0000-0000-0000-000000000087', 'HR Officer', 'Manage recruitment, employee relations, and HR operations for our growing organization in Tanzania.', 'Degree in HR Management, 3+ years HR experience, Knowledge of Tanzanian labor laws', 'TZS 2,500,000-3,500,000, Training opportunities, Pension scheme, Annual bonus', 'Dar es Salaam', 'FULL_TIME', 'Human Resources', 'TZS 2,500,000 - 3,500,000', '2024-05-10 23:59:59', 'DRAFT', '30000000-0000-0000-0000-000000000023', 'hr, recruitment, human resources', '2024-03-20 10:00:00', '2024-03-20 10:00:00'),
('90000000-0000-0000-0000-000000000088', 'Junior Data Scientist', 'Entry-level position for recent graduates passionate about data science and machine learning applied to agricultural development.', 'Degree in Computer Science/Statistics, Knowledge of Python, Machine learning fundamentals, Strong academic record', 'Training program, Mentorship, TZS 1,200,000-2,000,000, Conference attendance', 'Morogoro', 'FULL_TIME', 'Data Science', 'TZS 1,200,000 - 2,000,000', '2024-05-15 23:59:59', 'PUBLISHED', '30000000-0000-0000-0000-000000000021', 'data science, machine learning, junior', '2024-03-22 15:00:00', '2024-03-22 15:00:00'),
('90000000-0000-0000-0000-000000000089', 'Graphic Designer', 'Design marketing materials, website graphics, and branding elements for our banking division.', 'Portfolio of design work, Proficiency in Adobe Creative Suite, Understanding of brand identity', 'TZS 1,500,000-2,500,000, Creative workspace, Flexible hours, Design tools', 'Dar es Salaam', 'CONTRACT', 'Design', 'TZS 1,500,000 - 2,500,000', '2024-04-05 23:59:59', 'CLOSED', '30000000-0000-0000-0000-000000000023', 'design, graphic, creative', '2024-03-25 11:00:00', '2024-04-01 16:00:00'),
('90000000-0000-0000-0000-000000000090', 'Project Coordinator', 'Coordinate mining community development projects, liaise with local communities, and ensure project milestones are met.', 'Degree in Project Management or related field, 3+ years coordination experience, Excellent communication skills', 'TZS 2,800,000-4,000,000, Field allowance, Travel budget, Housing support', 'Mining Sites', 'FULL_TIME', 'Project Management', 'TZS 2,800,000 - 4,000,000', '2024-05-20 23:59:59', 'PUBLISHED', '30000000-0000-0000-0000-000000000021', 'project management, coordination, mining', '2024-03-28 09:00:00', '2024-03-28 09:00:00');

-- ============================================
-- APPLICATIONS
-- ============================================
INSERT INTO applications (id, opportunity_id, applicant_id, status, cover_letter, resume_url, applied_at, updated_at) VALUES
('a0000000-0000-0000-0000-000000000091', '90000000-0000-0000-0000-000000000081', '20000000-0000-0000-0000-000000000011', 'REVIEWED', 'I am excited to apply for the Senior Software Engineer position. With 5 years of experience in web development and a Master''s in Computer Science from UDSM, I am confident I can contribute effectively to your team.', 'https://cdn.tdop.go.tz/resumes/0011_resume.pdf', '2024-03-02 09:00:00', '2024-03-05 14:00:00'),
('a0000000-0000-0000-0000-000000000092', '90000000-0000-0000-0000-000000000081', '20000000-0000-0000-0000-000000000015', 'PENDING', 'As a financial analyst looking to transition into tech, I bring strong analytical and problem-solving skills that complement software engineering.', 'https://cdn.tdop.go.tz/resumes/0015_resume.pdf', '2024-03-03 10:00:00', '2024-03-03 10:00:00'),
('a0000000-0000-0000-0000-000000000093', '90000000-0000-0000-0000-000000000088', '20000000-0000-0000-0000-000000000012', 'ACCEPTED', 'I am a recent graduate from Sokoine University with a Master''s in Agricultural Data Science. I am passionate about applying data science to solve agricultural challenges in Tanzania.', 'https://cdn.tdop.go.tz/resumes/0012_resume.pdf', '2024-03-23 11:00:00', '2024-03-28 16:00:00'),
('a0000000-0000-0000-0000-000000000094', '90000000-0000-0000-0000-000000000085', '20000000-0000-0000-0000-000000000013', 'PENDING', 'I recently graduated with a Bachelor''s in Civil Engineering from Mzumbe University and am eager to gain field experience in mining infrastructure.', 'https://cdn.tdop.go.tz/resumes/0013_resume.pdf', '2024-03-16 09:00:00', '2024-03-16 09:00:00'),
('a0000000-0000-0000-0000-000000000095', '90000000-0000-0000-0000-000000000085', '20000000-0000-0000-0000-000000000011', 'REJECTED', 'I have the required qualifications but I am currently committed to another project and cannot relocate to mining sites.', 'https://cdn.tdop.go.tz/resumes/0011_resume_alt.pdf', '2024-03-17 10:00:00', '2024-03-20 11:00:00'),
('a0000000-0000-0000-0000-000000000096', '90000000-0000-0000-0000-000000000082', '20000000-0000-0000-0000-000000000012', 'REVIEWED', 'As a current Master''s student in Agricultural Data Science, I am a perfect fit for this internship to combine my academic knowledge with practical experience.', 'https://cdn.tdop.go.tz/resumes/0012_resume_intern.pdf', '2024-03-06 09:00:00', '2024-03-08 12:00:00');

-- ============================================
-- SAVED OPPORTUNITIES
-- ============================================
INSERT INTO saved_opportunities (id, user_id, opportunity_id, saved_at) VALUES
('b0000000-0000-0000-0000-000000000101', '20000000-0000-0000-0000-000000000011', '90000000-0000-0000-0000-000000000081', '2024-03-01 09:05:00'),
('b0000000-0000-0000-0000-000000000102', '20000000-0000-0000-0000-000000000011', '90000000-0000-0000-0000-000000000088', '2024-03-22 15:05:00'),
('b0000000-0000-0000-0000-000000000103', '20000000-0000-0000-0000-000000000012', '90000000-0000-0000-0000-000000000082', '2024-03-05 10:05:00'),
('b0000000-0000-0000-0000-000000000104', '20000000-0000-0000-0000-000000000012', '90000000-0000-0000-0000-000000000084', '2024-03-12 12:05:00'),
('b0000000-0000-0000-0000-000000000105', '20000000-0000-0000-0000-000000000014', '90000000-0000-0000-0000-000000000086', '2024-03-18 09:35:00'),
('b0000000-0000-0000-0000-000000000106', '20000000-0000-0000-0000-000000000015', '90000000-0000-0000-0000-000000000081', '2024-03-03 10:05:00'),
('b0000000-0000-0000-0000-000000000107', '20000000-0000-0000-0000-000000000015', '90000000-0000-0000-0000-000000000084', '2024-03-12 12:05:00'),
('b0000000-0000-0000-0000-000000000108', '20000000-0000-0000-0000-000000000013', '90000000-0000-0000-0000-000000000085', '2024-03-16 09:05:00');

-- ============================================
-- AUDIT LOGS
-- ============================================
INSERT INTO audit_logs (id, action, entity_type, entity_id, user_id, old_value, new_value, timestamp, ip_address) VALUES
('c0000000-0000-0000-0000-000000000111', 'CREATE', 'opportunity', '90000000-0000-0000-0000-000000000081', '30000000-0000-0000-0000-000000000021', NULL, '{"title": "Senior Software Engineer", "status": "PUBLISHED"}', '2024-03-01 09:00:00', '197.156.50.1'),
('c0000000-0000-0000-0000-000000000112', 'APPLICATION', 'application', 'a0000000-0000-0000-0000-000000000091', '20000000-0000-0000-0000-000000000011', NULL, '{"opportunity_id": "90000000-0000-0000-0000-000000000081", "status": "REVIEWED"}', '2024-03-02 09:00:00', '197.156.50.2'),
('c0000000-0000-0000-0000-000000000113', 'VERIFY', 'organization', '50000000-0000-0000-0000-000000000041', '10000000-0000-0000-0000-000000000001', NULL, '{"verified": true, "verified_at": "2024-01-22T10:00:00"}', '2024-01-22 10:00:00', '197.156.50.3'),
('c0000000-0000-0000-0000-000000000114', 'UPDATE', 'opportunity', '90000000-0000-0000-0000-000000000089', '30000000-0000-0000-0000-000000000023', '{"status": "PUBLISHED"}', '{"status": "CLOSED"}', '2024-04-01 16:00:00', '197.156.50.4'),
('c0000000-0000-0000-0000-000000000115', 'APPLICATION', 'application', 'a0000000-0000-0000-0000-000000000093', '20000000-0000-0000-0000-000000000012', '{"status": "PENDING"}', '{"status": "ACCEPTED"}', '2024-03-28 16:00:00', '197.156.50.5'),
('c0000000-0000-0000-0000-000000000116', 'CREATE', 'verification_request', 'd0000000-0000-0000-0000-000000000121', '30000000-0000-0000-0000-000000000022', NULL, '{"document": "company_registration.pdf", "status": "PENDING"}', '2024-01-25 10:05:00', '197.156.50.2'),
('c0000000-0000-0000-0000-000000000117', 'LOGIN', 'session', NULL, '20000000-0000-0000-0000-000000000011', NULL, '{"ip": "197.156.50.2", "device": "Chrome"}', '2024-03-01 08:55:00', '197.156.50.2'),
('c0000000-0000-0000-0000-000000000118', 'SAVE', 'saved_opportunity', 'b0000000-0000-0000-0000-000000000101', '20000000-0000-0000-0000-000000000011', NULL, '{"opportunity_id": "90000000-0000-0000-0000-000000000081"}', '2024-03-01 09:05:00', '197.156.50.2'),
('c0000000-0000-0000-0000-000000000119', 'CREATE', 'verification_request', 'd0000000-0000-0000-0000-000000000122', '30000000-0000-0000-0000-000000000023', NULL, '{"document": "financial_license.pdf", "status": "PENDING"}', '2024-02-01 11:05:00', '197.156.50.4'),
('c0000000-0000-0000-0000-000000000120', 'ROLE_CHANGE', 'user', '30000000-0000-0000-0000-000000000022', '10000000-0000-0000-0000-000000000001', '{"role": "ADMIN"}', '{"role": "ORGANIZATION"}', '2024-02-01 09:00:00', '197.156.50.3');

-- ============================================
-- NOTIFICATIONS
-- ============================================
INSERT INTO notifications (id, user_id, title, message, type, read, created_at) VALUES
('d0000000-0000-0000-0000-000000000131', '20000000-0000-0000-0000-000000000011', 'Application Reviewed', 'Your application for Senior Software Engineer has been reviewed. The employer would like to schedule an interview.', 'APPLICATION_UPDATE', FALSE, '2024-03-05 14:00:00'),
('d0000000-0000-0000-0000-0000-000000000132', '20000000-0000-0000-0000-000000000012', 'Application Accepted', 'Congratulations! Your application for Junior Data Scientist has been accepted. Please check your email for next steps.', 'APPLICATION_UPDATE', FALSE, '2024-03-28 16:00:00'),
('d0000000-0000-0000-0000-0000-000000000133', '20000000-0000-0000-0000-000000000013', 'Application Rejected', 'We regret to inform you that your application for Civil Site Engineer was not successful. We encourage you to apply for other opportunities.', 'APPLICATION_UPDATE', TRUE, '2024-03-20 11:00:00'),
('d0000000-0000-0000-0000-0000-000000000134', '30000000-0000-0000-0000-000000000021', 'New Application Received', 'You have received a new application from John Mwangi for the Senior Software Engineer position.', 'NEW_APPLICATION', FALSE, '2024-03-02 09:00:00'),
('d0000000-0000-0000-0000-0000-000000000135', '30000000-0000-0000-0000-000000000021', 'Organization Verified', 'Your organization Tanzania Gold Mining Ltd has been successfully verified. You can now post unlimited opportunities.', 'ORG_VERIFIED', TRUE, '2024-01-22 10:00:00'),
('d0000000-0000-0000-0000-0000-000000000136', '20000000-0000-0000-0000-000000000011', 'New Opportunity Available', 'A new Senior Software Engineer position has been posted by Tanzania Gold Mining Ltd.', 'NEW_OPPORTUNITY', TRUE, '2024-03-01 09:05:00'),
('d0000000-0000-0000-0000-0000-000000000137', '30000000-0000-0000-0000-000000000022', 'Verification Request Submitted', 'Your verification request for Safaricom Tanzania PLC is pending review. Please provide additional documentation if requested.', 'VERIFICATION_PENDING', FALSE, '2024-01-25 10:05:00'),
('d0000000-0000-0000-0000-0000-000000000138', '20000000-0000-0000-0000-000000000015', 'Application Status Update', 'Your application for Senior Software Engineer has been moved to REVIEWED status.', 'APPLICATION_UPDATE', TRUE, '2024-03-05 14:00:00');

-- ============================================
-- REPORTS
-- ============================================
INSERT INTO reports (id, reporter_id, target_type, target_id, reason, description, status, created_at) VALUES
('e0000000-0000-0000-0000-000000000141', '20000000-0000-0000-0000-000000000013', 'opportunity', '90000000-0000-0000-0000-000000000089', 'SPAM', 'This opportunity appears to be a duplicate listing. It was already posted and closed.', 'PENDING', '2024-03-29 14:00:00'),
('e0000000-0000-0000-0000-000000000142', '20000000-0000-0000-0000-000000000011', 'user', '20000000-0000-0000-0000-000000000013', 'INAPPROPRIATE_CONTENT', 'User profile contains misleading information about qualifications.', 'REVIEWED', '2024-03-25 10:00:00'),
('e0000000-0000-0000-0000-000000000143', '20000000-0000-0000-0000-000000000014', 'opportunity', '90000000-0000-0000-0000-000000000084', 'MISLEADING_SALARY', 'The salary range mentioned does not match what was discussed during the interview process.', 'PENDING', '2024-04-01 09:00:00'),
('e0000000-0000-0000-0000-000000000144', '20000000-0000-0000-0000-000000000012', 'user', '30000000-0000-0000-0000-000000000022', 'UNAUTHORIZED_POSTING', 'Organization account is posting opportunities without proper verification.', 'RESOLVED', '2024-03-20 11:00:00');

-- ============================================
-- VERIFICATION REQUESTS
-- ============================================
INSERT INTO verification_requests (id, organization_id, document, status, reviewed_by, reviewed_at, created_at) VALUES
('f0000000-0000-0000-0000-000000000151', '50000000-0000-0000-0000-000000000041', 'company_registration_verified.pdf', 'APPROVED', '10000000-0000-0000-0000-000000000001', '2024-01-22 09:00:00', '2024-01-20 09:05:00'),
('f0000000-0000-0000-0000-000000000152', '50000000-0000-0000-0000-000000000042', 'company_registration_pending.pdf', 'PENDING', NULL, NULL, '2024-01-25 10:05:00'),
('f0000000-0000-0000-0000-0000-000000000153', '50000000-0000-0000-0000-000000000043', 'financial_license_pending.pdf', 'PENDING', NULL, NULL, '2024-02-01 11:05:00');

-- ============================================
-- SEED DATA COMPLETE
-- ============================================
