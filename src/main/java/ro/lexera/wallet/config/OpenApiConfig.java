package ro.lexera.wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lexeraWalletOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lexera Digital Wallet API")
                        .description("Secure Digital Identity & Verifiable Credentials Backend")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Lexera")
                                .email("contact@lexera.ro"))
                        .license(new License()
                                .name("Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License")
                                .url("https://creativecommons.org/licenses/by-nc-sa/4.0/")));
    }
}
