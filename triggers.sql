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

DELIMITER ;