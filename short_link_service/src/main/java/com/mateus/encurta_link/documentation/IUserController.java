package com.mateus.encurta_link.documentation;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoResponse;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
public interface IUserController {

    @Operation(
        summary = "Obter links do usuário",
        description = "Retorna todos os links encurtados criados pelo usuário autenticado",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de links encontrada",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ShortLinkDtoResponse.class))
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado - Token inválido ou ausente"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado"
            )
        }
    )
    @GetMapping("/links")
    ResponseEntity<List<ShortLinkDtoResponse>> getUserLinks(
        @Parameter(
            description = "Token de autenticação no formato 'Bearer {token}'",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @RequestHeader(name = "Authorization") String bearerToken
    ) throws UserNotFoundException;
}