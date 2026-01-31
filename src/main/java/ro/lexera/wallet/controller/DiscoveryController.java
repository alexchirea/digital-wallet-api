package ro.lexera.wallet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.lexera.wallet.config.RsaKeyConfigProvider;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DiscoveryController {

    private final RsaKeyConfigProvider rsaKeyConfigProvider;

    /**
     * Standard endpoint for Public Key Discovery.
     * The UI will call this to cache the key for offline verification.
     */
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        return rsaKeyConfigProvider.getPublicJWK();
    }

}
