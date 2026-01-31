package ro.lexera.wallet.service.issuance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.lexera.wallet.exception.DigitalWalletException;
import ro.lexera.wallet.service.crypto.SigningService;
import ro.lexera.wallet.service.status.StatusService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssuanceServiceTest {

    private IssuanceService issuanceService;

    @Mock
    private StatusService statusService;

    @Mock
    private SigningService signingService;

    @Mock
    private DocumentProvider mockProvider;

    @BeforeEach
    void setUp() {
        // We inject the list containing our mocked provider
        issuanceService = new IssuanceService(List.of(mockProvider), signingService, statusService);
    }

    @Test
    void issueCredential_Success_ShouldFlowThroughAllSteps() {
        // Arrange
        String hash = "user-hash-123";
        String type = "UNIVERSITY_DIPLOMA";
        Map<String, Object> mockClaims = Map.of("degree", "Bachelor of Science");
        String expectedJwt = "header.payload.signature";

        when(mockProvider.supportsType()).thenReturn(type);
        when(mockProvider.fetchClaims(hash)).thenReturn(mockClaims);
        when(signingService.signCredential(anyString(), anyString(), anyMap(), any(UUID.class)))
                .thenReturn(expectedJwt);

        // Act
        String result = issuanceService.issueCredential(hash, type);

        // Assert
        assertThat(result).isEqualTo(expectedJwt);

        // 1. Verify Strategy Pattern worked
        verify(mockProvider).fetchClaims(hash);

        // 2. Verify Status Registry was initialized with a UUID
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(statusService).initializeStatus(uuidCaptor.capture());
        UUID generatedId = uuidCaptor.getValue();
        assertThat(generatedId).isNotNull();

        // 3. Verify Signing was called with that same UUID (JTI)
        verify(signingService).signCredential(eq(hash), eq(type), eq(mockClaims), eq(generatedId));
    }

    @Test
    void issueCredential_UnsupportedType_ShouldThrowLexeraException() {
        // Arrange
        when(mockProvider.supportsType()).thenReturn("IDENTITY_CARD");

        // Act & Assert
        assertThatThrownBy(() -> issuanceService.issueCredential("hash", "NON_EXISTENT_TYPE"))
                .isInstanceOf(DigitalWalletException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ERR_UNSUPPORTED_TYPE");

        // Verify no signing or persistence happened
        verifyNoInteractions(statusService);
        verifyNoInteractions(signingService);
    }

    @Test
    void issueCredential_ProviderFails_ShouldPropagateException() {
        // Arrange
        String type = "UNIVERSITY_DIPLOMA";
        when(mockProvider.supportsType()).thenReturn(type);
        when(mockProvider.fetchClaims(anyString())).thenThrow(new RuntimeException("DB Down"));

        // Act & Assert
        assertThatThrownBy(() -> issuanceService.issueCredential("hash", type))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB Down");

        // Verify status was NOT initialized if data fetch failed
        verifyNoInteractions(statusService);
    }
}
