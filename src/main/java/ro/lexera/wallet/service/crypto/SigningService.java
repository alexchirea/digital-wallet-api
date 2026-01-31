package ro.lexera.wallet.service.crypto;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.lexera.wallet.config.RsaKeyConfigProvider;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SigningService {

    private final RsaKeyConfigProvider rsaKeyConfigProvider;

    public String signCredential(String subjectId, String docType, Map<String, Object> claims) {
        return Jwts.builder()
                .subject(subjectId)
                .claim("type", docType)
                .claims(claims)
                .issuer("ro.lexera.issuer")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(rsaKeyConfigProvider.rsaPrivateKey())
                .compact();
    }

}
