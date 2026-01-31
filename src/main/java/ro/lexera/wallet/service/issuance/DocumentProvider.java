package ro.lexera.wallet.service.issuance;

import java.util.Map;

/**
 * Strategy interface for providing domain-specific data for Verifiable Credentials.
 * <p>
 * Implementations of this interface are responsible for interacting with external
 * systems (e.g., University Databases, Government Registries) to retrieve the
 * specific attributes or "claims" required for a credential type.
 * </p>
 * <p>
 * The {@link IssuanceService} automatically discovers all implementations of this
 * interface and selects the appropriate one based on the requested document type.
 * </p>
 */
public interface DocumentProvider {

    /**
     * Defines the unique identifier for the credential type this provider handles.
     * <p>
     * This string is used as a discriminator to match incoming issuance requests
     * to the correct data provider.
     * </p>
     * @return A unique string identifier (e.g., "UNIVERSITY_DIPLOMA", "ID_CARD").
     */
    String supportsType();

    /**
     * Retrieves the specific attribute set (claims) for a given identity.
     * <p>
     * This method maps the anonymized {@code rootIdentityHash} to the internal
     * records of the source system to collect data such as grades, expiry dates,
     * or personal titles.
     * </p>
     * @param rootIdentityHash The unique, anonymized identifier of the user.
     * @return A {@link Map} containing key-value pairs of the credential's data.
     * @throws ro.lexera.wallet.exception.DigitalWalletException if the user data cannot be found.
     */
    Map<String, Object> fetchClaims(String rootIdentityHash);
}
