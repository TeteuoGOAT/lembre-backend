package com.lembre.service;

import com.lembre.dto.RankingItem;
import com.lembre.dto.RankingSemanalResponse;
import com.lembre.dto.RecompensaRequest;
import com.lembre.dto.RecompensaResponse;
import com.lembre.entity.Grupo;
import com.lembre.entity.Recompensa;
import com.lembre.entity.StatusRecompensa;
import com.lembre.entity.Usuario;
import com.lembre.exception.RecursoNaoEncontradoException;
import com.lembre.exception.RegraNegocioException;
import com.lembre.repository.RecompensaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final GrupoService grupoService;
    private final UsuarioService usuarioService;
    private final RankingService rankingService;

    @Transactional(readOnly = true)
    public List<RecompensaResponse> listar(Long grupoId) {
        List<Recompensa> lista = (grupoId != null)
                ? recompensaRepository.findByGrupoId(grupoId)
                : recompensaRepository.findAll();
        return lista.stream().map(RecompensaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<RecompensaResponse> listarDoUsuario(Long usuarioId) {
        usuarioService.buscarEntidade(usuarioId);
        return recompensaRepository.findByVencedorIdOrderByDataConcessaoDesc(usuarioId)
                .stream().map(RecompensaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public RecompensaResponse buscar(Long id) {
        return RecompensaResponse.de(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Recompensa buscarEntidade(Long id) {
        return recompensaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recompensa", id));
    }

    public RecompensaResponse criar(RecompensaRequest req) {
        Grupo grupo = grupoService.buscarEntidade(req.grupoId());
        Recompensa r = new Recompensa();
        r.setTitulo(req.titulo());
        r.setDescricao(req.descricao());
        r.setGrupo(grupo);
        return RecompensaResponse.de(recompensaRepository.save(r));
    }

    public RecompensaResponse atualizar(Long id, RecompensaRequest req) {
        Recompensa r = buscarEntidade(id);
        if (r.getStatus() == StatusRecompensa.CONCEDIDA) {
            throw new RegraNegocioException("Recompensa já concedida não pode ser alterada.");
        }
        if (!r.getGrupo().getId().equals(req.grupoId())) {
            throw new RegraNegocioException("Não é permitido mover a recompensa para outro grupo.");
        }
        r.setTitulo(req.titulo());
        r.setDescricao(req.descricao());
        return RecompensaResponse.de(recompensaRepository.save(r));
    }

    public void excluir(Long id) {
        recompensaRepository.delete(buscarEntidade(id));
    }

    /**
     * FUNCIONALIDADE ESPECÍFICA DO PI: entrega a recompensa a quem mais pontuou na semana.
     * Regras:
     *  - a recompensa precisa estar DISPONIVEL;
     *  - precisa haver pelo menos uma atividade concluída na semana.
     * Desempate: mais atividades concluídas e, depois, ordem alfabética.
     */
    public RecompensaResponse concederAoDestaque(Long recompensaId, LocalDate semana) {
        Recompensa r = buscarEntidade(recompensaId);
        if (r.getStatus() == StatusRecompensa.CONCEDIDA) {
            throw new RegraNegocioException("Esta recompensa já foi concedida.");
        }
        RankingSemanalResponse ranking = rankingService.rankingSemanal(r.getGrupo().getId(), semana);
        if (ranking.ranking().isEmpty()) {
            throw new RegraNegocioException(
                    "Nenhuma atividade foi concluída na semana de " + ranking.inicioSemana()
                            + ". Não há destaque para receber a recompensa.");
        }
        RankingItem vencedor = ranking.ranking().get(0);
        Usuario usuario = usuarioService.buscarEntidade(vencedor.usuarioId());

        r.setVencedor(usuario);
        r.setStatus(StatusRecompensa.CONCEDIDA);
        r.setDataConcessao(LocalDate.now());
        r.setSemanaReferencia(ranking.inicioSemana());
        return RecompensaResponse.de(recompensaRepository.save(r));
    }
}
