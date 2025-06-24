-- Drop existing tables if they exist (in correct order to avoid FK constraint issues)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS exam;
DROP TABLE IF EXISTS session;
DROP TABLE IF EXISTS payment_installment;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS application_file;
DROP TABLE IF EXISTS insurance;
DROP TABLE IF EXISTS oil_change;
DROP TABLE IF EXISTS technical_visit;
DROP TABLE IF EXISTS candidate;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS instructor;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;
SET FOREIGN_KEY_CHECKS = 1;

-- Create User table
CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT,
                      email VARCHAR(100) UNIQUE NOT NULL,
                      first_name VARCHAR(100),
                      last_name VARCHAR(100),
                      password VARCHAR(255) NOT NULL,
                      role VARCHAR(50),
                      CONSTRAINT pk_user PRIMARY KEY (id)
);

-- Create Category table
CREATE TABLE category (
                          code VARCHAR(10),
                          description VARCHAR(255),
                          min_age INTEGER,
                          CONSTRAINT pk_category PRIMARY KEY (code)
);

-- Create Instructor table
CREATE TABLE instructor (
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
CREATE TABLE vehicle (
                         immatriculation VARCHAR(20),
                         amount_vignette DOUBLE,
                         category VARCHAR(10),
                         date_last_vignette DATE,
                         km_initial INTEGER,
                         vehicle_brand VARCHAR(100),
                         fuel_type VARCHAR(50),
                         vehicle_type VARCHAR(50),
                         CONSTRAINT pk_vehicle PRIMARY KEY (immatriculation),
                         CONSTRAINT fk_vehicle_category FOREIGN KEY (category) REFERENCES category(code)
);

-- Create Candidate table
CREATE TABLE candidate (
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

-- Create ApplicationFile table
CREATE TABLE application_file (
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
                                  CONSTRAINT pk_application_file PRIMARY KEY (id),
                                  CONSTRAINT fk_application_file_candidate FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE,
                                  CONSTRAINT fk_application_file_category FOREIGN KEY (category_code) REFERENCES category(code)
);

-- Create Payment table (One-to-One with ApplicationFile)
CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT,
                         paid_amount INTEGER,
                         status VARCHAR(50),
                         total_amount INTEGER,
                         application_file_id BIGINT UNIQUE, -- Made UNIQUE for One-to-One relationship
                         CONSTRAINT pk_payment PRIMARY KEY (id),
                         CONSTRAINT fk_payment_application_file FOREIGN KEY (application_file_id) REFERENCES application_file(id) ON DELETE CASCADE
);

-- Create PaymentInstallment table
CREATE TABLE payment_installment (
                                     id BIGINT AUTO_INCREMENT,
                                     amount INTEGER,
                                     date DATE,
                                     installment_number INTEGER,
                                     status VARCHAR(20) DEFAULT 'PENDING',
                                     payment_id BIGINT,
                                     CONSTRAINT pk_payment_installment PRIMARY KEY (id),
                                     CONSTRAINT fk_payment_installment_payment FOREIGN KEY (payment_id) REFERENCES payment(id) ON DELETE CASCADE
);

-- Create Exam table (linked to ApplicationFile, not directly to Candidate)
CREATE TABLE exam (
                      id BIGINT AUTO_INCREMENT,
                      attempt_number INTEGER,
                      date DATE,
                      exam_type VARCHAR(50),  -- 'Code' or 'Driving'
                      status VARCHAR(50),     -- 'PASSED', 'FAILED', 'SCHEDULED'
                      application_file_id BIGINT,
                      CONSTRAINT pk_exam PRIMARY KEY (id),
                      CONSTRAINT fk_exam_application_file FOREIGN KEY (application_file_id) REFERENCES application_file(id) ON DELETE CASCADE
);

-- Create Session table
CREATE TABLE session (
                         id BIGINT AUTO_INCREMENT,
                         date_session DATE,
                         duration DOUBLE,
                         status VARCHAR(50),
                         session_type VARCHAR(50),
                         candidate_cin VARCHAR(20),
                         instructor_cin VARCHAR(20),
                         vehicle_immat VARCHAR(20),
                         CONSTRAINT pk_session PRIMARY KEY (id),
                         CONSTRAINT fk_session_candidate FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE,
                         CONSTRAINT fk_session_instructor FOREIGN KEY (instructor_cin) REFERENCES instructor(cin),
                         CONSTRAINT fk_session_vehicle FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation)
);

