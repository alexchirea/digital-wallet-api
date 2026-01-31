package ro.lexera.wallet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.lexera.wallet.model.entity.UserEntity;
import ro.lexera.wallet.repository.UserRepository;
import ro.lexera.wallet.service.identity.IdentityService;
import ro.lexera.wallet.service.issuance.IssuanceService;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final UserRepository userRepository;
    private final IdentityService identityService;
    private final IssuanceService issuanceService;

    @PostMapping("/users")
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

    @PostMapping("/documents/issue")
    public String issueDocument(@RequestParam String type, @RequestParam String rootIdentityHash) {
        return issuanceService.issueDocument(rootIdentityHash, type);
    }

    public record CreateUserRequest(String firstName,
                                    String lastName,
                                    String email,
                                    String nationalId) {
    }

    public record CreateUserResponse(String id,
                                    String hash) {
    }

}
