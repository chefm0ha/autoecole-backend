package com.autoecole.exception;

import lombok.Getter;

/**
 * ApplicationFileException represents specific business rule violations
 * related to application file operations.
 * <p>
 * This exception includes error codes for different types of violations:
 * <p>- 100: Candidate not found
 * <p>- 101: Category not found
 * <p>- 102: An active application file already exists for category
 * <p>- 103: A completed application file already exists for category
 * <p>- 104: Application file not found
 * <p>- 105: Cannot cancel completed application file
 * <p>- 106: Application file already canceled
 * <p>- 107: Application file failed due to multiple exam failures
 * <p>- 108: Application file already completed
 * <p>- 109: Application file already canceled (for close operation)
 */
@Getter
public class ApplicationFileException extends BusinessException {

    private final int errorCode;

    public ApplicationFileException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}