package com.lembre.repository;

import com.lembre.dto.RankingItem;
import com.lembre.entity.RegistroConclusao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConclusaoRepository extends JpaRepository<RegistroConclusao, Long> {
    boolean existsByUsuarioId(Long usuarioId);
    boolean existsByAtividadeId(Long atividadeId);

    /** Consulta personalizada (JPQL): ranking de pontos do grupo em um período. */
    @Query("""
            select new com.lembre.dto.RankingItem(u.id, u.nome, sum(c.pontosGanhos), count(c))
            from RegistroConclusao c
            join c.usuario u
            where u.grupo.id = :grupoId
              and c.dataConclusao >= :inicio
              and c.dataConclusao < :fim
            group by u.id, u.nome
            order by sum(c.pontosGanhos) desc, count(c) desc, u.nome asc
            """)
    List<RankingItem> rankingDoPeriodo(@Param("grupoId") Long grupoId,
                                       @Param("inicio") LocalDateTime inicio,
                                       @Param("fim") LocalDateTime fim);
}
