package com.lembre.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AtividadeRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        String titulo,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao,

        @NotNull(message = "Os pontos são obrigatórios")
        @Min(value = 1, message = "A atividade deve valer no mínimo 1 ponto")
        @Max(value = 100, message = "A atividade deve valer no máximo 100 pontos")
        Integer pontos,

        LocalDate dataLimite,

        @NotNull(message = "O grupo é obrigatório")
        Long grupoId,

        Long responsavelId) {
}
