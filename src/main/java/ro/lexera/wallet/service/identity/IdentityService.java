package ro.lexera.wallet.service.identity;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.lexera.wallet.repository.UserRepository;

/**
 * Service responsible for the lifecycle management of digital identities within the ecosystem.
 * <p>
 * The {@code IdentityService} serves as the primary abstraction for identity onboarding,
 * verification, and PII (Personally Identifiable Information) protection. It ensures that
 * users are uniquely identified via deterministic hashing while maintaining a
 * "Privacy-by-Design" architecture by never storing raw legal attributes.
 * </p>
 * * <p><b>Core Responsibilities:</b></p>
 * <ul>
 * <li>Generating irreversible, salted SHA-256 Root Identity Hashes.</li>
 * <li>Managing the association between physical identities and digital wallet records.</li>
 * <li>Preventing duplicate registrations through identity-existence verification.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class IdentityService {

    @Value("${wallet.app.secret.salt}")
    private String secretSalt;

    private final UserRepository userRepository;

    /**
     * Generates a deterministic, irreversible hash for a user based on their legal identity.
     * <p>
     * This method acts as an anonymization layer, converting sensitive PII (Personally
     * Identifiable Information) into a unique "Root Identity Hash". The process involves:
     * <ul>
     * <li>Normalization: Lowercasing and removing all whitespace.</li>
     * <li>Concatenation: Merging name and national ID components.</li>
     * <li>Salting: Appending a server-side secret to prevent rainbow table attacks.</li>
     * </ul>
     * </p>
     * @param firstName  The user's legal first name(s).
     * @param lastName   The user's legal last name.
     * @param nationalId The government-issued unique identifier (e.g., CNP, SSN).
     * @return A SHA-256 hexadecimal string representing the user's digital root.
     */
    public String createRootHash(String firstName, String lastName, String nationalId) {
        String rawData = (firstName + lastName + nationalId).toLowerCase().replaceAll("\\s", "");
        return DigestUtils.sha256Hex(rawData + secretSalt);
    }

    /**
     * Checks the existence of a specific root hash within the secure identity registry.
     * <p>
     * This is used during the onboarding flow to prevent duplicate registrations
     * and to verify if a document request originates from a known identity.
     * </p>
     * * @param hash The SHA-256 root identity hash to look up.
     * @return {@code true} if the identity exists in the database; {@code false} otherwise.
     */
    public boolean isUserAlreadyRegistered(String hash) {
        return userRepository.findByRootIdentityHash(hash).isPresent();
    }

}
