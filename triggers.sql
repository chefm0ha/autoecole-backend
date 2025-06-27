-- Trigger for INSERT on payment_installment
DELIMITER $$

CREATE TRIGGER tr_payment_installment_after_insert
    AFTER INSERT ON payment_installment
    FOR EACH ROW
BEGIN
    DECLARE v_total_paid INT DEFAULT 0;
    DECLARE v_total_amount INT DEFAULT 0;

    -- Calculate total paid amount for this payment
    SELECT COALESCE(SUM(amount), 0) INTO v_total_paid
    FROM payment_installment
    WHERE payment_id = NEW.payment_id;

    -- Get total amount for this payment
    SELECT total_amount INTO v_total_amount
    FROM payment
    WHERE id = NEW.payment_id;

    -- Update payment with new paid amount
    UPDATE payment
    SET paid_amount = v_total_paid,
        status = IF(v_total_paid >= v_total_amount, 'COMPLETED', 'PENDING')
    WHERE id = NEW.payment_id;
END$$

-- Trigger for UPDATE on payment_installment
CREATE TRIGGER tr_payment_installment_after_update
    AFTER UPDATE ON payment_installment
    FOR EACH ROW
BEGIN
    DECLARE v_total_paid INT DEFAULT 0;
    DECLARE v_total_amount INT DEFAULT 0;

    -- Calculate total paid amount for this payment
    SELECT COALESCE(SUM(amount), 0) INTO v_total_paid
    FROM payment_installment
    WHERE payment_id = NEW.payment_id;

    -- Get total amount for this payment
    SELECT total_amount INTO v_total_amount
    FROM payment
    WHERE id = NEW.payment_id;

    -- Update payment with new paid amount
    UPDATE payment
    SET paid_amount = v_total_paid,
        status = IF(v_total_paid >= v_total_amount, 'COMPLETED', 'PENDING')
    WHERE id = NEW.payment_id;
END$$

-- Trigger for DELETE on payment_installment
CREATE TRIGGER tr_payment_installment_after_delete
    AFTER DELETE ON payment_installment
    FOR EACH ROW
BEGIN
    DECLARE v_total_paid INT DEFAULT 0;
    DECLARE v_total_amount INT DEFAULT 0;

    -- Calculate total paid amount for this payment
    SELECT COALESCE(SUM(amount), 0) INTO v_total_paid
    FROM payment_installment
    WHERE payment_id = OLD.payment_id;

    -- Get total amount for this payment
    SELECT total_amount INTO v_total_amount
    FROM payment
    WHERE id = OLD.payment_id;

    -- Update payment with new paid amount
    UPDATE payment
    SET paid_amount = v_total_paid,
        status = IF(v_total_paid >= v_total_amount, 'COMPLETED', 'PENDING')
    WHERE id = OLD.payment_id;
END$$

CREATE TRIGGER tr_payment_installment_before_insert
    BEFORE INSERT ON payment_installment
    FOR EACH ROW
BEGIN
    DECLARE v_max_installment_number INT DEFAULT 0;

    -- Get the maximum installment number for this payment
    SELECT COALESCE(MAX(installment_number), 0) INTO v_max_installment_number
    FROM payment_installment
    WHERE payment_id = NEW.payment_id;

    -- Set the new installment number as max + 1
    SET NEW.installment_number = v_max_installment_number + 1;
END$$

DELIMITER ;

DELIMITER //

