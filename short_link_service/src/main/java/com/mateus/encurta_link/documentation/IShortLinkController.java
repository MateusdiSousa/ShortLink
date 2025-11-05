package com.mateus.encurta_link.documentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.*;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoRequest;
import com.mateus.encurta_link.exceptions.*;

@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public interface IShortLinkController {

    @Operation(
        summary = "Redireciona para URL original",
        description = "Redireciona automaticamente para a URL original correspondente ao código encurtado",
        responses = {
            @ApiResponse(
                responseCode = "302",
                description = "Redirecionamento bem-sucedido"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Código não encontrado",
                content = @Content
            )
        }
    )
    @GetMapping("{codigo}")
    RedirectView redirecionarLink(
        @Parameter(
            description = "Código do link encurtado",
            example = "abc123",
            required = true
        )
        @PathVariable(name = "codigo") String codigo
    ) throws ShortLinkNotFoundException;


    @Operation(
        summary = "Criar novo link encurtado",
        description = "Cria um novo código encurtado para a URL fornecida",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Link criado com sucesso",
                content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Conflito - Link já existe"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado - Token inválido"
            )
        }
    )
    @PostMapping("create")
    ResponseEntity<String> criarShortLink(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para criação do link",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ShortLinkDtoRequest.class)
            )
        )
        @RequestBody ShortLinkDtoRequest dto,
        
        @Parameter(
            description = "Token de autenticação no formato 'Bearer {token}'",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @RequestHeader(name = "Authorization") String bearerToken
    ) throws ShortLinkConflictException, UserNotFoundException;
}
