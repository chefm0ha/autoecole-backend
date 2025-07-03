DELIMITER //

DROP PROCEDURE IF EXISTS save_exam_with_logic //
DROP PROCEDURE IF EXISTS save_payment_installment //
DROP PROCEDURE IF EXISTS update_exam_status //
DROP PROCEDURE IF EXISTS cancel_application_file //

CREATE PROCEDURE save_exam_with_logic(
    IN p_application_file_id BIGINT,
    IN p_exam_type VARCHAR(50),
    IN p_date DATE,
    IN p_status VARCHAR(50)
)
BEGIN
    DECLARE v_attempt_number INT DEFAULT 1;
    DECLARE v_existing_scheduled_count INT DEFAULT 0;
    DECLARE v_existing_passed_count INT DEFAULT 0;
    DECLARE v_theory_passed INT DEFAULT 0;
    DECLARE v_max_attempts INT DEFAULT 3;
    DECLARE v_exam_type_attempts INT DEFAULT 0;
    DECLARE v_tax_stamp_status VARCHAR(50);
    DECLARE v_medical_visit_status VARCHAR(50);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    START TRANSACTION;

    -- Check if application file exists and is active
    IF NOT EXISTS (SELECT 1 FROM application_file WHERE id = p_application_file_id AND is_active = 1) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Application file not found or inactive';
    END IF;

    -- Check tax stamp and medical visit status
    SELECT tax_stamp, medical_visit
    INTO v_tax_stamp_status, v_medical_visit_status
    FROM application_file
    WHERE id = p_application_file_id;

    -- Business Rule: Tax stamp must be PAID before scheduling any exam
    IF v_tax_stamp_status != 'PAID' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule exam: Tax stamp must be PAID first';
    END IF;

    -- Business Rule: Medical visit must be COMPLETED before scheduling any exam
    IF v_medical_visit_status != 'COMPLETED' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule exam: Medical visit must be COMPLETED first';
    END IF;

    -- Check if there's already a PASSED exam of this type (THIS CHECK MUST BE FIRST)
    SELECT COUNT(*)
    INTO v_existing_passed_count
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = p_exam_type
      AND status = 'PASSED';

    -- Business Rule: Cannot add another exam if one is already PASSED
    IF v_existing_passed_count > 0 THEN
        IF p_exam_type = 'THEORY' THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add another THEORY exam: already have a PASSED exam of this type';
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot add another PRACTICAL exam: already have a PASSED exam of this type';
        END IF;
    END IF;

    -- Get the next attempt number (sequential across all exams for this application file)
    SELECT COALESCE(MAX(attempt_number), 0) + 1
    INTO v_attempt_number
    FROM exam
    WHERE application_file_id = p_application_file_id;

    -- Check maximum attempts per exam type (2 for theory, 3 for practical)
    SELECT COUNT(*)
    INTO v_exam_type_attempts
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = p_exam_type;

    IF p_exam_type = 'THEORY' AND v_exam_type_attempts >= 2 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Maximum number of attempts (2) exceeded for theory exam';
    ELSEIF p_exam_type = 'PRACTICAL' AND v_exam_type_attempts >= 3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Maximum number of attempts (3) exceeded for practical exam';
    END IF;

    -- Check if there's already a SCHEDULED exam of this type
    SELECT COUNT(*)
    INTO v_existing_scheduled_count
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = p_exam_type
      AND status = 'SCHEDULED';

    -- Business Rule: Cannot schedule if there's already a scheduled exam of the same type
    IF p_status = 'SCHEDULED' AND v_existing_scheduled_count > 0 THEN
        IF p_exam_type = 'THEORY' THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule THEORY exam: there is already a scheduled THEORY exam. Complete the current exam first.';
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule PRACTICAL exam: there is already a scheduled PRACTICAL exam. Complete the current exam first.';
        END IF;
    END IF;

    -- Business Rule: For PRACTICAL exam, check if THEORY is passed first
    IF p_exam_type = 'PRACTICAL' THEN
        SELECT COUNT(*)
        INTO v_theory_passed
        FROM exam
        WHERE application_file_id = p_application_file_id
          AND exam_type = 'THEORY'
          AND status = 'PASSED';

        IF v_theory_passed = 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule practical exam: theory exam must be passed first';
        END IF;
    END IF;

    -- Insert the new exam
    INSERT INTO exam (application_file_id, exam_type, date, status, attempt_number)
    VALUES (p_application_file_id, p_exam_type, p_date, p_status, v_attempt_number);

    -- If this exam is PASSED and it's the second type to be passed, mark application as COMPLETED
    IF p_status = 'PASSED' THEN
        IF (SELECT COUNT(DISTINCT exam_type)
            FROM exam
            WHERE application_file_id = p_application_file_id
              AND status = 'PASSED'
              AND exam_type IN ('THEORY', 'PRACTICAL')) = 2 THEN

            UPDATE application_file
            SET status = 'COMPLETED'
            WHERE id = p_application_file_id;
        END IF;
    END IF;

    COMMIT;
END //

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