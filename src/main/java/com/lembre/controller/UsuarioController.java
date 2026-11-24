package com.lembre.controller;

import com.lembre.dto.RecompensaResponse;
import com.lembre.dto.UsuarioRequest;
import com.lembre.dto.UsuarioResponse;
import com.lembre.service.RecompensaService;
import com.lembre.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RecompensaService recompensaService;

    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) Long grupoId) {
        return usuarioService.listar(grupoId);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest req) {
        return usuarioService.criar(req);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        return usuarioService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }

    /** Recompensas que o usuário já conquistou. */
    @GetMapping("/{id}/recompensas")
    public List<RecompensaResponse> recompensas(@PathVariable Long id) {
        return recompensaService.listarDoUsuario(id);
    }
}
