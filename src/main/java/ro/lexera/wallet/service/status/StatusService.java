package ro.lexera.wallet.service.status;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ro.lexera.wallet.exception.DigitalWalletException;
import ro.lexera.wallet.model.entity.StatusRegistryEntity;
import ro.lexera.wallet.service.crypto.SigningService;
import ro.lexera.wallet.repository.StatusRegistryRepository;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRegistryRepository repository;
    private final SigningService signingService;

    public String getSignedStatusProof(UUID credentialId) {
        var statusEntry = repository.findById(credentialId)
                .orElseThrow(() -> new DigitalWalletException(
                        "Credential ID " + credentialId + " does not exist in our registry.",
                        "CREDENTIAL_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        if (statusEntry.isRevoked()) {
            throw new DigitalWalletException(
                    "This credential has been revoked and is no longer valid.",
                    "ERR_CREDENTIAL_REVOKED",
                    HttpStatus.FORBIDDEN
            );
        }

        Map<String, Object> claims = Map.of(
                "status", "VALID",
                "verifiedAt", Instant.now()
        );

        return signingService.signStatusProof(credentialId.toString(), claims);
    }

    @Transactional
    public void initializeStatus(UUID credentialId) {
        StatusRegistryEntity status = StatusRegistryEntity.builder()
                .credentialId(credentialId)
                .isRevoked(false)
                .updatedAt(Instant.now())
                .build();

        repository.save(status);
    }

    @Transactional
    public void revokeCredential(UUID credentialId, String reason) {
        StatusRegistryEntity status = repository.findById(credentialId)
                .orElseGet(() -> StatusRegistryEntity.builder()
                        .credentialId(credentialId)
                        .build());

        status.setRevoked(true);
        status.setRevocationReason(reason);
        status.setUpdatedAt(Instant.now());

        repository.save(status);
    }

}
