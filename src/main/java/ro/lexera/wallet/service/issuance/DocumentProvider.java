package ro.lexera.wallet.service.issuance;

import java.util.Map;

public interface DocumentProvider {
    String supportsType(); // e.g., "UNIVERSITY_DIPLOMA"
    Map<String, Object> fetchClaims(String rootIdentityHash);
}
