package ro.lexera.wallet.service.issuance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.lexera.wallet.service.crypto.SigningService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IssuanceService {

    private final List<DocumentProvider> providers;
    private final SigningService signingService;

    public String issueDocument(String rootIdentityHash, String type) {
        DocumentProvider provider = providers.stream()
                .filter(p -> p.supportsType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported document type: " + type));

        Map<String, Object> claims = provider.fetchClaims(rootIdentityHash);

        return signingService.signCredential(rootIdentityHash, type, claims);
    }

}
