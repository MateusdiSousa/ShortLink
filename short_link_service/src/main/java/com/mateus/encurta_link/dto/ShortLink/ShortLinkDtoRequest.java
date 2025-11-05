package com.mateus.encurta_link.dto.ShortLink;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de requisição para criação de um novo link encurtado")
public record ShortLinkDtoRequest(
    @Schema(
        description = "URL original a ser encurtada",
        example = "https://exemplo.com/url-muito-longa",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Link is mandatory")
    String link,
    
    @Schema(
        description = "Código customizado para o link encurtado (opcional)",
        example = "meulink",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    String shortLink
) {}