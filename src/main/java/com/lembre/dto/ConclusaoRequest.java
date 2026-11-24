package com.lembre.dto;

import jakarta.validation.constraints.NotNull;

public record ConclusaoRequest(
        @NotNull(message = "O usuário que concluiu a atividade é obrigatório")
        Long usuarioId) {
}
