package com.lembre.repository;

import com.lembre.entity.Atividade;
import com.lembre.entity.StatusAtividade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    boolean existsByGrupoId(Long grupoId);
    boolean existsByResponsavelIdAndStatus(Long responsavelId, StatusAtividade status);

    /** Consulta personalizada: atividades do grupo filtradas por situação. */
    List<Atividade> findByGrupoIdAndStatusOrderByDataLimiteAsc(Long grupoId, StatusAtividade status);

    /** Consulta personalizada: atividades pendentes e já vencidas. */
    List<Atividade> findByGrupoIdAndStatusAndDataLimiteBefore(Long grupoId, StatusAtividade status, LocalDate data);

    List<Atividade> findByGrupoId(Long grupoId);
    List<Atividade> findByResponsavelId(Long responsavelId);
}
