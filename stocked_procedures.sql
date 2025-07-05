DELIMITER //

DROP PROCEDURE IF EXISTS save_application_file_with_validation //
DROP PROCEDURE IF EXISTS save_exam_with_logic //
DROP PROCEDURE IF EXISTS save_payment_installment //
DROP PROCEDURE IF EXISTS update_exam_status //
DROP PROCEDURE IF EXISTS cancel_application_file //

CREATE PROCEDURE save_application_file_with_validation(
    IN p_candidate_cin VARCHAR(255),
    IN p_category_code VARCHAR(255),
    IN p_total_amount INT,
    IN p_initial_amount INT,
    OUT p_application_file_id BIGINT
)
BEGIN
    DECLARE v_candidate_exists INT DEFAULT 0;
    DECLARE v_category_exists INT DEFAULT 0;
    DECLARE v_active_file_exists INT DEFAULT 0;
    DECLARE v_completed_file_exists INT DEFAULT 0;
    DECLARE v_new_file_number VARCHAR(255);
    DECLARE v_new_application_file_id BIGINT;
    DECLARE v_new_payment_id BIGINT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    START TRANSACTION;

    -- Check if candidate exists
    SELECT COUNT(*) INTO v_candidate_exists
    FROM candidate
    WHERE cin = p_candidate_cin;

    IF v_candidate_exists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Candidate not found';
    END IF;

    -- Check if category exists
    SELECT COUNT(*) INTO v_category_exists
    FROM category
    WHERE code = p_category_code;

    IF v_category_exists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Category not found';
    END IF;

    -- Check for any active application file (IN_PROGRESS with is_active = TRUE)
    SELECT COUNT(*) INTO v_active_file_exists
    FROM application_file af
    WHERE af.candidate_cin = p_candidate_cin
      AND af.category_code = p_category_code
      AND af.status = 'IN_PROGRESS'
      AND af.is_active = TRUE;

    IF v_active_file_exists > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add application file: An active application file is already in progress for this category';
    END IF;

    -- Check for any completed application file
    SELECT COUNT(*) INTO v_completed_file_exists
    FROM application_file af
    WHERE af.candidate_cin = p_candidate_cin
      AND af.category_code = p_category_code
      AND af.status = 'COMPLETED';

    IF v_completed_file_exists > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add application file: A completed application file already exists for this category';
    END IF;

    -- Note: Only FAILED status or no existing application files are allowed to create new application files

    -- Generate file number
    SET v_new_file_number = CONCAT('test-', p_candidate_cin, '-test');

    -- Create new application file
    INSERT INTO application_file (
        candidate_cin,
        category_code,
        practical_hours_completed,
        theoretical_hours_completed,
        is_active,
        starting_date,
        status,
        file_number,
        tax_stamp,
        medical_visit
    ) VALUES (
                 p_candidate_cin,
                 p_category_code,
                 0.0,
                 0.0,
                 TRUE,
                 CURDATE(),
                 'IN_PROGRESS',
                 v_new_file_number,
                 'NOT_PAID',
                 'NOT_REQUESTED'
             );

    SET v_new_application_file_id = LAST_INSERT_ID();

    -- Create payment record
    INSERT INTO payment (
        application_file_id,
        paid_amount,
        status,
        total_amount
    ) VALUES (
                 v_new_application_file_id,
                 0,
                 'PENDING',
                 p_total_amount
             );

    SET v_new_payment_id = LAST_INSERT_ID();

    -- Create initial payment installment using existing procedure
    CALL save_payment_installment(v_new_payment_id, p_initial_amount);

    SET p_application_file_id = v_new_application_file_id;

    COMMIT;

END//

