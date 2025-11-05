package com.mateus.encurta_link.documentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;

import com.mateus.encurta_link.dto.Auth.AuthenticationResponse;
import com.mateus.encurta_link.dto.Auth.UserRegisterRequest;
import com.mateus.encurta_link.exceptions.InvalidCredentialsException;
import com.mateus.encurta_link.exceptions.UserAlreadyExistException;
import com.mateus.encurta_link.model.User;

public interface IAuthenticationController {

    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria uma nova conta de usuário e retorna um token JWT",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Registro bem-sucedido",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Usuário já existe",
                content = @Content
            )
        }
    )
    @PostMapping("/sign")
    ResponseEntity<AuthenticationResponse> registerUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para registro do usuário",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRegisterRequest.class)
            )
        )
        @RequestBody UserRegisterRequest request
    ) throws UserAlreadyExistException;

    @Operation(
        summary = "Autenticar usuário",
        description = "Realiza login e retorna um token JWT",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Autenticação bem-sucedida",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Credenciais inválidas",
                content = @Content
            )
        }
    )
    @PostMapping("/login")
    ResponseEntity<AuthenticationResponse> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciais do usuário",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        )
        @RequestBody User request
    ) throws InvalidCredentialsException;
}