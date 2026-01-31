package ro.lexera.wallet.config;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * Interface for providing RSA cryptographic key components used throughout the Lexera system.
 * <p>
 * This provider centralizes the management of the Issuer's asymmetric keys, which are
 * essential for signing Verifiable Credentials (VCs) and generating JSON Web Key Sets (JWKS).
 * </p>
 */
public interface RsaKeyConfigProvider {

    /**
     * Generates or retrieves an RSA KeyPair.
     * * @return The complete {@link java.security.KeyPair} containing both public and private keys.
     * @throws java.security.NoSuchAlgorithmException If the RSA algorithm is not available in the current environment.
     */
    KeyPair rsaKeyPair() throws NoSuchAlgorithmException;

    /**
     * Provides the RSA Private Key used for signing documents.
     * <p>
     * <b>Security Note:</b> This key must be handled with extreme care as it is used to
     * create non-repudiable digital signatures.
     * </p>
     * * @return The {@link java.security.interfaces.RSAPrivateKey} instance.
     */
    RSAPrivateKey rsaPrivateKey();

    /**
     * Provides the RSA Public Key used for signature verification.
     * * @return The {@link java.security.interfaces.RSAPublicKey} instance.
     */
    RSAPublicKey rsaPublicKey();

    /**
     * Exports the Public Key in a format compatible with JSON Web Key (JWK) specifications.
     * <p>
     * This is typically used to populate the {@code /well-known/jwks.json} endpoint,
     * allowing external Verifiers to dynamically fetch the keys needed to validate
     * credentials issued by this server.
     * </p>
     * * @return A {@link java.util.Map} representing the JWK structure (e.g., kty, n, e).
     */
    Map<String, Object> getPublicJWK();
}
