package com.lembre.dto;

import com.lembre.entity.Recompensa;
import com.lembre.entity.StatusRecompensa;

import java.time.LocalDate;

public record RecompensaResponse(
        Long id, String titulo, String descricao, StatusRecompensa status, Long grupoId,
        Long vencedorId, String vencedorNome, LocalDate dataConcessao, LocalDate semanaReferencia) {

    public static RecompensaResponse de(Recompensa r) {
        return new RecompensaResponse(r.getId(), r.getTitulo(), r.getDescricao(), r.getStatus(),
                r.getGrupo().getId(),
                r.getVencedor() != null ? r.getVencedor().getId() : null,
                r.getVencedor() != null ? r.getVencedor().getNome() : null,
                r.getDataConcessao(), r.getSemanaReferencia());
    }
}
