package com.lembre.dto;

import java.time.LocalDateTime;

public record ConclusaoResponse(
        Long atividadeId, String atividadeTitulo, Long usuarioId, String usuarioNome,
        int pontosGanhos, int totalPontosUsuario, LocalDateTime dataConclusao) {
}
