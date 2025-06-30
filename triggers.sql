-- Trigger to automatically manage exam status changes with detailed application file statuses
-- This trigger fires AFTER INSERT or UPDATE on the exam table

DELIMITER //

-- Drop existing triggers if they exist
DROP TRIGGER IF EXISTS exam_status_management_after_insert //
DROP TRIGGER IF EXISTS exam_status_management_after_update //
DROP TRIGGER IF EXISTS application_file_status_update //
DROP TRIGGER IF EXISTS candidate_active_status_check //

CREATE TRIGGER exam_status_management_after_insert
    AFTER INSERT ON exam
    FOR EACH ROW
BEGIN
    DECLARE v_theory_passed_count INT DEFAULT 0;
    DECLARE v_theory_failed_count INT DEFAULT 0;
    DECLARE v_theory_scheduled_count INT DEFAULT 0;
    DECLARE v_practical_passed_count INT DEFAULT 0;
    DECLARE v_practical_failed_count INT DEFAULT 0;
    DECLARE v_practical_scheduled_count INT DEFAULT 0;
    DECLARE v_total_theory_attempts INT DEFAULT 0;
    DECLARE v_total_practical_attempts INT DEFAULT 0;
    DECLARE v_new_status VARCHAR(50);

    -- Count exam statuses for this application file
    SELECT
        SUM(IF(exam_type = 'THEORY' AND status = 'PASSED', 1, 0)),
        SUM(IF(exam_type = 'THEORY' AND status = 'FAILED', 1, 0)),
        SUM(IF(exam_type = 'THEORY' AND status = 'SCHEDULED', 1, 0)),
        SUM(IF(exam_type = 'PRACTICAL' AND status = 'PASSED', 1, 0)),
        SUM(IF(exam_type = 'PRACTICAL' AND status = 'FAILED', 1, 0)),
        SUM(IF(exam_type = 'PRACTICAL' AND status = 'SCHEDULED', 1, 0)),
        SUM(IF(exam_type = 'THEORY', 1, 0)),
        SUM(IF(exam_type = 'PRACTICAL', 1, 0))
    INTO v_theory_passed_count, v_theory_failed_count, v_theory_scheduled_count,
        v_practical_passed_count, v_practical_failed_count, v_practical_scheduled_count,
        v_total_theory_attempts, v_total_practical_attempts
    FROM exam
    WHERE application_file_id = NEW.application_file_id;

    -- Determine new application status based on exam states
    IF v_theory_passed_count > 0 AND v_practical_passed_count > 0 THEN
        -- Both theory and practical passed - GRADUATED
        SET v_new_status = 'GRADUATED';

    ELSEIF v_total_theory_attempts >= 2 AND v_theory_passed_count = 0 THEN
        -- Failed theory 2 times - FAILED
        SET v_new_status = 'FAILED';

    ELSEIF v_total_practical_attempts >= 3 AND v_practical_passed_count = 0 THEN
        -- Failed practical 3 times - FAILED
        SET v_new_status = 'FAILED';

    ELSEIF v_theory_passed_count > 0 AND v_practical_scheduled_count > 0 THEN
        -- Theory passed, practical scheduled - PRACTICAL_EXAM_SCHEDULED
        SET v_new_status = 'PRACTICAL_EXAM_SCHEDULED';

    ELSEIF v_theory_passed_count > 0 AND v_practical_failed_count > 0 THEN
        -- Theory passed, practical failed - PRACTICAL_FAILED
        SET v_new_status = 'PRACTICAL_FAILED';

    ELSEIF v_theory_passed_count > 0 THEN
        -- Theory passed, no practical attempts yet - THEORY_PASSED
        SET v_new_status = 'THEORY_PASSED';

    ELSEIF v_theory_scheduled_count > 0 THEN
        -- Theory exam scheduled - THEORY_EXAM_SCHEDULED
        SET v_new_status = 'THEORY_EXAM_SCHEDULED';

    ELSEIF v_theory_failed_count > 0 AND v_theory_passed_count = 0 THEN
        -- Theory failed but not exhausted attempts - THEORY_FAILED
        SET v_new_status = 'THEORY_FAILED';

    ELSE
        -- Default state - actively learning
        SET v_new_status = 'IN_PROGRESS';
    END IF;

    -- Update application file status
    UPDATE application_file
    SET status = v_new_status
    WHERE id = NEW.application_file_id;

END //

CREATE TRIGGER exam_status_management_after_update
    AFTER UPDATE ON exam
    FOR EACH ROW
