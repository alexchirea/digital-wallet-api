package ro.lexera.wallet.service.identity;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.lexera.wallet.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class IdentityService {

    @Value("${wallet.app.secret.salt}")
    private String secretSalt;

    private final UserRepository userRepository;

    public String createRootHash(String firstName, String lastName, String nationalId) {
        // Normalize and concatenate
        String rawData = (firstName + lastName + nationalId).toLowerCase().replaceAll("\\s", "");

        return DigestUtils.sha256Hex(rawData + secretSalt);
    }

    public boolean isUserAlreadyRegistered(String hash) {
        return userRepository.findByRootIdentityHash(hash).isPresent();
    }

}
