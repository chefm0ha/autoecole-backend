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
    candidate_cin VARCHAR(20),
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
    instructor_cin VARCHAR(20),
    vehicle_immat VARCHAR(20),
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
    exam_type VARCHAR(50),
    status VARCHAR(50),
    candidate_cin VARCHAR(20),
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

-- Insert sample user (password should be hashed in real application)
INSERT INTO user (email, first_name, last_name, password, role) VALUES
('admin@autoecole.ma', 'Admin', 'User', '$2a$10$example.hashed.password', 'ADMIN');

-- Insert sample instructors
INSERT INTO instructor (cin, first_name, last_name, email, address, city, gsm, starting_date) VALUES
    ('I123456789', 'Ahmed', 'Bennani', 'ahmed.bennani@autoecole.ma', '123 Rue Mohammed V', 'Casablanca', '0612345678', '2023-01-15'),
    ('I987654321', 'Fatima', 'Alaoui', 'fatima.alaoui@autoecole.ma', '456 Avenue Hassan II', 'Rabat', '0687654321', '2023-02-01');

-- Insert sample vehicles
INSERT INTO vehicle (immatriculation, vehicle_brand, vehicle_type, fuel_type, category, km_initial, amount_vignette) VALUES
    ('123-A-45', 'Toyota', 'Sedan', 'Gasoline', 'B', 50000, 1500.00),
    ('456-B-78', 'Honda', 'Motorcycle', 'Gasoline', 'A', 25000, 800.00),
    ('789-C-12', 'Mercedes', 'Truck', 'Diesel', 'C', 80000, 3000.00);

-- Create indexes for better performance
CREATE INDEX idx_user_email ON user(email);
CREATE INDEX idx_candidate_cin ON candidate(cin);
CREATE INDEX idx_candidate_active ON candidate(is_active);
CREATE INDEX idx_candidate_instructor ON candidate(instructor_cin);
CREATE INDEX idx_candidate_vehicle ON candidate(vehicle_immat);
CREATE INDEX idx_payment_candidate ON payment(candidate_cin);
CREATE INDEX idx_exam_candidate ON exam(candidate_cin);
CREATE INDEX idx_exam_type ON exam(exam_type);
CREATE INDEX idx_session_candidate ON session(candidate_cin);
CREATE INDEX idx_vehicle_category ON vehicle(category);
CREATE INDEX idx_instructor_cin ON instructor(cin);
CREATE INDEX idx_insurance_vehicle ON insurance(vehicle_immat);
CREATE INDEX idx_oil_change_vehicle ON oil_change(vehicle_immat);
CREATE INDEX idx_technical_visit_vehicle ON technical_visit(vehicle_immat);
CREATE INDEX idx_payment_installment_payment ON payment_installment(payment_id);
CREATE INDEX idx_application_file_candidate ON application_file(candidate_cin);

-- Add Foreign Key Constraints
ALTER TABLE vehicle ADD CONSTRAINT fk_vehicle_category
    FOREIGN KEY (category) REFERENCES category(code);

ALTER TABLE candidate ADD CONSTRAINT fk_candidate_instructor
    FOREIGN KEY (instructor_cin) REFERENCES instructor(cin);

ALTER TABLE candidate ADD CONSTRAINT fk_candidate_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation);

ALTER TABLE payment ADD CONSTRAINT fk_payment_candidate
    FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE;

ALTER TABLE payment_installment ADD CONSTRAINT fk_payment_installment_payment
    FOREIGN KEY (payment_id) REFERENCES payment(id) ON DELETE CASCADE;

ALTER TABLE exam ADD CONSTRAINT fk_exam_candidate
    FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE;

ALTER TABLE session ADD CONSTRAINT fk_session_candidate
    FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE;

ALTER TABLE insurance ADD CONSTRAINT fk_insurance_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE;

ALTER TABLE oil_change ADD CONSTRAINT fk_oil_change_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE;

ALTER TABLE technical_visit ADD CONSTRAINT fk_technical_visit_vehicle
    FOREIGN KEY (vehicle_immat) REFERENCES vehicle(immatriculation) ON DELETE CASCADE;

ALTER TABLE application_file ADD CONSTRAINT fk_application_file_candidate
    FOREIGN KEY (candidate_cin) REFERENCES candidate(cin) ON DELETE CASCADE;