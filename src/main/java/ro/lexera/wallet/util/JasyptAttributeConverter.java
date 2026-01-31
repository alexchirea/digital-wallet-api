package ro.lexera.wallet.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Converter
@Component
public class JasyptAttributeConverter implements AttributeConverter<String, String> {

    private final StandardPBEStringEncryptor encryptor;

    public JasyptAttributeConverter(Environment environment) {
        this.encryptor = new StandardPBEStringEncryptor();
        String password = environment.getProperty("jasypt.encryptor.password");
        if (password == null) {
            throw new IllegalStateException("Encryption password not found in environment!");
        }
        this.encryptor.setPassword(password);
        this.encryptor.setAlgorithm("PBEWithMD5AndDES");
    }

    @Override
    public String convertToDatabaseColumn(String entityValue) {
        if (entityValue == null) return null;
        return encryptor.encrypt(entityValue);
    }

    @Override
    public String convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return encryptor.decrypt(dbValue);
    }
}
