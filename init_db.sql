-- Create User table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    CONSTRAINT pk_user PRIMARY KEY (id)
);

-- Create Category table
CREATE TABLE IF NOT EXISTS category (
    code VARCHAR(10),
    description VARCHAR(255),
    min_age INTEGER,
    CONSTRAINT pk_category PRIMARY KEY (code)
);

-- Create Instructor table
CREATE TABLE IF NOT EXISTS instructor (
    cin VARCHAR(20),
    address VARCHAR(255),
    birthday DATE,
    city VARCHAR(100),
    email VARCHAR(100),
    first_name VARCHAR(100),
    gsm VARCHAR(20),
    last_name VARCHAR(100),
    starting_date DATE,
    CONSTRAINT pk_instructor PRIMARY KEY (cin)
);

-- Create Vehicle table
CREATE TABLE IF NOT EXISTS vehicle (
    immatriculation VARCHAR(20),
    amount_vignette DOUBLE,
    category VARCHAR(10),
    date_last_vignette DATE,
    km_initial INTEGER,
    vehicle_brand VARCHAR(100),
    fuel_type VARCHAR(50),
    vehicle_type VARCHAR(50),
    CONSTRAINT pk_vehicle PRIMARY KEY (immatriculation)
);

-- Create ApplicationFile table
CREATE TABLE IF NOT EXISTS application_file (
    id BIGINT AUTO_INCREMENT,
    practical_hours_completed DOUBLE,
    theoretical_hours_completed DOUBLE,
    is_active BOOLEAN DEFAULT TRUE,
    starting_date DATE,
    status VARCHAR(50),
    file_number VARCHAR(255),
    tax_stamp BOOLEAN,
    medical_visit VARCHAR(255),
    candidate_cin VARCHAR(20),
    category_code VARCHAR(10),
    CONSTRAINT pk_application_file PRIMARY KEY (id)
);

-- Create Candidate table
CREATE TABLE IF NOT EXISTS candidate (
    cin VARCHAR(20),
    address VARCHAR(255),
    birth_day DATE,
    birth_place VARCHAR(100),
    city VARCHAR(100),
    email VARCHAR(100),
    first_name VARCHAR(100),
    gender VARCHAR(10),
    gsm VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    last_name VARCHAR(100),
    starting_date DATE,
    CONSTRAINT pk_candidate PRIMARY KEY (cin)
);

-- Create Payment table
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT AUTO_INCREMENT,
    paid_amount INTEGER,
    status VARCHAR(50),
    total_amount INTEGER,
    candidate_cin VARCHAR(20),
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

-- Create PaymentInstallment table
CREATE TABLE IF NOT EXISTS payment_installment (
    id BIGINT AUTO_INCREMENT,
    amount INTEGER,
    date DATE,
    installment_number INTEGER,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_id BIGINT,
    CONSTRAINT pk_payment_installment PRIMARY KEY (id)
);

-- Create Exam table
CREATE TABLE IF NOT EXISTS exam (
    id BIGINT AUTO_INCREMENT,
    attempt_number INTEGER,
    date DATE,
    exam_type VARCHAR(50),  -- 'Code' or 'Driving'
    status VARCHAR(50),     -- 'PASSED', 'FAILED', 'SCHEDULED'
    application_file_id BIGINT,
    CONSTRAINT pk_exam PRIMARY KEY (id)
);

-- Create Session table
CREATE TABLE IF NOT EXISTS session (
    id BIGINT AUTO_INCREMENT,
    date_session DATE,
    duration DOUBLE,
    status VARCHAR(50),
    session_type VARCHAR(50),
    candidate_cin VARCHAR(20),
    instructor_cin VARCHAR(20),
    vehicle_immat VARCHAR(20),
    CONSTRAINT pk_session PRIMARY KEY (id)
);

-- Create Insurance table
CREATE TABLE IF NOT EXISTS insurance (
    id BIGINT AUTO_INCREMENT,
    amount INTEGER,
    company VARCHAR(255),
    next_operation_date DATE,
    operation_date DATE,
    vehicle_immat VARCHAR(20),
    CONSTRAINT pk_insurance PRIMARY KEY (id)
);

-- Create OilChange table
CREATE TABLE IF NOT EXISTS oil_change (
    id BIGINT AUTO_INCREMENT,
    amount INTEGER,
    company VARCHAR(255),
    next_operation_date DATE,
    operation_date DATE,
    vehicle_immat VARCHAR(20),
    CONSTRAINT pk_oil_change PRIMARY KEY (id)
);

