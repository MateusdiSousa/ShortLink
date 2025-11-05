package com.mateus.encurta_link.dto.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para registro de novo usuário")
public record UserRegisterRequest(
    @Schema(
        description = "Email do usuário",
        example = "usuario@exemplo.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email invalid")
    String email,

    @Schema(
        description = "Senha do usuário (mínimo 6 caracteres)",
        example = "senhaSegura123",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 6
    )
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be 6 characters or more")
    String password
) {}