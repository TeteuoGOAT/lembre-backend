package com.lembre.dto;

import com.lembre.entity.Atividade;
import com.lembre.entity.StatusAtividade;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AtividadeResponse(
        Long id, String titulo, String descricao, int pontos, StatusAtividade status,
        LocalDate dataLimite, LocalDateTime dataCriacao, LocalDateTime dataConclusao,
        Long grupoId, Long responsavelId, String responsavelNome) {

    public static AtividadeResponse de(Atividade a) {
        return new AtividadeResponse(a.getId(), a.getTitulo(), a.getDescricao(), a.getPontos(), a.getStatus(),
                a.getDataLimite(), a.getDataCriacao(), a.getDataConclusao(), a.getGrupo().getId(),
                a.getResponsavel() != null ? a.getResponsavel().getId() : null,
                a.getResponsavel() != null ? a.getResponsavel().getNome() : null);
    }
}
