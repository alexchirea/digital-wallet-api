package ro.lexera.wallet.service.crypto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.lexera.wallet.config.RsaKeyConfigProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class SigningServiceTest {

    @Mock
    private RsaKeyConfigProvider rsaKeyConfigProvider;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateValidSignedJwt() throws Exception {
        // Arrange: Generate a temporary keypair for the test
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        when(rsaKeyConfigProvider.rsaPublicKey()).thenReturn((RSAPublicKey) pair.getPublic());
        when(rsaKeyConfigProvider.rsaPrivateKey()).thenReturn((RSAPrivateKey) pair.getPrivate());

        SigningService signingService = new SigningService(rsaKeyConfigProvider, new JacksonSerializer<>());
        Map<String, Object> claims = Map.of("gpa", "4.0");

        var documentId = UUID.randomUUID();

        // Act
        String jwt = signingService.signCredential("user-123", "DIPLOMA", claims, documentId);

        // Assert: Use the Public Key to verify the signature
        Claims decoded = Jwts.parser()
                .verifyWith(pair.getPublic())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        assertThat(decoded.getSubject()).isEqualTo("user-123");
        assertThat(decoded.getId()).isEqualTo(documentId.toString());
        assertThat(decoded.get("type")).isEqualTo("DIPLOMA");
        assertThat(decoded.get("gpa")).isEqualTo("4.0");
    }

}
