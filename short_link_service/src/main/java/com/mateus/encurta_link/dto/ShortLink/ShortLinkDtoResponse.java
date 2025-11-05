package com.mateus.encurta_link.dto.ShortLink;

import com.mateus.encurta_link.model.ShortLink;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com informações completas sobre um link encurtado")
public record ShortLinkDtoResponse(
    @Schema(
        description = "ID único do link no banco de dados",
        example = "507f1f77bcf86cd799439011"
    )
    String id,
    
    @Schema(
        description = "URL original que foi encurtada",
        example = "https://exemplo.com/url-muito-longa",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String originalLink,
    
    @Schema(
        description = "Código encurtado do link",
        example = "abc123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String shortLink,
    
    @Schema(
        description = "Email do usuário que criou o link",
        example = "usuario@exemplo.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String userEmail
) {
    public static ShortLinkDtoResponse fromEntity(ShortLink link) {
        return new ShortLinkDtoResponse(
                link.getId(),
                link.getOriginalLink(),
                link.getShortLink(),
                link.getUser());
    }
}