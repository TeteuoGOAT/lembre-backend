package com.lembre.service;

import com.lembre.dto.GrupoRequest;
import com.lembre.dto.GrupoResponse;
import com.lembre.entity.Grupo;
import com.lembre.exception.RecursoNaoEncontradoException;
import com.lembre.exception.RegraNegocioException;
import com.lembre.repository.AtividadeRepository;
import com.lembre.repository.GrupoRepository;
import com.lembre.repository.RecompensaRepository;
import com.lembre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AtividadeRepository atividadeRepository;
    private final RecompensaRepository recompensaRepository;

    @Transactional(readOnly = true)
    public List<GrupoResponse> listar() {
        return grupoRepository.findAll().stream().map(this::paraResposta).toList();
    }

    @Transactional(readOnly = true)
    public GrupoResponse buscar(Long id) {
        return paraResposta(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Grupo buscarEntidade(Long id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo", id));
    }

    public GrupoResponse criar(GrupoRequest req) {
        Grupo g = new Grupo();
        g.setNome(req.nome());
        g.setDescricao(req.descricao());
        g.setCodigoConvite(gerarCodigoUnico());
        return paraResposta(grupoRepository.save(g));
    }

    public GrupoResponse atualizar(Long id, GrupoRequest req) {
        Grupo g = buscarEntidade(id);
        g.setNome(req.nome());
        g.setDescricao(req.descricao());
        return paraResposta(grupoRepository.save(g));
    }

    /** REGRA DE NEGÓCIO: não é possível excluir um grupo que ainda possua dados vinculados. */
    public void excluir(Long id) {
        Grupo g = buscarEntidade(id);
        if (usuarioRepository.existsByGrupoId(id)
                || atividadeRepository.existsByGrupoId(id)
                || recompensaRepository.existsByGrupoId(id)) {
            throw new RegraNegocioException(
                    "Não é possível excluir o grupo '" + g.getNome()
                            + "' pois ele possui membros, atividades ou recompensas vinculados.");
        }
        grupoRepository.delete(g);
    }

    private GrupoResponse paraResposta(Grupo g) {
        return GrupoResponse.de(g, usuarioRepository.countByGrupoId(g.getId()));
    }

    private String gerarCodigoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (grupoRepository.existsByCodigoConvite(codigo));
        return codigo;
    }
}
