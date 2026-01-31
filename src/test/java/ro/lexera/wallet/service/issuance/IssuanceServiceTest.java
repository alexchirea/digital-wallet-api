package ro.lexera.wallet.service.issuance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ro.lexera.wallet.service.identity.IdentityService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IssuanceServiceTest {

    private IdentityService identityService;

    @BeforeEach
    void setUp() {
        identityService = new IdentityService(null); // Repo not needed for hashing test
        ReflectionTestUtils.setField(identityService, "secretSalt", "secret-salt");
    }

    @Test
    void shouldGenerateConsistentHash() {
        String hash1 = identityService.createRootHash("John", "Doe", "ID123");
        String hash2 = identityService.createRootHash("john", "doe ", "ID123");

        // Should be equal regardless of casing or trailing spaces
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void shouldChangeHashWhenSaltChanges() {
        String hash1 = identityService.createRootHash("John", "Doe", "ID123");

        ReflectionTestUtils.setField(identityService, "secretSalt", "different-salt");
        String hash2 = identityService.createRootHash("John", "Doe", "ID123");

        assertThat(hash1).isNotEqualTo(hash2);
    }

}