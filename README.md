# Lembre — Backend (Projeto Integrador)

## Descrição
**Lembre** é um app de organização de tarefas domésticas com **gamificação**. Pessoas de um mesmo grupo (ex.: uma família) dividem atividades, ganham pontos ao concluí-las e competem de forma saudável. O **admin** (ex.: pai ou mãe) acompanha quem mais colaborou na semana e entrega uma **recompensa** ao destaque.

## Objetivo do backend
API REST que gerencia grupos, membros, atividades e recompensas, registra o histórico de conclusões, calcula o **ranking semanal** e permite ao admin conceder a recompensa ao vencedor da semana.

## Tecnologias
Java 17 · Spring Boot 3.3 · Spring Web · Spring Data JPA (Hibernate) · MySQL 8 · Spring Security (HTTP Basic) · Bean Validation · Lombok · SpringDoc/Swagger · JUnit 5 + Mockito · Postman

## Arquitetura
```
controller  →  service  →  repository  →  MySQL
(REST)        (regras)     (JPA)
        dto (entrada/saída) · exception (@RestControllerAdvice) · config (segurança, seed)
```

## Como executar
1. **Pré-requisitos:** JDK 17+, Maven 3.9+ e MySQL 8 (ou Docker).
2. **Banco de dados** (opção Docker):
   ```bash
   docker compose up -d
   ```
   Ou use um MySQL local; o schema `lembre` é criado automaticamente.
3. **Configuração:** por padrão `root`/`root` em `localhost:3306`. Para alterar, use variáveis de ambiente `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` ou edite `src/main/resources/application.properties`.
4. **Rodar:**
   ```bash
   mvn spring-boot:run
   ```
5. Acesse `http://localhost:8080/swagger-ui.html` (a documentação é pública; os endpoints exigem login).

Na primeira execução são criados dados de exemplo (grupo "Família Silva", 3 membros, 3 atividades e 1 recompensa).

**Usuários de teste (HTTP Basic):** `pai / admin123` (ADMIN) · `filho / membro123` (MEMBRO)

## Testes
- Unitários: `mvn test`
- API: importe `postman/projeto-integrador.postman_collection.json` no Postman e execute as pastas em ordem (1 a 8).

## Documentação
- [Modelo de dados](docs/modelo-dados.png)
- [Documentação dos endpoints](docs/documentacao-api.md)

## Integrantes
| Nome completo | ID acadêmico |
| --- | --- |
| Matheus dos Santos Silva | 272520 |
| Guilherme Cavalcanti de Oliveira | 272444 |
| Diogo Silva de Oliveira | 272430 |