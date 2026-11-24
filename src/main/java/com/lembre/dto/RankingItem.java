package com.lembre.dto;

/** Uma linha do ranking: quanto cada membro pontuou no período. */
public record RankingItem(Long usuarioId, String nome, Long pontos, Long atividadesConcluidas) {
}
