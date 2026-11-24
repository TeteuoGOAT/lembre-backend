package com.lembre.service;

import com.lembre.dto.UsuarioRequest;
import com.lembre.dto.UsuarioResponse;
import com.lembre.entity.Grupo;
import com.lembre.entity.StatusAtividade;
import com.lembre.entity.Usuario;
import com.lembre.exception.RecursoNaoEncontradoException;
import com.lembre.exception.RegraNegocioException;
import com.lembre.repository.AtividadeRepository;
import com.lembre.repository.ConclusaoRepository;
import com.lembre.repository.RecompensaRepository;
import com.lembre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AtividadeRepository atividadeRepository;
    private final ConclusaoRepository conclusaoRepository;
    private final RecompensaRepository recompensaRepository;
    private final GrupoService grupoService;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Long grupoId) {
        List<Usuario> usuarios = (grupoId != null)
                ? usuarioRepository.findByGrupoIdOrderByNomeAsc(grupoId)
                : usuarioRepository.findAll();
        return usuarios.stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    public UsuarioResponse criar(UsuarioRequest req) {
        Grupo grupo = grupoService.buscarEntidade(req.grupoId());
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com o e-mail " + req.email());
        }
        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setPapel(req.papel());
        u.setGrupo(grupo);
        return UsuarioResponse.de(usuarioRepository.save(u));
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest req) {
        Usuario u = buscarEntidade(id);
        if (!u.getGrupo().getId().equals(req.grupoId())) {
            throw new RegraNegocioException("Não é permitido trocar o usuário de grupo.");
        }
        if (usuarioRepository.existsByEmailAndIdNot(req.email(), id)) {
            throw new RegraNegocioException("Já existe outro usuário com o e-mail " + req.email());
        }
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setPapel(req.papel());
        return UsuarioResponse.de(usuarioRepository.save(u));
    }

    /** REGRA DE NEGÓCIO: não excluir usuário com atividades pendentes ou histórico de pontuação. */
    public void excluir(Long id) {
        Usuario u = buscarEntidade(id);
        if (atividadeRepository.existsByResponsavelIdAndStatus(id, StatusAtividade.PENDENTE)) {
            throw new RegraNegocioException(
                    "O usuário possui atividades pendentes. Conclua ou reatribua antes de excluir.");
        }
        if (conclusaoRepository.existsByUsuarioId(id) || recompensaRepository.existsByVencedorId(id)) {
            throw new RegraNegocioException(
                    "O usuário possui histórico de conclusões/recompensas e não pode ser excluído.");
        }
        usuarioRepository.delete(u);
    }
}
