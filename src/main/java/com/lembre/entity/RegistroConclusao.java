package com.lembre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Histórico de conclusões. É a base para o ranking semanal. */
@Entity
@Table(name = "registros_conclusao")
@Getter
@Setter
@NoArgsConstructor
public class RegistroConclusao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "pontos_ganhos", nullable = false)
    private int pontosGanhos;

    @Column(name = "data_conclusao", nullable = false)
    private LocalDateTime dataConclusao;
}
