package com.assino.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger com autenticação Bearer (JWT).
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Assinô API", version = "1.0",
        description = "Gerenciador de assinaturas para controle de gastos mensais"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
