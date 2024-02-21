package com.hostfully.technicalchallenge.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "Hostfully technical challenge API",
        description = "Booking system API",
        version = "v1"
    )
)
@Configuration
public class OpenApiConfig {

}