-- Create Insurance table
CREATE TABLE insurance (
                           id BIGINT AUTO_INCREMENT,
                           amount INTEGER,
                           company VARCHAR(255),
                           next_operation_date DATE,
                           operation_date DATE,
                           vehicle_immat VARCHAR(20),
                           CONSTRAINT pk_insurance PRIMARY KEY (id),
                           CONSTRAINT fk_insurance_vehicle FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE
);

-- Create OilChange table
CREATE TABLE oil_change (
                            id BIGINT AUTO_INCREMENT,
                            amount INTEGER,
                            company VARCHAR(255),
                            next_operation_date DATE,
                            operation_date DATE,
                            vehicle_immat VARCHAR(20),
                            CONSTRAINT pk_oil_change PRIMARY KEY (id),
                            CONSTRAINT fk_oil_change_vehicle FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE
);

-- Create TechnicalVisit table
CREATE TABLE technical_visit (
                                 id BIGINT AUTO_INCREMENT,
                                 amount INTEGER,
                                 company VARCHAR(255),
                                 next_operation_date DATE,
                                 operation_date DATE,
                                 vehicle_immat VARCHAR(20),
                                 CONSTRAINT pk_technical_visit PRIMARY KEY (id),
                                 CONSTRAINT fk_technical_visit_vehicle FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE
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

-- Insert 15 candidates for testing pagination
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

-- Insert Payments for application files (using dynamic references)
INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 3500, 'PARTIAL', 5000, af.id FROM application_file af WHERE af.candidate_cin = 'AB123456' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 5000, 'PAID', 5000, af.id FROM application_file af WHERE af.candidate_cin = 'CD789012' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 2000, 'PARTIAL', 4800, af.id FROM application_file af WHERE af.candidate_cin = 'EF345678' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 4500, 'PARTIAL', 5200, af.id FROM application_file af WHERE af.candidate_cin = 'GH901234' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 1500, 'PARTIAL', 5000, af.id FROM application_file af WHERE af.candidate_cin = 'IJ567890' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 0, 'UNPAID', 4800, af.id FROM application_file af WHERE af.candidate_cin = 'KL123890' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 5200, 'PAID', 5200, af.id FROM application_file af WHERE af.candidate_cin = 'MN456123' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 3000, 'PARTIAL', 5000, af.id FROM application_file af WHERE af.candidate_cin = 'OP789456' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 800, 'PARTIAL', 4800, af.id FROM application_file af WHERE af.candidate_cin = 'QR012789' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 5200, 'PAID', 5200, af.id FROM application_file af WHERE af.candidate_cin = 'ST345012' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 2500, 'PARTIAL', 5000, af.id FROM application_file af WHERE af.candidate_cin = 'UV678345' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 1200, 'PARTIAL', 4800, af.id FROM application_file af WHERE af.candidate_cin = 'WX901678' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 4000, 'PARTIAL', 5200, af.id FROM application_file af WHERE af.candidate_cin = 'YZ234901' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 3200, 'PARTIAL', 5000, af.id FROM application_file af WHERE af.candidate_cin = 'AA567234' AND af.category_code = 'B';

INSERT INTO payment (paid_amount, status, total_amount, application_file_id)
SELECT 4800, 'PARTIAL', 5000, af.id FROM application_file af WHERE af.candidate_cin = 'BB890567' AND af.category_code = 'B';

-- Insert Payment Installments (using dynamic payment ID references)
-- For AB123456 Category B payment
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 2000, '2024-01-15', 1, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'AB123456' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1500, '2024-02-15', 2, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'AB123456' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1500, '2024-03-15', 3, 'PENDING', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'AB123456' AND af.category_code = 'B';

-- For CD789012 Category B payment - Fully paid
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 2500, '2024-02-10', 1, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'CD789012' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 2500, '2024-03-10', 2, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'CD789012' AND af.category_code = 'B';

-- For EF345678 Category B payment
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 2000, '2023-12-05', 1, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'EF345678' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1400, '2024-01-05', 2, 'OVERDUE', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'EF345678' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1400, '2024-02-05', 3, 'PENDING', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'EF345678' AND af.category_code = 'B';

-- For GH901234 Category B payment
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 2600, '2024-03-20', 1, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'GH901234' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1900, '2024-04-20', 2, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'GH901234' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 700, '2024-05-20', 3, 'PENDING', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'GH901234' AND af.category_code = 'B';

-- For IJ567890 Category B payment
INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1500, '2024-01-08', 1, 'PAID', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'IJ567890' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1750, '2024-02-08', 2, 'PENDING', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'IJ567890' AND af.category_code = 'B';

INSERT INTO payment_installment (amount, date, installment_number, status, payment_id)
SELECT 1750, '2024-03-08', 3, 'PENDING', p.id
FROM payment p
         JOIN application_file af ON p.application_file_id = af.id
WHERE af.candidate_cin = 'IJ567890' AND af.category_code = 'B';

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

-- First, let's check what application_file IDs were actually created
-- The auto-increment might not match our assumptions

-- Insert exams linked to application files (using proper references)
-- We need to be more careful about the actual IDs that get generated

-- For AB123456 - Category B (should be ID 1)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-03-01', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'AB123456' AND af.category_code = 'B';

INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-15', 'Conduite', 'SCHEDULED', af.id
FROM application_file af
WHERE af.candidate_cin = 'AB123456' AND af.category_code = 'B';

-- For AB123456 - Category A (should be ID 2)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-03-20', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'AB123456' AND af.category_code = 'A';

-- For CD789012 - Category B (should be ID 3)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-03-15', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'CD789012' AND af.category_code = 'B';

INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-20', 'Conduite', 'SCHEDULED', af.id
FROM application_file af
WHERE af.candidate_cin = 'CD789012' AND af.category_code = 'B';

-- For EF345678 - Category B (should be ID 4)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-01-20', 'Code', 'FAILED', af.id
FROM application_file af
WHERE af.candidate_cin = 'EF345678' AND af.category_code = 'B';

INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 2, '2024-02-15', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'EF345678' AND af.category_code = 'B';

-- For EF345678 - Category C (should be ID 5)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-02-20', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'EF345678' AND af.category_code = 'C';

-- For GH901234 - Category B (should be ID 6)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-10', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'GH901234' AND af.category_code = 'B';

INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-05-01', 'Conduite', 'SCHEDULED', af.id
FROM application_file af
WHERE af.candidate_cin = 'GH901234' AND af.category_code = 'B';

-- For IJ567890 - Category B (should be ID 7)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-02-20', 'Code', 'FAILED', af.id
FROM application_file af
WHERE af.candidate_cin = 'IJ567890' AND af.category_code = 'B';

INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 2, '2024-03-20', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'IJ567890' AND af.category_code = 'B';

-- For IJ567890 - Category A (should be ID 8)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-03-05', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'IJ567890' AND af.category_code = 'A';

-- For MN456123 - Category B (should be ID 10)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-15', 'Code', 'SCHEDULED', af.id
FROM application_file af
WHERE af.candidate_cin = 'MN456123' AND af.category_code = 'B';

-- For OP789456 - Category B (should be ID 11)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-03-25', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'OP789456' AND af.category_code = 'B';

INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-10', 'Conduite', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'OP789456' AND af.category_code = 'B';

-- For OP789456 - Category A1 (should be ID 12)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-01', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'OP789456' AND af.category_code = 'A1';

-- For ST345012 - Category B (should be ID 15)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-20', 'Code', 'SCHEDULED', af.id
FROM application_file af
WHERE af.candidate_cin = 'ST345012' AND af.category_code = 'B';

-- For ST345012 - Category C (should be ID 16)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-05', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'ST345012' AND af.category_code = 'C';

-- For UV678345 - Category B (should be ID 17)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-03-10', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'UV678345' AND af.category_code = 'B';

-- For YZ234901 - Category B (should be ID 19)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-25', 'Code', 'SCHEDULED', af.id
FROM application_file af
WHERE af.candidate_cin = 'YZ234901' AND af.category_code = 'B';

-- For AA567234 - Category B (should be ID 20)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-05', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'AA567234' AND af.category_code = 'B';

-- For AA567234 - Category A (should be ID 21)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-04-10', 'Code', 'SCHEDULED', af.id
FROM application_file af
WHERE af.candidate_cin = 'AA567234' AND af.category_code = 'A';

-- For BB890567 - Category B (should be ID 22)
INSERT INTO exam (attempt_number, date, exam_type, status, application_file_id)
SELECT 1, '2024-03-30', 'Code', 'PASSED', af.id
FROM application_file af
WHERE af.candidate_cin = 'BB890567' AND af.category_code = 'B';

-- Insert some sessions
INSERT INTO session (date_session, duration, status, session_type, candidate_cin, instructor_cin, vehicle_immat) VALUES
                                                                                                                     ('2024-03-01', 1.5, 'COMPLETED', 'DRIVING', 'AB123456', 'I123456789', '123-A-45'),
                                                                                                                     ('2024-03-08', 2.0, 'COMPLETED', 'DRIVING', 'AB123456', 'I123456789', '123-A-45'),
                                                                                                                     ('2024-03-15', 1.0, 'COMPLETED', 'THEORY', 'CD789012', 'I987654321', NULL),
                                                                                                                     ('2024-03-22', 1.5, 'SCHEDULED', 'DRIVING', 'GH901234', 'I123456789', '123-A-45'),
                                                                                                                     ('2024-03-29', 2.0, 'COMPLETED', 'DRIVING', 'IJ567890', 'I987654321', '456-B-78');

-- Insert insurance records
INSERT INTO insurance (amount, company, operation_date, next_operation_date, vehicle_immat) VALUES
                                                                                                (2500, 'WAFA Assurance', '2024-01-15', '2025-01-15', '123-A-45'),
                                                                                                (1800, 'Atlanta Assurance', '2024-02-01', '2025-02-01', '456-B-78'),
                                                                                                (3500, 'RMA Watanya', '2024-01-20', '2025-01-20', '789-C-12');

-- Insert oil change records
INSERT INTO oil_change (amount, company, operation_date, next_operation_date, vehicle_immat) VALUES
                                                                                                 (350, 'Garage Mohammed', '2024-02-15', '2024-08-15', '123-A-45'),
                                                                                                 (280, 'Station Total', '2024-01-30', '2024-07-30', '456-B-78'),
                                                                                                 (450, 'Garage Atlas', '2024-03-01', '2024-09-01', '789-C-12');

-- Insert technical visit records
INSERT INTO technical_visit (amount, company, operation_date, next_operation_date, vehicle_immat) VALUES
                                                                                                      (300, 'Centre Technique Auto', '2024-01-10', '2025-01-10', '123-A-45'),
                                                                                                      (250, 'DEKRA', '2024-02-05', '2025-02-05', '456-B-78'),
                                                                                                      (400, 'Bureau Veritas', '2024-01-25', '2025-01-25', '789-C-12');