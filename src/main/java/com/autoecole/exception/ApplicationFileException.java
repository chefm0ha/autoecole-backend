package com.autoecole.exception;

import lombok.Getter;

/**
 * ApplicationFileException represents specific business rule violations
 * related to application file operations.
 * <p>
 * This exception includes error codes for different types of violations:
 * <p>- 100: An active application file already exists for category
 * <p>- 101: A completed application file already exists for category
 * <p>- 102: Cannot cancel completed application file
 * <p>- 103: Application file already canceled
 */
@Getter
public class ApplicationFileException extends BusinessException {

    private final int errorCode;

    public ApplicationFileException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}