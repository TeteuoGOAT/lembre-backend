package com.lembre.service;

import com.lembre.dto.RankingItem;
import com.lembre.dto.RankingSemanalResponse;
import com.lembre.repository.ConclusaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final ConclusaoRepository conclusaoRepository;
    private final GrupoService grupoService;

    /** Ranking da semana (segunda a domingo) que contém a data informada (padrão: hoje). */
    public RankingSemanalResponse rankingSemanal(Long grupoId, LocalDate referencia) {
        grupoService.buscarEntidade(grupoId);
        LocalDate ref = (referencia != null) ? referencia : LocalDate.now();
        LocalDate segunda = ref.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate proximaSegunda = segunda.plusDays(7);

        List<RankingItem> ranking = conclusaoRepository.rankingDoPeriodo(
                grupoId, segunda.atStartOfDay(), proximaSegunda.atStartOfDay());

        String destaque = ranking.isEmpty() ? null : ranking.get(0).nome();
        return new RankingSemanalResponse(grupoId, segunda, segunda.plusDays(6), destaque, ranking);
    }
}
