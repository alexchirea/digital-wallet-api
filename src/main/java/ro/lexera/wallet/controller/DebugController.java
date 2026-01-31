package ro.lexera.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ro.lexera.wallet.model.entity.UserEntity;
import ro.lexera.wallet.repository.UserRepository;
import ro.lexera.wallet.service.identity.IdentityService;
import ro.lexera.wallet.service.issuance.IssuanceService;
import ro.lexera.wallet.service.status.StatusService;

import java.util.UUID;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final UserRepository userRepository;
    private final IdentityService identityService;
    private final IssuanceService issuanceService;
    private final StatusService statusService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user record and generates a deterministic Root Identity Hash based on their PII.",
            tags = {"Users"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error during hashing or persistence")
    })
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse createUser(@RequestBody CreateUserRequest request) {
        UserEntity user = UserEntity.builder()
                .rootIdentityHash(identityService.createRootHash(request.firstName, request.lastName, request.nationalId))
                .firstName(request.firstName)
                .lastName(request.lastName)
                .email(request.email)
                .nationalId(request.nationalId)
                .build();
        var savedUser = userRepository.save(user);
        return new CreateUserResponse(savedUser.getId().toString(), savedUser.getRootIdentityHash());
    }

    @Operation(
            summary = "Issue a Verifiable Credential",
            description = "Retrieves identity claims, initializes a revocation status, and returns a cryptographically signed JWT.",
            tags = {"Verifiable Credentials"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credential successfully issued (Returns JWT String)"),
            @ApiResponse(responseCode = "400", description = "Unsupported credentials type or invalid identity hash"),
            @ApiResponse(responseCode = "404", description = "Identity hash not found in registry"),
            @ApiResponse(responseCode = "500", description = "Cryptographic signing failure")
    })
    @PostMapping("/credentials/issue")
    public String issueCredential(
            @Parameter(description = "The type of credential to issue", example = "UNIVERSITY_DIPLOMA")
            @RequestParam String type,

            @Parameter(description = "The anonymized Root Identity Hash of the user", example = "a591a6d40b...")
            @RequestParam String rootIdentityHash) {

        return issuanceService.issueCredential(rootIdentityHash, type);
    }

    @Operation(
            summary = "Revoke a specific credential",
            description = "Marks a credential as invalid in the Status Registry. This action is irreversible and requires a reason for the audit log.",
            tags = {"Verifiable Credentials"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Credential successfully revoked"),
            @ApiResponse(responseCode = "404", description = "Credential ID not found"),
            @ApiResponse(responseCode = "400", description = "Invalid UUID format or missing reason"),
            @ApiResponse(responseCode = "410", description = "Credential was already revoked")
    })
    @PostMapping("/credentials/{credentialId}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeCredential(
            @Parameter(description = "The unique JTI (UUID) of the credential", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID credentialId,

            @Parameter(description = "Reason for revocation (e.g., 'Lost device', 'Security breach')", example = "User reported stolen phone")
            @RequestParam String reason) {

        statusService.revokeCredential(credentialId, reason);
    }

    @Operation(
            summary = "Retrieve a signed status proof",
            description = "Generates a short-lived, cryptographically signed assertion confirming whether a specific credential is currently VALID or REVOKED.",
            tags = {"Verifiable Credentials"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved signed status (Returns JWT String)"),
            @ApiResponse(responseCode = "404", description = "Credential ID not found in the status registry"),
            @ApiResponse(responseCode = "500", description = "Error generating cryptographic signature")
    })
    @GetMapping("/credentials/{credentialId}/status")
    public String credentialStatus(
            @Parameter(description = "The JTI (unique ID) of the credential to check", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID credentialId) {

        return statusService.getSignedStatusProof(credentialId);
    }

    public record CreateUserRequest(
            @Schema(description = "User's legal first name", example = "John")
            String firstName,
            @Schema(description = "User's legal last name", example = "Doe")
            String lastName,
            @Schema(description = "Contact email address", example = "john.doe@lexera.ro")
            String email,
            @Schema(description = "Unique government-issued ID (used for root hash generation)", example = "ABC9876")
            String nationalId) {
    }

    public record CreateUserResponse(
            @Schema(description = "Internal database UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            String id,
            @Schema(description = "The generated SHA-256 Root Identity Hash", example = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e")
            String hash) {
    }

}
