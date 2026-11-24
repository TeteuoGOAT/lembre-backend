package com.lembre.controller;

import com.lembre.dto.AtividadeRequest;
import com.lembre.dto.AtividadeResponse;
import com.lembre.dto.ConclusaoRequest;
import com.lembre.dto.ConclusaoResponse;
import com.lembre.entity.StatusAtividade;
import com.lembre.service.AtividadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atividades")
@RequiredArgsConstructor
public class AtividadeController {

    private final AtividadeService atividadeService;

    @GetMapping
    public List<AtividadeResponse> listar(@RequestParam(required = false) Long grupoId,
                                          @RequestParam(required = false) StatusAtividade status) {
        return atividadeService.listar(grupoId, status);
    }

    @GetMapping("/{id}")
    public AtividadeResponse buscar(@PathVariable Long id) {
        return atividadeService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtividadeResponse criar(@Valid @RequestBody AtividadeRequest req) {
        return atividadeService.criar(req);
    }

    @PutMapping("/{id}")
    public AtividadeResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtividadeRequest req) {
        return atividadeService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        atividadeService.excluir(id);
    }

    /** Marca a atividade como concluída e soma os pontos ao usuário. */
    @PostMapping("/{id}/concluir")
    public ConclusaoResponse concluir(@PathVariable Long id, @Valid @RequestBody ConclusaoRequest req) {
        return atividadeService.concluir(id, req.usuarioId());
    }
}
