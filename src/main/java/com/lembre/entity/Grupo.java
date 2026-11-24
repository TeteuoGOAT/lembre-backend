package com.lembre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Grupo de pessoas que dividem atividades (ex.: uma família ou república). */
@Entity
@Table(name = "grupos")
@Getter
@Setter
@NoArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(name = "codigo_convite", nullable = false, unique = true, length = 12)
    private String codigoConvite;

    @OneToMany(mappedBy = "grupo")
    private List<Usuario> membros = new ArrayList<>();

    @OneToMany(mappedBy = "grupo")
    private List<Atividade> atividades = new ArrayList<>();

    @OneToMany(mappedBy = "grupo")
    private List<Recompensa> recompensas = new ArrayList<>();
}