-- Create TechnicalVisit table
CREATE TABLE IF NOT EXISTS technical_visit (
    id BIGINT AUTO_INCREMENT,
    amount INTEGER,
    company VARCHAR(255),
    next_operation_date DATE,
    operation_date DATE,
    vehicle_immat VARCHAR(20),
    CONSTRAINT pk_technical_visit PRIMARY KEY (id)
);

-- Insert sample categories
INSERT INTO category (code, description, min_age) VALUES
('AM', 'Cyclomoteur ≤4kw/50cm³/50km/h et quadricycle léger ≤4kw/350kg', 16),
('A1', 'Motocycle ≤125cm³ et/ou ≤15kw', 18),
('A', 'Motocycle >125cm³ avec puissance ≤73.6kw avec/sans side-car', 20),
('B', 'Véhicules transport personnes ≤9 places et marchandises PTAC ≤3.5t', 18),
('C', 'Véhicules transport marchandises PTAC >3500kg', 21),
('D', 'Véhicules transport personnes >8 places assises', 24),
('EB', 'Catégorie B + remorque PTAC >750kg (conditions spéciales)', 18),
('EC', 'Catégorie C + remorque PTAC >750kg', 21),
('ED', 'Catégorie D + remorque PTAC >750kg', 24);

-- Password: hello
INSERT INTO user (email, first_name, last_name, password, role) VALUES
('admin@autoecole.ma', 'Admin', 'User', '$2a$12$TxYvH66NrxMhp.fAOX/HsOZH4Ulws.OX1pQbaFFPFGG5SK4xcfbJa', 'ADMIN');

-- Insert sample instructors
INSERT INTO instructor (cin, first_name, last_name, email, address, city, gsm, starting_date) VALUES
    ('I123456789', 'Ahmed', 'Bennani', 'ahmed.bennani@autoecole.ma', '123 Rue Mohammed V', 'Casablanca', '0612345678', '2023-01-15'),
    ('I987654321', 'Fatima', 'Alaoui', 'fatima.alaoui@autoecole.ma', '456 Avenue Hassan II', 'Rabat', '0687654321', '2023-02-01');

-- Insert sample vehicles
INSERT INTO vehicle (immatriculation, vehicle_brand, vehicle_type, fuel_type, category, km_initial, amount_vignette) VALUES
    ('123-A-45', 'Toyota', 'Sedan', 'Gasoline', 'B', 50000, 1500.00),
    ('456-B-78', 'Honda', 'Motorcycle', 'Gasoline', 'A', 25000, 800.00),
    ('789-C-12', 'Mercedes', 'Truck', 'Diesel', 'C', 80000, 3000.00);

-- Add Foreign Key Constraints
ALTER TABLE vehicle ADD CONSTRAINT fk_vehicle_category
    FOREIGN KEY (category) REFERENCES category(code);

ALTER TABLE payment ADD CONSTRAINT fk_payment_candidate
    FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE;

ALTER TABLE payment_installment ADD CONSTRAINT fk_payment_installment_payment
    FOREIGN KEY (payment_id) REFERENCES payment(id) ON DELETE CASCADE;

ALTER TABLE exam ADD CONSTRAINT fk_exam_candidate
    FOREIGN KEY (application_file_id) REFERENCES application_file(id) ON DELETE CASCADE;

ALTER TABLE session ADD CONSTRAINT fk_session_candidate
    FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE;

ALTER TABLE session ADD CONSTRAINT fk_session_instructor
    FOREIGN KEY (instructor_cin) REFERENCES instructor(cin);

ALTER TABLE session ADD CONSTRAINT fk_session_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation);

ALTER TABLE insurance ADD CONSTRAINT fk_insurance_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE;

ALTER TABLE oil_change ADD CONSTRAINT fk_oil_change_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE;

ALTER TABLE technical_visit ADD CONSTRAINT fk_technical_visit_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE;

ALTER TABLE application_file ADD CONSTRAINT fk_application_file_candidate
    FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE;

ALTER TABLE application_file ADD CONSTRAINT fk_application_file_category
    FOREIGN KEY (category_code) REFERENCES category(code) ON DELETE CASCADE;

-- Add unique constraint to prevent duplicate active application files for same category
ALTER TABLE application_file ADD CONSTRAINT uk_candidate_category_active
    UNIQUE (candidate_cin, category_code, is_active);

