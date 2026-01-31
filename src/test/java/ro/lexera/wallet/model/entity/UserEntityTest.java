package ro.lexera.wallet.model.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ro.lexera.wallet.util.JasyptAttributeConverter;
import ro.lexera.wallet.repository.UserRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JasyptAttributeConverter.class)
class UserEntityTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldEncryptAndDecryptUserPii() {
        // Arrange
        UserEntity user = UserEntity.builder()
                .rootIdentityHash("test-hash")
                .firstName("John")
                .lastName("Smith")
                .email("john@lexera.ro")
                .nationalId("123456789")
                .build();

        // Act
        UserEntity saved = userRepository.save(user);
        UserEntity retrieved = userRepository.findById(saved.getId()).get();
        userRepository.flush();

        // Assert
        assertThat(retrieved.getFirstName()).isEqualTo("John");
        assertThat(retrieved.getLastName()).isEqualTo("Smith");
        assertThat(retrieved.getNationalId()).isEqualTo("123456789");
        assertThat(retrieved.getEmail()).isEqualTo("john@lexera.ro");

        // Encryption verification
        Map<String, Object> rawRow = jdbcTemplate.queryForMap(
                "SELECT first_name, last_name, national_id FROM users WHERE id = ?", saved.getId());
        assertThat(rawRow.get("first_name")).isNotEqualTo("John");
        assertThat(rawRow.get("last_name")).isNotEqualTo("Smith");
        assertThat(rawRow.get("national_id")).isNotEqualTo("123456789");
    }

}
