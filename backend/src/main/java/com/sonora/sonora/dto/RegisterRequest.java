package com.sonora.sonora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Registration request payload. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** Minimum password length for registration. */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /** User email used for account creation. */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    /** User plaintext password used during account creation. */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = MIN_PASSWORD_LENGTH,
            message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
}