-- Insertions de 15 candidats pour tester la pagination
INSERT INTO candidate (cin, address, birth_day, birth_place, city, email, first_name, gender, gsm, is_active, last_name, starting_date) VALUES
('AB123456', '123 Rue Mohammed V', '1995-03-15', 'Casablanca', 'Casablanca', 'ahmed.alami@email.com', 'Ahmed', 'M', '+212601234567', true, 'Alami', '2024-01-15'),
('CD789012', '456 Avenue Hassan II', '1998-07-22', 'Rabat', 'Rabat', 'fatima.benali@email.com', 'Fatima', 'F', '+212602345678', true, 'Benali', '2024-02-10'),
('EF345678', '789 Boulevard Zerktouni', '1996-11-08', 'Casablanca', 'Casablanca', 'youssef.chakir@email.com', 'Youssef', 'M', '+212603456789', false, 'Chakir', '2023-12-05'),
('GH901234', '321 Rue Allal Ben Abdellah', '1999-05-14', 'Fès', 'Fès', 'khadija.derouich@email.com', 'Khadija', 'F', '+212604567890', true, 'Derouich', '2024-03-20'),
('IJ567890', '654 Avenue Moulay Ismail', '1997-09-30', 'Meknès', 'Meknès', 'omar.elalami@email.com', 'Omar', 'M', '+212605678901', true, 'El Alami', '2024-01-08'),
('KL123890', '987 Rue Ibn Battuta', '1994-12-03', 'Tanger', 'Tanger', 'aicha.fassi@email.com', 'Aicha', 'F', '+212606789012', false, 'Fassi', '2023-11-12'),
('MN456123', '147 Boulevard Abdelmoumen', '2000-04-18', 'Casablanca', 'Casablanca', 'hamid.ghali@email.com', 'Hamid', 'M', '+212607890123', true, 'Ghali', '2024-02-25'),
('OP789456', '258 Avenue Agdal', '1996-08-25', 'Rabat', 'Rabat', 'nadia.hassani@email.com', 'Nadia', 'F', '+212608901234', true, 'Hassani', '2024-01-30'),
('QR012789', '369 Rue Gueliz', '1998-01-12', 'Marrakech', 'Marrakech', 'Said.idrissi@email.com', 'Saïd', 'M', '+212609012345', false, 'Idrissi', '2023-10-15'),
('ST345012', '741 Boulevard Anfa', '1995-06-07', 'Casablanca', 'Casablanca', 'laila.jabbari@email.com', 'Laila', 'F', '+212610123456', true, 'Jabbari', '2024-03-05'),
('UV678345', '852 Avenue Atlas', '1999-10-20', 'Agadir', 'Agadir', 'mohamed.kabbaj@email.com', 'Mohamed', 'M', '+212611234567', true, 'Kabbaj', '2024-02-18'),
('WX901678', '963 Rue Liberté', '1997-02-28', 'Salé', 'Salé', 'zineb.lahlou@email.com', 'Zineb', 'F', '+212612345678', false, 'Lahlou', '2023-09-22'),
('YZ234901', '159 Boulevard Massira', '1996-12-16', 'Témara', 'Témara', 'rachid.mounir@email.com', 'Rachid', 'M', '+212613456789', true, 'Mounir', '2024-01-12'),
('AA567234', '357 Avenue Royale', '1998-03-09', 'Oujda', 'Oujda', 'samira.naciri@email.com', 'Samira', 'F', '+212614567890', true, 'Naciri', '2024-03-15'),
('BB890567', '486 Rue Palmier', '1995-09-05', 'Kenitra', 'Kenitra', 'tarik.ouali@email.com', 'Tarik', 'M', '+212615678901', true, 'Ouali', '2024-02-08');

-- Insert Payments for candidates
INSERT INTO payment (paid_amount, status, total_amount, candidate_cin) VALUES
    (3500, 'PARTIAL', 5000, 'AB123456'),
    (5000, 'PAID', 5000, 'CD789012'),
    (2000, 'PARTIAL', 4800, 'EF345678'),
    (4500, 'PARTIAL', 5200, 'GH901234'),
    (1500, 'PARTIAL', 5000, 'IJ567890'),
    (0, 'UNPAID', 4800, 'KL123890'),
    (5200, 'PAID', 5200, 'MN456123'),
    (3000, 'PARTIAL', 5000, 'OP789456'),
    (800, 'PARTIAL', 4800, 'QR012789'),
    (5200, 'PAID', 5200, 'ST345012'),
    (2500, 'PARTIAL', 5000, 'UV678345'),
    (1200, 'PARTIAL', 4800, 'WX901678'),
    (4000, 'PARTIAL', 5200, 'YZ234901'),
    (3200, 'PARTIAL', 5000, 'AA567234'),
    (4800, 'PARTIAL', 5000, 'BB890567');

