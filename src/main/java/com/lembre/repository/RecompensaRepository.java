package com.lembre.repository;

import com.lembre.entity.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {
    boolean existsByGrupoId(Long grupoId);
    boolean existsByVencedorId(Long usuarioId);
    List<Recompensa> findByGrupoId(Long grupoId);

    /** Consulta personalizada: recompensas já conquistadas por um usuário. */
    List<Recompensa> findByVencedorIdOrderByDataConcessaoDesc(Long usuarioId);
}