CREATE PROCEDURE save_exam_with_logic(
    IN p_application_file_id BIGINT,
    IN p_exam_type VARCHAR(50),
    IN p_date DATE,
    IN p_status VARCHAR(50)
)
BEGIN
    DECLARE v_application_file_exists INT DEFAULT 0;
    DECLARE v_tax_stamp VARCHAR(50);
    DECLARE v_medical_visit VARCHAR(50);
    DECLARE v_current_attempt_number INT DEFAULT 0;
    DECLARE v_theory_passed INT DEFAULT 0;
    DECLARE v_practical_passed INT DEFAULT 0;
    DECLARE v_scheduled_theory_count INT DEFAULT 0;
    DECLARE v_scheduled_practical_count INT DEFAULT 0;
    DECLARE v_total_failed_count INT DEFAULT 0;
    DECLARE v_failed_theory_count INT DEFAULT 0;
    DECLARE v_failed_practical_count INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    START TRANSACTION;

    -- Check if application file exists and is active
    SELECT COUNT(*), tax_stamp, medical_visit INTO v_application_file_exists, v_tax_stamp, v_medical_visit
    FROM application_file
    WHERE id = p_application_file_id AND is_active = TRUE;

    IF v_application_file_exists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Application file not found or not active';
    END IF;

    -- Validate tax stamp and medical visit requirements
    IF v_tax_stamp != 'PAID' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tax stamp must be PAID';
    END IF;

    IF v_medical_visit != 'COMPLETED' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Medical visit must be COMPLETED';
    END IF;

    -- Validate exam type
    IF p_exam_type NOT IN ('THEORY', 'PRACTICAL') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid exam type. Must be THEORY or PRACTICAL';
    END IF;

    -- Validate exam status
    IF p_status NOT IN ('SCHEDULED', 'PASSED', 'FAILED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid exam status. Must be SCHEDULED, PASSED, or FAILED';
    END IF;

    -- Count existing exams by type and status
    SELECT COUNT(*) INTO v_theory_passed
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = 'THEORY'
      AND status = 'PASSED';

    SELECT COUNT(*) INTO v_practical_passed
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = 'PRACTICAL'
      AND status = 'PASSED';

    SELECT COUNT(*) INTO v_scheduled_theory_count
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = 'THEORY'
      AND status = 'SCHEDULED';

    SELECT COUNT(*) INTO v_scheduled_practical_count
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = 'PRACTICAL'
      AND status = 'SCHEDULED';

    SELECT COUNT(*) INTO v_failed_theory_count
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = 'THEORY'
      AND status = 'FAILED';

    SELECT COUNT(*) INTO v_failed_practical_count
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = 'PRACTICAL'
      AND status = 'FAILED';

    -- Total failed exams across all types
    SET v_total_failed_count = v_failed_theory_count + v_failed_practical_count;

    -- Business logic validation
    IF p_exam_type = 'THEORY' THEN
        -- Theory exam validation
        IF v_theory_passed > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add another THEORY exam: already have a PASSED exam of this type';
        END IF;

        IF p_status = 'SCHEDULED' AND v_scheduled_theory_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule theory exam: there is already a scheduled theory exam. Complete the current exam first.';
        END IF;

        -- Check if adding this failed exam would exceed the total failure limit
        IF p_status = 'FAILED' AND v_total_failed_count >= 1 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Maximum number of failures exceeded: candidate has already failed once';
        END IF;

        SET v_current_attempt_number = v_failed_theory_count + 1;

    ELSE -- PRACTICAL exam
    -- Practical exam validation
        IF v_practical_passed > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add another PRACTICAL exam: already have a PASSED exam of this type';
        END IF;

        IF v_theory_passed = 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule practical exam: theory exam must be passed first';
        END IF;

        IF p_status = 'SCHEDULED' AND v_scheduled_practical_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule practical exam: there is already a scheduled practical exam. Complete the current exam first.';
        END IF;

        -- Check if adding this failed exam would exceed the total failure limit
        IF p_status = 'FAILED' AND v_total_failed_count >= 1 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Maximum number of failures exceeded: candidate has already failed once';
        END IF;

        SET v_current_attempt_number = v_failed_practical_count + 1;
    END IF;

    -- Insert the new exam
    INSERT INTO exam (
        application_file_id,
        exam_type,
        date,
        status,
        attempt_number
    ) VALUES (
                 p_application_file_id,
                 p_exam_type,
                 p_date,
                 p_status,
                 v_current_attempt_number
             );

    -- Update application file status if needed
    -- Case 1: Both theory and practical are passed -> COMPLETED
    IF v_theory_passed > 0 AND (v_practical_passed > 0 OR (p_exam_type = 'PRACTICAL' AND p_status = 'PASSED')) THEN
        UPDATE application_file
        SET status = 'COMPLETED', is_active = FALSE
        WHERE id = p_application_file_id;

        -- Case 2: Second failure (total failures = 1 after this insert) -> FAILED
    ELSEIF p_status = 'FAILED' AND v_total_failed_count + 1 >= 2 THEN
        UPDATE application_file
        SET status = 'FAILED', is_active = FALSE
        WHERE id = p_application_file_id;
    END IF;

    COMMIT;
END//

CREATE PROCEDURE save_payment_installment(
    IN p_payment_id BIGINT,
    IN p_amount INT
)
BEGIN
    DECLARE v_installment_number INT DEFAULT 1;
    DECLARE v_new_paid_amount INT DEFAULT 0;
    DECLARE v_total_amount INT DEFAULT 0;
    DECLARE v_new_status VARCHAR(50) DEFAULT 'PENDING';

    -- Start transaction
    START TRANSACTION;

    -- Get next installment number
    SELECT COALESCE(MAX(installment_number), 0) + 1
    INTO v_installment_number
    FROM payment_installment
    WHERE payment_id = p_payment_id;

    -- Insert the new installment
    INSERT INTO payment_installment (
        payment_id,
        amount,
        date,
        installment_number
    ) VALUES (
                 p_payment_id,
                 p_amount,
                 CURDATE(),
                 v_installment_number
             );

    -- Get total amount from payment table
    SELECT total_amount
    INTO v_total_amount
    FROM payment
    WHERE id = p_payment_id;

    -- Calculate new paid amount from installments
    SELECT COALESCE(SUM(amount), 0)
    INTO v_new_paid_amount
    FROM payment_installment
    WHERE payment_id = p_payment_id;

    -- Determine new payment status
    IF v_new_paid_amount >= v_total_amount THEN
        SET v_new_status = 'COMPLETED';
    ELSEIF v_new_paid_amount > 0 THEN
        SET v_new_status = 'PARTIAL';
    ELSE
        SET v_new_status = 'PENDING';
    END IF;

    -- Update payment table
    UPDATE payment
    SET paid_amount = v_new_paid_amount,
        status = v_new_status
    WHERE id = p_payment_id;

    -- Commit transaction
    COMMIT;

END//

CREATE PROCEDURE update_exam_status(
    IN p_exam_id BIGINT,
    IN p_new_status VARCHAR(50)
)
BEGIN
    DECLARE v_current_status VARCHAR(50);
    DECLARE v_exam_type VARCHAR(50);
    DECLARE v_application_file_id BIGINT;
    DECLARE v_attempt_number INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    START TRANSACTION;

    -- Get current exam details
    SELECT status, exam_type, application_file_id, attempt_number
    INTO v_current_status, v_exam_type, v_application_file_id, v_attempt_number
    FROM exam
    WHERE id = p_exam_id;

    -- Check if exam exists
    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Exam not found';
    END IF;

    -- Business rule: Cannot change status of already passed exam
    IF v_current_status = 'PASSED' AND p_new_status != 'PASSED' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot change status of an already passed exam';
    END IF;

    -- Business rule: If marking as PASSED, update application file status if both exams are passed
    IF p_new_status = 'PASSED' THEN
        -- Update the exam status
        UPDATE exam
        SET status = p_new_status
        WHERE id = p_exam_id;

        -- Check if both theory and practical exams are passed
        IF (SELECT COUNT(*) FROM exam
            WHERE application_file_id = v_application_file_id
              AND status = 'PASSED'
              AND exam_type IN ('THEORY', 'PRACTICAL')) = 2 THEN

            -- Update application file status to COMPLETED
            UPDATE application_file
            SET status = 'COMPLETED'
            WHERE id = v_application_file_id;
        END IF;

    ELSE
        -- For other status updates, just update the exam
        UPDATE exam
        SET status = p_new_status
        WHERE id = p_exam_id;

        -- If marking as FAILED and it was previously PASSED,
        -- might need to revert application file status
        IF v_current_status = 'PASSED' AND p_new_status = 'FAILED' THEN
            UPDATE application_file
            SET status = 'IN_PROGRESS'
            WHERE id = v_application_file_id;
        END IF;
    END IF;

    COMMIT;
END //

CREATE PROCEDURE cancel_application_file(
    IN p_application_file_id BIGINT
)
BEGIN
    DECLARE v_current_status VARCHAR(50);
    DECLARE v_candidate_cin VARCHAR(50);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    START TRANSACTION;

    -- Get current application file details
    SELECT status, candidate_cin
    INTO v_current_status, v_candidate_cin
    FROM application_file
    WHERE id = p_application_file_id;

    -- Check if application file exists
    IF v_current_status IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Application file not found';
    END IF;

    -- Business rule: Cannot cancel if already COMPLETED
    IF v_current_status = 'COMPLETED' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot cancel a COMPLETED application file';
    END IF;

    -- Business rule: Cannot cancel if already cancelled
    IF v_current_status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Application file is already cancelled';
    END IF;

    -- Update application file status to CANCELLED and mark as inactive
    UPDATE application_file
    SET status = 'CANCELLED',
        is_active = FALSE
    WHERE id = p_application_file_id;

    -- Cancel all scheduled exams for this application file
    UPDATE exam
    SET status = 'FAILED'
    WHERE application_file_id = p_application_file_id
      AND status = 'SCHEDULED';

    -- Check if candidate has any other active application files
    IF NOT EXISTS (
        SELECT 1 FROM application_file
        WHERE candidate_cin = v_candidate_cin
          AND is_active = TRUE
          AND id != p_application_file_id
    ) THEN
        -- No other active files, mark candidate as inactive
        UPDATE candidate
        SET is_active = FALSE
        WHERE cin = v_candidate_cin;
    END IF;

    COMMIT;
END //

DELIMITER ;