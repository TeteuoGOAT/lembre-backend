package com.lembre.dto;

import com.lembre.entity.Grupo;

public record GrupoResponse(Long id, String nome, String descricao, String codigoConvite, long totalMembros) {
    public static GrupoResponse de(Grupo g, long totalMembros) {
        return new GrupoResponse(g.getId(), g.getNome(), g.getDescricao(), g.getCodigoConvite(), totalMembros);
    }
}
