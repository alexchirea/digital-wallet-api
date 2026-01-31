package ro.lexera.wallet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.lexera.wallet.util.JasyptAttributeConverter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String rootIdentityHash;

    @Column(nullable = false)
    @Convert(converter = JasyptAttributeConverter.class)
    private String firstName;

    @Column(nullable = false)
    @Convert(converter = JasyptAttributeConverter.class)
    private String lastName;

    @Column(nullable = false)
    @Convert(converter = JasyptAttributeConverter.class)
    private String nationalId;

    @Column(nullable = false)
    private String email;

    @Column(length = 2048)
    private String devicePublicKey;

}