-- Insert Payment Installments
-- For candidate AB123456 (Payment ID 1)
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (2000, '2024-01-15', 1, 'PAID', 1),
    (1500, '2024-02-15', 2, 'PAID', 1),
    (1500, '2024-03-15', 3, 'PENDING', 1);

-- For candidate CD789012 (Payment ID 2) - Fully paid
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (2500, '2024-02-10', 1, 'PAID', 2),
    (2500, '2024-03-10', 2, 'PAID', 2);

-- For candidate EF345678 (Payment ID 3)
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (2000, '2023-12-05', 1, 'PAID', 3),
    (1400, '2024-01-05', 2, 'OVERDUE', 3),
    (1400, '2024-02-05', 3, 'PENDING', 3);

-- For candidate GH901234 (Payment ID 4)
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (2600, '2024-03-20', 1, 'PAID', 4),
    (1900, '2024-04-20', 2, 'PAID', 4),
    (700, '2024-05-20', 3, 'PENDING', 4);

-- For candidate IJ567890 (Payment ID 5)
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (1500, '2024-01-08', 1, 'PAID', 5),
    (1750, '2024-02-08', 2, 'PENDING', 5),
    (1750, '2024-03-08', 3, 'PENDING', 5);

-- For candidate KL123890 (Payment ID 6) - Unpaid
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (1600, '2023-11-12', 1, 'OVERDUE', 6),
    (1600, '2023-12-12', 2, 'OVERDUE', 6),
    (1600, '2024-01-12', 3, 'OVERDUE', 6);

-- For candidate MN456123 (Payment ID 7) - Fully paid
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (2600, '2024-02-25', 1, 'PAID', 7),
    (2600, '2024-03-25', 2, 'PAID', 7);

-- For candidate OP789456 (Payment ID 8)
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id) VALUES
    (2000, '2024-01-30', 1, 'PAID', 8),
    (1000, '2024-02-28', 2, 'PAID', 8),
    (2000, '2024-03-30', 3, 'PENDING', 8);

-- Insert multiple application files for some candidates
INSERT INTO application_file (practical_hours_completed, theoretical_hours_completed, is_active, starting_date, status, file_number, tax_stamp, medical_visit, candidate_cin, category_code) VALUES
-- Candidate AB123456 - Multiple categories
(15.5, 28.0, true, '2024-01-15', 'IN_PROGRESS', 'DOS-2024-001-B', true, 'COMPLETED', 'AB123456', 'B'),
(5.0, 10.0, true, '2024-03-01', 'IN_PROGRESS', 'DOS-2024-001-A', true, 'COMPLETED', 'AB123456', 'A'),

-- Candidate CD789012 - Category B only
(22.0, 30.0, true, '2024-02-10', 'READY_FOR_EXAM', 'DOS-2024-002-B', true, 'COMPLETED', 'CD789012', 'B'),

-- Candidate EF345678 - Multiple categories
(18.5, 25.0, false, '2023-12-05', 'SUSPENDED', 'DOS-2023-045-B', false, 'PENDING', 'EF345678', 'B'),
(0.0, 5.0, true, '2024-02-01', 'IN_PROGRESS', 'DOS-2024-045-C', true, 'COMPLETED', 'EF345678', 'C'),

-- Candidate GH901234 - Category B only
(30.0, 30.0, true, '2024-03-20', 'READY_FOR_EXAM', 'DOS-2024-003-B', true, 'COMPLETED', 'GH901234', 'B'),

-- Candidate IJ567890 - Multiple categories
(12.0, 20.0, true, '2024-01-08', 'IN_PROGRESS', 'DOS-2024-004-B', true, 'COMPLETED', 'IJ567890', 'B'),
(8.0, 15.0, true, '2024-02-15', 'IN_PROGRESS', 'DOS-2024-004-A', true, 'COMPLETED', 'IJ567890', 'A'),

-- Candidate KL123890 - Category B only
(8.5, 15.0, false, '2023-11-12', 'SUSPENDED', 'DOS-2023-046-B', true, 'COMPLETED', 'KL123890', 'B'),

-- Candidate MN456123 - Category B only
(25.0, 30.0, true, '2024-02-25', 'READY_FOR_EXAM', 'DOS-2024-005-B', true, 'COMPLETED', 'MN456123', 'B'),

