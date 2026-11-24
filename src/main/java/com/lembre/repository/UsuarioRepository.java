package com.lembre.repository;

import com.lembre.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByGrupoId(Long grupoId);
    long countByGrupoId(Long grupoId);

    /** Consulta personalizada: membros de um grupo. */
    List<Usuario> findByGrupoIdOrderByNomeAsc(Long grupoId);
}