CREATE PROCEDURE save_exam_with_logic(
    IN p_application_file_id BIGINT,
    IN p_exam_type VARCHAR(50),
    IN p_date DATE,
    IN p_status VARCHAR(50)
)
BEGIN
    DECLARE v_attempt_number INT DEFAULT 1;
    DECLARE v_current_status VARCHAR(50);
    DECLARE v_theory_passed BOOLEAN DEFAULT FALSE;
    DECLARE v_practical_passed BOOLEAN DEFAULT FALSE;
    DECLARE v_theory_failed_count INT DEFAULT 0;
    DECLARE v_practical_failed_count INT DEFAULT 0;
    DECLARE v_new_application_status VARCHAR(50);
    DECLARE v_scheduled_theory_count INT DEFAULT 0;
    DECLARE v_scheduled_practical_count INT DEFAULT 0;

    -- Start transaction
    START TRANSACTION;

    -- Check for existing scheduled exams
    SELECT
        SUM(CASE WHEN exam_type = 'THEORY' AND status = 'SCHEDULED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN exam_type = 'PRACTICAL' AND status = 'SCHEDULED' THEN 1 ELSE 0 END)
    INTO v_scheduled_theory_count, v_scheduled_practical_count
    FROM exam
    WHERE application_file_id = p_application_file_id;

    -- Check current exam status counts
    SELECT
        SUM(CASE WHEN exam_type = 'THEORY' AND status = 'PASSED' THEN 1 ELSE 0 END) > 0,
        SUM(CASE WHEN exam_type = 'PRACTICAL' AND status = 'PASSED' THEN 1 ELSE 0 END) > 0,
        SUM(CASE WHEN exam_type = 'THEORY' AND status = 'FAILED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN exam_type = 'PRACTICAL' AND status = 'FAILED' THEN 1 ELSE 0 END)
    INTO v_theory_passed, v_practical_passed, v_theory_failed_count, v_practical_failed_count
    FROM exam
    WHERE application_file_id = p_application_file_id;

    -- If trying to schedule an exam, check business rules
    IF p_status = 'SCHEDULED' THEN
        -- Cannot schedule theory exam if there's already a scheduled theory exam
        IF p_exam_type = 'THEORY' AND v_scheduled_theory_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule theory exam: there is already a scheduled theory exam. Complete the current exam first.';
        END IF;

        -- Cannot schedule practical exam if there's already a scheduled practical exam
        IF p_exam_type = 'PRACTICAL' AND v_scheduled_practical_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule practical exam: there is already a scheduled practical exam. Complete the current exam first.';
        END IF;

        -- Cannot schedule practical exam if theory hasn't been passed yet
        IF p_exam_type = 'PRACTICAL' AND NOT v_theory_passed THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot schedule practical exam: theory exam must be passed first.';
        END IF;
    END IF;

    -- Get current attempt number for this exam type
    SELECT COALESCE(MAX(attempt_number), 0) + 1
    INTO v_attempt_number
    FROM exam
    WHERE application_file_id = p_application_file_id
      AND exam_type = p_exam_type;

    -- Check if attempt number exceeds limit
    IF v_attempt_number > 3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Maximum number of attempts (3) exceeded for this exam type';
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
                 v_attempt_number
             );

    -- Recalculate exam status counts after insertion
    SELECT
        SUM(CASE WHEN exam_type = 'THEORY' AND status = 'PASSED' THEN 1 ELSE 0 END) > 0,
        SUM(CASE WHEN exam_type = 'PRACTICAL' AND status = 'PASSED' THEN 1 ELSE 0 END) > 0,
        SUM(CASE WHEN exam_type = 'THEORY' AND status = 'FAILED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN exam_type = 'PRACTICAL' AND status = 'FAILED' THEN 1 ELSE 0 END)
    INTO v_theory_passed, v_practical_passed, v_theory_failed_count, v_practical_failed_count
    FROM exam
    WHERE application_file_id = p_application_file_id;

    -- Determine new application file status
    CASE
        -- If just scheduled an exam
        WHEN p_status = 'SCHEDULED' THEN
            IF p_exam_type = 'THEORY' THEN
                SET v_new_application_status = 'THEORY_EXAM_SCHEDULED';
            ELSE
                SET v_new_application_status = 'PRACTICAL_EXAM_SCHEDULED';
            END IF;

        -- If exam was passed
        WHEN p_status = 'PASSED' THEN
            IF p_exam_type = 'THEORY' THEN
                SET v_new_application_status = 'THEORY_PASSED';
            ELSEIF p_exam_type = 'PRACTICAL' AND v_theory_passed THEN
                SET v_new_application_status = 'GRADUATED';
            ELSE
                SET v_new_application_status = 'PRACTICAL_PASSED';
            END IF;

        -- If exam was failed
        WHEN p_status = 'FAILED' THEN
            -- Check if this is the second failure for this exam type
            IF (p_exam_type = 'THEORY' AND v_theory_failed_count >= 2) OR
               (p_exam_type = 'PRACTICAL' AND v_practical_failed_count >= 2) THEN
                SET v_new_application_status = 'FAILED';
            ELSE
                IF p_exam_type = 'THEORY' THEN
                    SET v_new_application_status = 'THEORY_FAILED';
                ELSE
                    SET v_new_application_status = 'PRACTICAL_FAILED';
                END IF;
            END IF;

        ELSE
            SET v_new_application_status = 'IN_PROGRESS';
        END CASE;

    -- Update application file status
    UPDATE application_file
    SET status = v_new_application_status,
        is_active = CASE
                        WHEN v_new_application_status IN ('FAILED', 'GRADUATED') THEN FALSE
                        ELSE TRUE
            END
    WHERE id = p_application_file_id;

    -- Commit transaction
    COMMIT;

END//

DELIMITER ;