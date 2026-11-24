package com.lembre.config;

import com.lembre.entity.*;
import com.lembre.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/** Popula o banco com dados de exemplo na primeira execução (facilita a demonstração). */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seed(GrupoRepository grupos, UsuarioRepository usuarios,
                           AtividadeRepository atividades, RecompensaRepository recompensas) {
        return args -> {
            if (grupos.count() > 0) return;

            Grupo g = new Grupo();
            g.setNome("Família Silva");
            g.setDescricao("Divisão das tarefas de casa");
            g.setCodigoConvite("SILVA001");
            g = grupos.save(g);

            Usuario pai = novoUsuario("Carlos Silva", "carlos@lembre.com", PapelNoGrupo.ADMIN, g);
            Usuario ana = novoUsuario("Ana Silva", "ana@lembre.com", PapelNoGrupo.MEMBRO, g);
            Usuario lucas = novoUsuario("Lucas Silva", "lucas@lembre.com", PapelNoGrupo.MEMBRO, g);
            usuarios.save(pai);
            usuarios.save(ana);
            usuarios.save(lucas);

            atividades.save(novaAtividade("Lavar a louça", "Jantar de hoje", 10, LocalDate.now().plusDays(1), g, ana));
            atividades.save(novaAtividade("Tirar o lixo", "Lixo reciclável e orgânico", 5, LocalDate.now().plusDays(2), g, lucas));
            atividades.save(novaAtividade("Limpar o quintal", "Varrer e lavar", 30, LocalDate.now().plusDays(5), g, null));

            Recompensa r = new Recompensa();
            r.setTitulo("Escolher o filme da sexta");
            r.setDescricao("O destaque da semana escolhe o filme da noite");
            r.setGrupo(g);
            recompensas.save(r);
        };
    }

    private Usuario novoUsuario(String nome, String email, PapelNoGrupo papel, Grupo g) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setPapel(papel);
        u.setGrupo(g);
        return u;
    }

    private Atividade novaAtividade(String titulo, String desc, int pontos, LocalDate limite, Grupo g, Usuario resp) {
        Atividade a = new Atividade();
        a.setTitulo(titulo);
        a.setDescricao(desc);
        a.setPontos(pontos);
        a.setDataLimite(limite);
        a.setGrupo(g);
        a.setResponsavel(resp);
        return a;
    }
}
