package ro.lexera.wallet.service.issuance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ro.lexera.wallet.exception.DigitalWalletException;
import ro.lexera.wallet.service.crypto.SigningService;
import ro.lexera.wallet.service.status.StatusService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestration service responsible for the end-to-end issuance of Verifiable Credentials.
 * <p>
 * This service coordinates between {@link DocumentProvider} implementations, the
 * {@link StatusService}, and the {@link SigningService} to transform raw identity data
 * into a legally valid, signed digital document.
 * </p>
 *  <p><b>Issuance Workflow:</b></p>
 * <ol>
 * <li>Identify the correct data provider based on the requested {@code type}.</li>
 * <li>Retrieve domain-specific claims for the provided identity hash.</li>
 * <li>Generate a unique {@code credentialId} (JTI) and initialize its status in the registry.</li>
 * <li>Cryptographically sign the final payload to produce a JWT.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class IssuanceService {

    /**
     * Automatically injected list of all available document strategy implementations.
     */
    private final List<DocumentProvider> providers;

    private final SigningService signingService;
    private final StatusService statusService;

    /**
     * Executes the full issuance process for a specific document type.
     * <p>
     * This method ensures that no document is signed without first being registered
     * in the {@link StatusService}, maintaining the integrity of the revocation system.
     * </p>
     *
     * @param rootIdentityHash The anonymized identifier of the user requesting the document.
     * @param type             The type of document to issue (e.g., "UNIVERSITY_DIPLOMA").
     * @return A Base64URL encoded JWT representing the signed Verifiable Credential.
     * @throws DigitalWalletException if the document type is unsupported or if data retrieval fails.
     */
    public String issueCredential(String rootIdentityHash, String type) {
        DocumentProvider provider = providers.stream()
                .filter(p -> p.supportsType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new DigitalWalletException(
                        "The document type '" + type + "' is not supported by this issuer.",
                        "ERR_UNSUPPORTED_TYPE", // This matches your test case!
                        HttpStatus.BAD_REQUEST
                ));

        Map<String, Object> claims = provider.fetchClaims(rootIdentityHash);

        UUID credentialId = UUID.randomUUID();
        statusService.initializeStatus(credentialId);

        return signingService.signCredential(rootIdentityHash, type, claims, credentialId);
    }

}
