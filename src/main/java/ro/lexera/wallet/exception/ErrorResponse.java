package ro.lexera.wallet.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) used to provide a consistent error structure across the API.
 * <p>
 * When an exception occurs, this object is serialized into JSON and returned in the
 * response body, allowing clients to handle failures gracefully using the {@code code}
 * and {@code status} fields.
 * </p>
 */
@Getter
@Builder
@JsonPropertyOrder({ "timestamp", "status", "code", "error", "message", "path" })
public class ErrorResponse {

    /**
     * The precise moment the error occurred in ISO-8601 UTC format.
     * <p>Used for log correlation and debugging client-side issues.</p>
     */
    private final Instant timestamp;

    /**
     * The HTTP status code value (e.g., 400, 404, 500).
     * <p>Redundant but included for ease of access in frontend logic.</p>
     */
    private final int status;

    /**
     * The short-form HTTP error phrase associated with the status (e.g., "Not Found").
     */
    private final String error;

    /**
     * The internal application-specific error code (e.g., "ERR_CREDENTIAL_REVOKED").
     * <p>
     * <b>Note:</b> This is the primary field used by frontend applications
     * to trigger specific UI behaviors or localized messages.
     * </p>
     */
    private final String code;

    /**
     * A human-readable description providing detail about the specific failure.
     * <p>Should be used for developer debugging rather than direct display to end-users.</p>
     */
    private final String message;

    /**
     * The URI path where the error was triggered.
     * <p>Example: {@code /api/v1/issuance/request}</p>
     */
    private final String path;
}
