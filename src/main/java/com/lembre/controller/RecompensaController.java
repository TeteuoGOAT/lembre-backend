package com.lembre.controller;

import com.lembre.dto.RecompensaRequest;
import com.lembre.dto.RecompensaResponse;
import com.lembre.service.RecompensaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/recompensas")
@RequiredArgsConstructor
public class RecompensaController {

    private final RecompensaService recompensaService;

    @GetMapping
    public List<RecompensaResponse> listar(@RequestParam(required = false) Long grupoId) {
        return recompensaService.listar(grupoId);
    }

    @GetMapping("/{id}")
    public RecompensaResponse buscar(@PathVariable Long id) {
        return recompensaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecompensaResponse criar(@Valid @RequestBody RecompensaRequest req) {
        return recompensaService.criar(req);
    }

    @PutMapping("/{id}")
    public RecompensaResponse atualizar(@PathVariable Long id, @Valid @RequestBody RecompensaRequest req) {
        return recompensaService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        recompensaService.excluir(id);
    }

    /** ADMIN entrega a recompensa a quem mais pontuou na semana. */
    @PostMapping("/{id}/conceder")
    public RecompensaResponse conceder(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana) {
        return recompensaService.concederAoDestaque(id, semana);
    }
}
