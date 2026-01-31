package ro.lexera.wallet.service.crypto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Serializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.lexera.wallet.config.RsaKeyConfigProvider;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Core cryptographic service for generating and signing Digital Credentials.
 * <p>
 * This service utilizes JSON Web Token (JWT) standards to wrap identity claims into
 * non-repudiable, tamper-evident digital documents. It relies on RSA asymmetric
 * encryption provided by the {@link RsaKeyConfigProvider}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SigningService {

    private final RsaKeyConfigProvider rsaKeyConfigProvider;
    private final Serializer<Map<String, ?>> serializer;

    /**
     * Signs a full Verifiable Credential in JWT format.
     * <p>
     * The resulting token includes a {@code jti} (JWT ID) which is critical for
     * the revocation registry to track this specific issuance instance.
     * </p>
     *
     * @param subjectId The unique identifier of the holder (e.g., identity hash).
     * @param docType   The schema/type of the credential (e.g., IDENTITY_CARD).
     * @param claims    The business data to be encoded within the JWT payload.
     * @param jti       The unique UUID that identifies this specific credential issuance.
     * @return A Base64URL encoded String representing the signed JWT.
     */
    public String signCredential(String subjectId, String docType, Map<String, Object> claims, UUID jti) {
        return Jwts.builder()
                .json(serializer)
                .id(jti.toString())          // 1. Set the JTI (JWT ID) for revocation tracking
                .subject(subjectId)          // 2. Who this belongs to
                .claim("type", docType)   // 3. What this document is
                .claims(claims)              // 4. The specific data from the Provider
                .issuer("ro.lexera.issuer")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(rsaKeyConfigProvider.rsaPrivateKey())
                .compact();
    }

    /**
     * Generates a short-lived proof of a credential's current validity status.
     * <p>
     * This is used by the Status Registry to provide signed assertions that a
     * credential has not been revoked, without needing to re-issue the entire document.
     * </p>
     *
     * @param credentialId The JTI of the original credential being verified.
     * @param claims       The status data (e.g., {@code status: "VALID"}).
     * @return A Base64URL encoded String representing the signed status proof.
     */
    public String signStatusProof(String credentialId, Map<String, Object> claims) {
        return Jwts.builder()
                .json(serializer)
                .subject(credentialId)
                .claims(claims)
                .issuer("ro.lexera.status-registry")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(12, ChronoUnit.HOURS)))
                .signWith(rsaKeyConfigProvider.rsaPrivateKey())
                .compact();
    }

}
