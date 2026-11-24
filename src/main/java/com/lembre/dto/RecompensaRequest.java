package com.lembre.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecompensaRequest(
        @NotBlank(message = "O título da recompensa é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        String titulo,

        @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres")
        String descricao,

        @NotNull(message = "O grupo é obrigatório")
        Long grupoId) {
}
