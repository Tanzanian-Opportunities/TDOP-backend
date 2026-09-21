package tdop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI tdopOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("TDOP API")
                .description("Tanzania Digital Opportunity Platform — REST API")
                .version("1.0.0")
                .contact(new Contact().name("TDOP Team").email("api@tdop.co.tz")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .schemaRequirement("Bearer", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token"));
    }
}