BEGIN
    DECLARE v_theory_passed_count INT DEFAULT 0;
    DECLARE v_theory_failed_count INT DEFAULT 0;
    DECLARE v_theory_scheduled_count INT DEFAULT 0;
    DECLARE v_practical_passed_count INT DEFAULT 0;
    DECLARE v_practical_failed_count INT DEFAULT 0;
    DECLARE v_practical_scheduled_count INT DEFAULT 0;
    DECLARE v_total_theory_attempts INT DEFAULT 0;
    DECLARE v_total_practical_attempts INT DEFAULT 0;
    DECLARE v_new_status VARCHAR(50);

    -- Only proceed if status actually changed
    IF OLD.status != NEW.status THEN

        -- Count exam statuses for this application file
        SELECT
            SUM(IF(exam_type = 'THEORY' AND status = 'PASSED', 1, 0)),
            SUM(IF(exam_type = 'THEORY' AND status = 'FAILED', 1, 0)),
            SUM(IF(exam_type = 'THEORY' AND status = 'SCHEDULED', 1, 0)),
            SUM(IF(exam_type = 'PRACTICAL' AND status = 'PASSED', 1, 0)),
            SUM(IF(exam_type = 'PRACTICAL' AND status = 'FAILED', 1, 0)),
            SUM(IF(exam_type = 'PRACTICAL' AND status = 'SCHEDULED', 1, 0)),
            SUM(IF(exam_type = 'THEORY', 1, 0)),
            SUM(IF(exam_type = 'PRACTICAL', 1, 0))
        INTO v_theory_passed_count, v_theory_failed_count, v_theory_scheduled_count,
            v_practical_passed_count, v_practical_failed_count, v_practical_scheduled_count,
            v_total_theory_attempts, v_total_practical_attempts
        FROM exam
        WHERE application_file_id = NEW.application_file_id;

        -- Determine new application status based on exam states
        IF v_theory_passed_count > 0 AND v_practical_passed_count > 0 THEN
            -- Both theory and practical passed - GRADUATED
            SET v_new_status = 'GRADUATED';

        ELSEIF v_total_theory_attempts >= 3 AND v_theory_passed_count = 0 THEN
            -- Failed theory 3 times - FAILED
            SET v_new_status = 'FAILED';

        ELSEIF v_total_practical_attempts >= 3 AND v_practical_passed_count = 0 THEN
            -- Failed practical 3 times - FAILED
            SET v_new_status = 'FAILED';

        ELSEIF v_theory_passed_count > 0 AND v_practical_scheduled_count > 0 THEN
            -- Theory passed, practical scheduled - PRACTICAL_EXAM_SCHEDULED
            SET v_new_status = 'PRACTICAL_EXAM_SCHEDULED';

        ELSEIF v_theory_passed_count > 0 AND v_practical_failed_count > 0 THEN
            -- Theory passed, practical failed - PRACTICAL_FAILED
            SET v_new_status = 'PRACTICAL_FAILED';

        ELSEIF v_theory_passed_count > 0 THEN
            -- Theory passed, no practical attempts yet - THEORY_PASSED
            SET v_new_status = 'THEORY_PASSED';

        ELSEIF v_theory_scheduled_count > 0 THEN
            -- Theory exam scheduled - THEORY_EXAM_SCHEDULED
            SET v_new_status = 'THEORY_EXAM_SCHEDULED';

        ELSEIF v_theory_failed_count > 0 AND v_theory_passed_count = 0 THEN
            -- Theory failed but not exhausted attempts - THEORY_FAILED
            SET v_new_status = 'THEORY_FAILED';

        ELSE
            -- Default state - actively learning
            SET v_new_status = 'IN_PROGRESS';
        END IF;

        -- Update application file status
        UPDATE application_file
        SET status = v_new_status
        WHERE id = NEW.application_file_id;

    END IF;

END //

CREATE TRIGGER application_file_status_update
    BEFORE UPDATE ON application_file
    FOR EACH ROW
BEGIN
    -- When status changes to terminal states, mark as inactive
    IF NEW.status IN ('FAILED', 'CANCELLED', 'GRADUATED') AND OLD.status != NEW.status THEN
        SET NEW.is_active = FALSE;
    END IF;

    -- When status changes from terminal states back to active states, mark as active
    IF NEW.status NOT IN ('FAILED', 'CANCELLED', 'GRADUATED') AND OLD.status IN ('FAILED', 'CANCELLED', 'GRADUATED') THEN
        SET NEW.is_active = TRUE;
    END IF;
END //

CREATE TRIGGER candidate_active_status_check
    AFTER UPDATE ON application_file
    FOR EACH ROW
BEGIN
    DECLARE v_active_files_count INT DEFAULT 0;

    -- Only proceed if isActive status changed from TRUE to FALSE
    IF OLD.is_active = TRUE AND NEW.is_active = FALSE THEN

        -- Count how many active application files this candidate still has
        SELECT COUNT(*)
        INTO v_active_files_count
        FROM application_file
        WHERE candidate_cin = NEW.candidate_cin
          AND is_active = TRUE;

        -- If no active files remain, mark candidate as inactive
        IF v_active_files_count = 0 THEN
            UPDATE candidate
            SET is_active = FALSE
            WHERE cin = NEW.candidate_cin;
        END IF;

    END IF;

    -- Reverse: If application file becomes active and candidate was inactive, reactivate candidate
    IF OLD.is_active = FALSE AND NEW.is_active = TRUE THEN
        UPDATE candidate
        SET is_active = TRUE
        WHERE cin = NEW.candidate_cin;
    END IF;

END //

DELIMITER ;