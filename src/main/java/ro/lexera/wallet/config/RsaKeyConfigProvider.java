package ro.lexera.wallet.config;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

public interface RsaKeyConfigProvider {
    KeyPair rsaKeyPair() throws NoSuchAlgorithmException;
    RSAPrivateKey rsaPrivateKey();
    RSAPublicKey rsaPublicKey();
    Map<String, Object> getPublicJWK();
}
