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

CREATE TABLE application_file (
    id BIGINT AUTO_INCREMENT,
    practical_hours_completed DOUBLE DEFAULT 0,
    theoretical_hours_completed DOUBLE DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    starting_date DATE,
    -- Status values:
    -- IN_PROGRESS: Student is actively learning (theory/practical phases)
    -- THEORY_EXAM_SCHEDULED: Theory exam has been scheduled
    -- PRACTICAL_EXAM_SCHEDULED: Practical exam has been scheduled
    -- THEORY_PASSED: Theory exam passed, proceeding to practical
    -- THEORY_FAILED: Theory exam failed, needs retake
    -- PRACTICAL_FAILED: Practical exam failed, needs retake
    -- FAILED: Application failed completely (multiple exam failures or other reasons)
    -- GRADUATED: Successfully completed all requirements and obtained license
    -- CANCELLED: Application cancelled/terminated before completion
    status VARCHAR(50) DEFAULT 'IN_PROGRESS',
    file_number VARCHAR(255),
    tax_stamp VARCHAR(50), -- ( 'NOT_PAID", 'PENDING', 'PAID')
    -- Medical visit status values:
    -- NOT_REQUESTED: Manager/staff hasn't asked for eye certification yet
    -- PENDING: Candidate has been asked to get eye certification but hasn't completed it
    -- COMPLETED: Candidate has obtained the eye doctor certification
    medical_visit VARCHAR(255) DEFAULT 'NOT_REQUESTED',
    candidate_cin VARCHAR(20),
    category_code VARCHAR(10),
    CONSTRAINT pk_application_file PRIMARY KEY (id),
    CONSTRAINT fk_application_file_candidate FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE,
    CONSTRAINT fk_application_file_category FOREIGN KEY (category_code) REFERENCES category(code)
);

-- Create Payment table (One-to-One with ApplicationFile)
CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT,
                         paid_amount INTEGER DEFAULT 0,
                         status VARCHAR(50) DEFAULT 'PENDING', -- (PENDING, COMPLETED)
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
                                     installment_number INTEGER DEFAULT 1,
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