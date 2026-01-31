package ro.lexera.wallet.service.issuance;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DiplomaProvider implements DocumentProvider {

    @Override
    public String supportsType() {
        return "UNIVERSITY_DIPLOMA";
    }

    @Override
    public Map<String, Object> fetchClaims(String rootIdentityHash) {
        // In a real app, we'd call a 3rd party API or University DB here.
        return Map.of(
                "degree", "Bachelor of Science",
                "major", "Computer Science",
                "gpa", "3.9",
                "university", "Lexera Tech Institute"
        );
    }

}
