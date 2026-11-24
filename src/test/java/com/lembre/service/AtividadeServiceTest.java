package com.lembre.service;

import com.lembre.dto.ConclusaoResponse;
import com.lembre.entity.*;
import com.lembre.exception.RegraNegocioException;
import com.lembre.repository.AtividadeRepository;
import com.lembre.repository.ConclusaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtividadeServiceTest {

    @Mock AtividadeRepository atividadeRepository;
    @Mock ConclusaoRepository conclusaoRepository;
    @Mock GrupoService grupoService;
    @Mock UsuarioService usuarioService;
    @InjectMocks AtividadeService service;

    Grupo grupo;
    Usuario usuario;
    Atividade atividade;

    @BeforeEach
    void setUp() {
        grupo = new Grupo();
        grupo.setId(1L);
        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Ana");
        usuario.setGrupo(grupo);
        atividade = new Atividade();
        atividade.setId(3L);
        atividade.setTitulo("Lavar a louça");
        atividade.setPontos(10);
        atividade.setGrupo(grupo);
    }

    @Test
    void concluirDeveSomarPontosEMudarStatus() {
        when(atividadeRepository.findById(3L)).thenReturn(Optional.of(atividade));
        when(usuarioService.buscarEntidade(2L)).thenReturn(usuario);
        when(conclusaoRepository.save(any(RegistroConclusao.class))).thenAnswer(i -> i.getArgument(0));

        ConclusaoResponse resp = service.concluir(3L, 2L);

        assertEquals(10, usuario.getPontos());
        assertEquals(10, resp.pontosGanhos());
        assertEquals(StatusAtividade.CONCLUIDA, atividade.getStatus());
    }

    @Test
    void concluirAtividadeJaConcluidaDeveLancarExcecao() {
        atividade.setStatus(StatusAtividade.CONCLUIDA);
        when(atividadeRepository.findById(3L)).thenReturn(Optional.of(atividade));

        assertThrows(RegraNegocioException.class, () -> service.concluir(3L, 2L));
    }

    @Test
    void concluirPorUsuarioDeOutroGrupoDeveLancarExcecao() {
        Grupo outro = new Grupo();
        outro.setId(99L);
        usuario.setGrupo(outro);
        when(atividadeRepository.findById(3L)).thenReturn(Optional.of(atividade));
        when(usuarioService.buscarEntidade(2L)).thenReturn(usuario);

        assertThrows(RegraNegocioException.class, () -> service.concluir(3L, 2L));
    }
}
