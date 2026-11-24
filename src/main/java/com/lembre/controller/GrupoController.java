package com.lembre.controller;

import com.lembre.dto.*;
import com.lembre.entity.StatusAtividade;
import com.lembre.service.AtividadeService;
import com.lembre.service.GrupoService;
import com.lembre.service.RankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;
    private final RankingService rankingService;
    private final AtividadeService atividadeService;

    @GetMapping
    public List<GrupoResponse> listar() {
        return grupoService.listar();
    }

    @GetMapping("/{id}")
    public GrupoResponse buscar(@PathVariable Long id) {
        return grupoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GrupoResponse criar(@Valid @RequestBody GrupoRequest req) {
        return grupoService.criar(req);
    }

    @PutMapping("/{id}")
    public GrupoResponse atualizar(@PathVariable Long id, @Valid @RequestBody GrupoRequest req) {
        return grupoService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        grupoService.excluir(id);
    }

    /** Ranking da semana (segunda a domingo). Parâmetro "semana" opcional: qualquer data dentro da semana. */
    @GetMapping("/{id}/ranking-semanal")
    public RankingSemanalResponse rankingSemanal(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana) {
        return rankingService.rankingSemanal(id, semana);
    }

    /** Atividades pendentes com prazo vencido. */
    @GetMapping("/{id}/atividades-atrasadas")
    public List<AtividadeResponse> atrasadas(@PathVariable Long id) {
        return atividadeService.listarAtrasadas(id);
    }
}
