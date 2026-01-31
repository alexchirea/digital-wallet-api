package ro.lexera.wallet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all business logic failures within the Lexera Digital Wallet.
 * <p>
 * This exception carries a specific {@link HttpStatus} to be returned to the client
 * and a machine-readable {@code errorCode} used for frontend localization and debugging.
 * </p>
 */
@Getter
public class DigitalWalletException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public DigitalWalletException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    // Default to 500 Internal Server Error if status isn't provided
    public DigitalWalletException(String message, String errorCode) {
        this(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
