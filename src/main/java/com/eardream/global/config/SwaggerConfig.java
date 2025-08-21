package com.eardream.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI eardreamOpenAPI() {

		String securitySchemeName = "bearerAuth";
		return new OpenAPI()
				.info(new Info()
						.title("EarDream API")
						.version("v1")
						.description("EarDream 백엔드 API 문서")
						.license(new License().name("Proprietary").url("https://eardream.com")))
				.components(new Components()
						.addSecuritySchemes(securitySchemeName, new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
	}
}




