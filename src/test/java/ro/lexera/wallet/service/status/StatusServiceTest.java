package ro.lexera.wallet.service.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ro.lexera.wallet.exception.DigitalWalletException;
import ro.lexera.wallet.model.entity.StatusRegistryEntity;
import ro.lexera.wallet.repository.StatusRegistryRepository;
import ro.lexera.wallet.service.crypto.SigningService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRegistryRepository repository;

    @InjectMocks
    private StatusService statusService;

    @Mock
    private SigningService signingService;

    @Test
    void shouldReturnSignedProofWhenValid() {
        // Arrange
        UUID id = UUID.randomUUID();
        StatusRegistryEntity validEntity = StatusRegistryEntity.builder()
                .credentialId(id)
                .isRevoked(false)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(validEntity));
        when(signingService.signStatusProof(anyString(), anyMap())).thenReturn("mocked-jwt-proof");

        // Act
        String result = statusService.getSignedStatusProof(id);

        // Assert
        assertThat(result).isEqualTo("mocked-jwt-proof");
        verify(signingService).signStatusProof(eq(id.toString()), anyMap());
    }

    @Test
    void shouldInitializeStatusAsValid() {
        // Arrange
        UUID credentialId = UUID.randomUUID();
        ArgumentCaptor<StatusRegistryEntity> captor = ArgumentCaptor.forClass(StatusRegistryEntity.class);

        // Act
        statusService.initializeStatus(credentialId);

        // Assert
        verify(repository).save(captor.capture());
        StatusRegistryEntity saved = captor.getValue();

        assertThat(saved.getCredentialId()).isEqualTo(credentialId);
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRevokeExistingCredential() {
        // Arrange
        UUID id = UUID.randomUUID();
        StatusRegistryEntity existing = StatusRegistryEntity.builder()
                .credentialId(id)
                .isRevoked(false)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));

        // Act
        statusService.revokeCredential(id, "Security Breach");

        // Assert
        verify(repository).save(existing);
        assertThat(existing.isRevoked()).isTrue();
        assertThat(existing.getRevocationReason()).isEqualTo("Security Breach");
    }

    @Test
    void shouldThrowExceptionWhenCredentialIsRevoked() {
        // Arrange
        UUID id = UUID.randomUUID();
        StatusRegistryEntity revokedEntity = StatusRegistryEntity.builder()
                .credentialId(id)
                .isRevoked(true)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(revokedEntity));

        // Act & Assert
        DigitalWalletException ex = assertThrows(DigitalWalletException.class,
                () -> statusService.getSignedStatusProof(id));

        assertThat(ex.getErrorCode()).isEqualTo("ERR_CREDENTIAL_REVOKED");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
