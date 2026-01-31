package ro.lexera.wallet.config;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@Configuration
public class RsaKeyConfigMockProvider implements RsaKeyConfigProvider {

    private final KeyPair keyPair;

    public RsaKeyConfigMockProvider() throws NoSuchAlgorithmException {
        this.keyPair = rsaKeyPair();
    }

    @Override
    public KeyPair rsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    @Override
    public RSAPrivateKey rsaPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    @Override
    public RSAPublicKey rsaPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    @Override
    public Map<String, Object> getPublicJWK() {
        RSAKey jwk = new RSAKey.Builder(rsaPublicKey())
                .keyID("lexera-auth-key")
                .build();
        return jwk.toJSONObject();
    }

}
