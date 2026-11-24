package com.lembre.service;

import com.lembre.dto.AtividadeRequest;
import com.lembre.dto.AtividadeResponse;
import com.lembre.dto.ConclusaoResponse;
import com.lembre.entity.Atividade;
import com.lembre.entity.Grupo;
import com.lembre.entity.RegistroConclusao;
import com.lembre.entity.StatusAtividade;
import com.lembre.entity.Usuario;
import com.lembre.exception.RecursoNaoEncontradoException;
import com.lembre.exception.RegraNegocioException;
import com.lembre.repository.AtividadeRepository;
import com.lembre.repository.ConclusaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final ConclusaoRepository conclusaoRepository;
    private final GrupoService grupoService;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<AtividadeResponse> listar(Long grupoId, StatusAtividade status) {
        List<Atividade> lista;
        if (grupoId != null && status != null) {
            lista = atividadeRepository.findByGrupoIdAndStatusOrderByDataLimiteAsc(grupoId, status);
        } else if (grupoId != null) {
            lista = atividadeRepository.findByGrupoId(grupoId);
        } else {
            lista = atividadeRepository.findAll();
        }
        return lista.stream().map(AtividadeResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponse> listarAtrasadas(Long grupoId) {
        grupoService.buscarEntidade(grupoId);
        return atividadeRepository
                .findByGrupoIdAndStatusAndDataLimiteBefore(grupoId, StatusAtividade.PENDENTE, LocalDate.now())
                .stream().map(AtividadeResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public AtividadeResponse buscar(Long id) {
        return AtividadeResponse.de(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Atividade buscarEntidade(Long id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atividade", id));
    }

    public AtividadeResponse criar(AtividadeRequest req) {
        Grupo grupo = grupoService.buscarEntidade(req.grupoId());
        // REGRA: prazo não pode estar no passado
        if (req.dataLimite() != null && req.dataLimite().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data limite não pode ser anterior à data de hoje.");
        }
        Atividade a = new Atividade();
        a.setGrupo(grupo);
        preencher(a, req);
        return AtividadeResponse.de(atividadeRepository.save(a));
    }

    public AtividadeResponse atualizar(Long id, AtividadeRequest req) {
        Atividade a = buscarEntidade(id);
        if (a.getStatus() == StatusAtividade.CONCLUIDA) {
            throw new RegraNegocioException("Atividade já concluída não pode ser alterada.");
        }
        if (!a.getGrupo().getId().equals(req.grupoId())) {
            throw new RegraNegocioException("Não é permitido mover a atividade para outro grupo.");
        }
        preencher(a, req);
        return AtividadeResponse.de(atividadeRepository.save(a));
    }

    /** REGRA: só atividades pendentes podem ser excluídas (preserva o histórico de pontos). */
    public void excluir(Long id) {
        Atividade a = buscarEntidade(id);
        if (a.getStatus() == StatusAtividade.CONCLUIDA || conclusaoRepository.existsByAtividadeId(id)) {
            throw new RegraNegocioException(
                    "Atividade concluída não pode ser excluída pois faz parte do histórico de pontuação.");
        }
        atividadeRepository.delete(a);
    }

    /**
     * FUNCIONALIDADE ESPECÍFICA DO PI: concluir atividade e pontuar.
     * Regras:
     *  - a atividade precisa estar PENDENTE;
     *  - o usuário precisa pertencer ao mesmo grupo da atividade;
     *  - se a atividade tem responsável definido, só ele pode concluí-la.
     */
    public ConclusaoResponse concluir(Long atividadeId, Long usuarioId) {
        Atividade a = buscarEntidade(atividadeId);
        if (a.getStatus() == StatusAtividade.CONCLUIDA) {
            throw new RegraNegocioException("Esta atividade já foi concluída.");
        }
        Usuario u = usuarioService.buscarEntidade(usuarioId);
        if (!u.getGrupo().getId().equals(a.getGrupo().getId())) {
            throw new RegraNegocioException("O usuário não pertence ao grupo desta atividade.");
        }
        if (a.getResponsavel() != null && !a.getResponsavel().getId().equals(u.getId())) {
            throw new RegraNegocioException(
                    "Esta atividade está sob responsabilidade de " + a.getResponsavel().getNome() + ".");
        }

        LocalDateTime agora = LocalDateTime.now();
        a.setStatus(StatusAtividade.CONCLUIDA);
        a.setDataConclusao(agora);
        if (a.getResponsavel() == null) {
            a.setResponsavel(u);
        }
        u.setPontos(u.getPontos() + a.getPontos());

        RegistroConclusao registro = new RegistroConclusao();
        registro.setAtividade(a);
        registro.setUsuario(u);
        registro.setPontosGanhos(a.getPontos());
        registro.setDataConclusao(agora);
        conclusaoRepository.save(registro);

        return new ConclusaoResponse(a.getId(), a.getTitulo(), u.getId(), u.getNome(),
                a.getPontos(), u.getPontos(), agora);
    }

    private void preencher(Atividade a, AtividadeRequest req) {
        a.setTitulo(req.titulo());
        a.setDescricao(req.descricao());
        a.setPontos(req.pontos());
        a.setDataLimite(req.dataLimite());
        if (req.responsavelId() != null) {
            Usuario resp = usuarioService.buscarEntidade(req.responsavelId());
            // REGRA: responsável precisa ser do mesmo grupo
            if (!resp.getGrupo().getId().equals(a.getGrupo().getId())) {
                throw new RegraNegocioException("O responsável deve pertencer ao mesmo grupo da atividade.");
            }
            a.setResponsavel(resp);
        } else {
            a.setResponsavel(null);
        }
    }
}
