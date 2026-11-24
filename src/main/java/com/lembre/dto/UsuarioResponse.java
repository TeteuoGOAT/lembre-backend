package com.lembre.dto;

import com.lembre.entity.PapelNoGrupo;
import com.lembre.entity.Usuario;

public record UsuarioResponse(Long id, String nome, String email, PapelNoGrupo papel, int pontos, Long grupoId) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPapel(), u.getPontos(),
                u.getGrupo().getId());
    }
}
