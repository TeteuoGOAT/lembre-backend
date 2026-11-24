# Documentação da API — Lembre

Base URL: `http://localhost:8080` · Autenticação: **HTTP Basic** · Swagger UI: `/swagger-ui.html`

| Usuário | Senha | Perfil |
|---|---|---|
| `pai` | `admin123` | ADMIN |
| `filho` | `membro123` | MEMBRO |

> Usuários **apenas de teste**, configurados em memória.

## Permissões
| Operação | ADMIN | MEMBRO |
|---|:-:|:-:|
| Qualquer `GET` | ✅ | ✅ |
| `POST /atividades/{id}/concluir` | ✅ | ✅ |
| Demais `POST`, `PUT`, `DELETE` (incluindo conceder recompensa) | ✅ | ❌ (403) |
| Sem autenticação | ❌ (401) | ❌ (401) |

## Endpoints

### Grupos
| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/grupos` | Lista grupos | 200 |
| GET | `/grupos/{id}` | Busca grupo | 200 |
| POST | `/grupos` | Cria grupo (gera código de convite) | 201 |
| PUT | `/grupos/{id}` | Atualiza grupo | 200 |
| DELETE | `/grupos/{id}` | Exclui grupo (409 se houver vínculos) | 204 |
| GET | `/grupos/{id}/ranking-semanal?semana=AAAA-MM-DD` | Ranking de pontos da semana | 200 |
| GET | `/grupos/{id}/atividades-atrasadas` | Pendentes com prazo vencido | 200 |

### Usuários
| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/usuarios?grupoId=` | Lista (filtro opcional por grupo) | 200 |
| GET | `/usuarios/{id}` | Busca usuário | 200 |
| POST | `/usuarios` | Cria usuário | 201 |
| PUT | `/usuarios/{id}` | Atualiza usuário | 200 |
| DELETE | `/usuarios/{id}` | Exclui usuário | 204 |
| GET | `/usuarios/{id}/recompensas` | Recompensas conquistadas | 200 |

### Atividades
| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/atividades?grupoId=&status=` | Lista com filtros | 200 |
| GET | `/atividades/{id}` | Busca atividade | 200 |
| POST | `/atividades` | Cria atividade | 201 |
| PUT | `/atividades/{id}` | Atualiza (só se pendente) | 200 |
| DELETE | `/atividades/{id}` | Exclui (só se pendente) | 204 |
| POST | `/atividades/{id}/concluir` | **Conclui e pontua** | 200 |

### Recompensas
| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/recompensas?grupoId=` | Lista | 200 |
| GET | `/recompensas/{id}` | Busca | 200 |
| POST | `/recompensas` | Cria | 201 |
| PUT | `/recompensas/{id}` | Atualiza (só se disponível) | 200 |
| DELETE | `/recompensas/{id}` | Exclui | 204 |
| POST | `/recompensas/{id}/conceder?semana=` | **Entrega ao destaque da semana (ADMIN)** | 200 |

## Exemplos de corpo

**POST /usuarios**
```json
{ "nome": "Maria Silva", "email": "maria@lembre.com", "papel": "MEMBRO", "grupoId": 1 }
```
**POST /atividades**
```json
{ "titulo": "Passar aspirador", "descricao": "Sala e quartos", "pontos": 15,
  "dataLimite": "2030-12-31", "grupoId": 1, "responsavelId": 2 }
```
**POST /atividades/1/concluir**
```json
{ "usuarioId": 2 }
```
**POST /recompensas**
```json
{ "titulo": "Escolher o filme da sexta", "descricao": "O destaque escolhe", "grupoId": 1 }
```

## Regras de negócio (camada Service)
1. **Concluir atividade**: precisa estar `PENDENTE`; o usuário deve ser do mesmo grupo; se houver responsável, só ele conclui → senão **409**.
2. **Conceder recompensa**: só se `DISPONIVEL` e se houver ao menos uma conclusão na semana → senão **409**. Vence quem tem mais pontos (desempate: mais atividades, depois nome).
3. **Excluir grupo** com membros/atividades/recompensas → **409**.
4. **Excluir usuário** com atividades pendentes ou histórico → **409**.
5. **E-mail** de usuário único → **409**.
6. **Data limite** no passado ao criar atividade → **409**.
7. Atividade **concluída** não pode ser editada nem excluída → **409**.

## Consultas personalizadas (Spring Data JPA)
- `findByGrupoIdAndStatusOrderByDataLimiteAsc` — atividades por grupo e situação
- `findByGrupoIdAndStatusAndDataLimiteBefore` — atividades atrasadas
- `findByGrupoIdOrderByNomeAsc` — membros de um grupo
- `findByVencedorIdOrderByDataConcessaoDesc` — recompensas do usuário
- `@Query` JPQL `rankingDoPeriodo` — soma de pontos por usuário em um período

## Formato de erro
```json
{
  "timestamp": "2026-11-24T10:15:30",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos. Verifique os campos informados.",
  "caminho": "/atividades",
  "campos": { "titulo": "O título é obrigatório", "pontos": "A atividade deve valer no mínimo 1 ponto" }
}
```
| Código | Quando |
|---|---|
| 400 | Validação falhou / JSON ou data malformados |
| 401 | Sem credenciais ou credenciais inválidas |
| 403 | Autenticado, mas sem permissão (ex.: MEMBRO criando atividade) |
| 404 | Recurso não encontrado |
| 409 | Regra de negócio violada |