-- Candidate OP789456 - Multiple categories
(20.0, 28.0, true, '2024-01-30', 'IN_PROGRESS', 'DOS-2024-006-B', true, 'COMPLETED', 'OP789456', 'B'),
(3.0, 8.0, true, '2024-03-15', 'IN_PROGRESS', 'DOS-2024-006-A1', true, 'COMPLETED', 'OP789456', 'A1'),

-- Candidate QR012789 - Category B only
(5.0, 10.0, false, '2023-10-15', 'SUSPENDED', 'DOS-2023-047-B', false, 'PENDING', 'QR012789', 'B'),

-- Candidate ST345012 - Multiple categories
(28.0, 30.0, true, '2024-03-05', 'READY_FOR_EXAM', 'DOS-2024-007-B', true, 'COMPLETED', 'ST345012', 'B'),
(12.0, 20.0, true, '2024-03-20', 'IN_PROGRESS', 'DOS-2024-007-C', true, 'COMPLETED', 'ST345012', 'C'),

-- Candidate UV678345 - Category B only
(16.5, 25.0, true, '2024-02-18', 'IN_PROGRESS', 'DOS-2024-008-B', true, 'COMPLETED', 'UV678345', 'B'),

-- Candidate WX901678 - Category B only
(10.0, 18.0, false, '2023-09-22', 'SUSPENDED', 'DOS-2023-048-B', true, 'COMPLETED', 'WX901678', 'B'),

-- Candidate YZ234901 - Category B only
(24.0, 30.0, true, '2024-01-12', 'READY_FOR_EXAM', 'DOS-2024-009-B', true, 'COMPLETED', 'YZ234901', 'B'),

-- Candidate AA567234 - Multiple categories
(19.0, 26.0, true, '2024-03-15', 'IN_PROGRESS', 'DOS-2024-010-B', true, 'COMPLETED', 'AA567234', 'B'),
(2.0, 5.0, true, '2024-04-01', 'IN_PROGRESS', 'DOS-2024-010-A', true, 'COMPLETED', 'AA567234', 'A'),

-- Candidate BB890567 - Category B only
(21.5, 29.0, true, '2024-02-08', 'IN_PROGRESS', 'DOS-2024-011-B', true, 'COMPLETED', 'BB890567', 'B');

-- Insert exams linked to application files
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id) VALUES
-- Category B exams
(1, '2024-03-01', 'Code', 'PASSED', 1),      -- AB123456 Category B
(1, '2024-04-15', 'Conduite', 'SCHEDULED', 1),
(1, '2024-03-15', 'Code', 'PASSED', 3),      -- CD789012 Category B
(1, '2024-04-20', 'Conduite', 'SCHEDULED', 3),
(1, '2024-01-20', 'Code', 'FAILED', 4),      -- EF345678 Category B
(2, '2024-02-15', 'Code', 'PASSED', 4),
(1, '2024-04-10', 'Code', 'PASSED', 6),      -- GH901234 Category B
(1, '2024-05-01', 'Conduite', 'SCHEDULED', 6),
(1, '2024-02-20', 'Code', 'FAILED', 7),      -- IJ567890 Category B
(2, '2024-03-20', 'Code', 'PASSED', 7),
(1, '2024-04-15', 'Code', 'SCHEDULED', 10),  -- MN456123 Category B
(1, '2024-03-25', 'Code', 'PASSED', 11),     -- OP789456 Category B
(1, '2024-04-10', 'Conduite', 'PASSED', 11),
(1, '2024-04-20', 'Code', 'SCHEDULED', 15),  -- ST345012 Category B
(1, '2024-03-10', 'Code', 'PASSED', 17),     -- UV678345 Category B
(1, '2024-04-25', 'Code', 'SCHEDULED', 19),  -- YZ234901 Category B
(1, '2024-04-05', 'Code', 'PASSED', 20),     -- AA567234 Category B
(1, '2024-03-30', 'Code', 'PASSED', 22),     -- BB890567 Category B

-- Category A exams
(1, '2024-03-20', 'Code', 'PASSED', 2),      -- AB123456 Category A
(1, '2024-03-05', 'Code', 'PASSED', 8),      -- IJ567890 Category A
(1, '2024-04-01', 'Code', 'PASSED', 12),     -- OP789456 Category A1
(1, '2024-04-10', 'Code', 'SCHEDULED', 21),  -- AA567234 Category A

-- Category C exams
(1, '2024-02-20', 'Code', 'PASSED', 5),      -- EF345678 Category C
(1, '2024-04-05', 'Code', 'PASSED', 16);     -- ST345012 Category C