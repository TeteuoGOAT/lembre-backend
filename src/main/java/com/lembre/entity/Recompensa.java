package com.lembre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Prêmio criado pelo admin e entregue a quem mais se destacou na semana. */
@Entity
@Table(name = "recompensas")
@Getter
@Setter
@NoArgsConstructor
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 300)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusRecompensa status = StatusRecompensa.DISPONIVEL;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vencedor_id")
    private Usuario vencedor;

    @Column(name = "data_concessao")
    private LocalDate dataConcessao;

    /** Segunda-feira da semana que originou a recompensa. */
    @Column(name = "semana_referencia")
    private LocalDate semanaReferencia;
}
