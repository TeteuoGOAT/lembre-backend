package com.lembre.repository;

import com.lembre.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    boolean existsByCodigoConvite(String codigoConvite);
}
