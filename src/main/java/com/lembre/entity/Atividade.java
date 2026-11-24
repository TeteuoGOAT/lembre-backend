package com.lembre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Tarefa doméstica a ser realizada por alguém do grupo. */
@Entity
@Table(name = "atividades")
@Getter
@Setter
@NoArgsConstructor
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    /** Pontos que o usuário ganha ao concluir a atividade. */
    @Column(nullable = false)
    private int pontos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusAtividade status = StatusAtividade.PENDENTE;

    @Column(name = "data_limite")
    private LocalDate dataLimite;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    /** Quem ficou responsável (opcional: a atividade pode estar "livre"). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;
}
