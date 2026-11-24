package com.lembre.dto;

import java.time.LocalDate;
import java.util.List;

public record RankingSemanalResponse(
        Long grupoId, LocalDate inicioSemana, LocalDate fimSemana,
        String destaque, List<RankingItem> ranking) {
}
